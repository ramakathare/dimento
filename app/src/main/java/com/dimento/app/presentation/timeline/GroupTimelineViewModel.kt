package com.dimento.app.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.usecase.CreateEventUseCase
import com.dimento.app.domain.usecase.DeleteEventUseCase
import com.dimento.app.domain.usecase.ForwardEventUseCase
import com.dimento.app.domain.usecase.GetGroupUseCase
import com.dimento.app.domain.usecase.ObserveGroupsUseCase
import com.dimento.app.domain.usecase.ObserveTimelineUseCase
import com.dimento.app.domain.usecase.SearchMemoriesUseCase
import com.dimento.app.domain.usecase.UpdateEventUseCase
import com.dimento.app.domain.util.EventTypeResolver
import com.dimento.app.presentation.model.TimelineItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.dimento.app.domain.model.SearchResult

data class OnScheduleNotification(
    val eventId: Long,
    val eventDateMillis: Long
)

class GroupTimelineViewModel(
    private val groupId: Long,
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeGroupsUseCase: ObserveGroupsUseCase,
    getGroupUseCase: GetGroupUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val forwardEventUseCase: ForwardEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val searchMemoriesUseCase: SearchMemoriesUseCase,
    private val eventTypeResolver: EventTypeResolver
) : ViewModel() {
    private val nowTicker = MutableStateFlow(System.currentTimeMillis())
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _selectedEventIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedEventIds: StateFlow<Set<Long>> = _selectedEventIds.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(SearchResult(emptyList(), emptyList()))
    val searchResults: StateFlow<SearchResult> = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    val group: StateFlow<MemoryGroup?> = observeGroupsUseCase().map { groups ->
        groups.find { it.id == groupId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allGroups: StateFlow<List<MemoryGroup>> = observeGroupsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timelineItems: StateFlow<List<TimelineItem>> = combine(
        observeTimelineUseCase(groupId),
        nowTicker
    ) { events, nowMillis ->
        val list = mutableListOf<TimelineItem>()
        var lastType: EventType? = null
        events.forEach { event ->
            val type = eventTypeResolver.resolve(event.eventDateMillis, nowMillis)
            if (type != lastType) {
                list += TimelineItem.Header(type)
                lastType = type
            }
            list += TimelineItem.EventRow(event = event, type = type)
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                nowTicker.value = System.currentTimeMillis()
            }
        }
    }

    fun addQuickEvent(text: String, eventDateMillis: Long = System.currentTimeMillis()) {
        if (text.isBlank()) return
        viewModelScope.launch {
            runCatching {
                createEventUseCase(
                    groupId = groupId,
                    text = text,
                    eventDateMillis = eventDateMillis,
                    recordedDateMillis = System.currentTimeMillis()
                )
            }.onSuccess { createResult ->
                if (createResult.eventType == EventType.FUTURE) {
                    _onScheduleNotification.tryEmit(OnScheduleNotification(createResult.eventId, eventDateMillis))
                }
            }.onFailure { _message.value = it.message }
        }
    }

    private val _onScheduleNotification = MutableSharedFlow<OnScheduleNotification>(extraBufferCapacity = 1)
    val onScheduleNotification: SharedFlow<OnScheduleNotification> = _onScheduleNotification.asSharedFlow()

    fun delete(eventId: Long) {
        viewModelScope.launch {
            deleteEventUseCase(eventId)
            _onCancelNotification.tryEmit(eventId)
        }
    }

    private val _onCancelNotification = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val onCancelNotification: SharedFlow<Long> = _onCancelNotification.asSharedFlow()

    fun forward(eventId: Long, destinationGroupId: Long) {
        viewModelScope.launch {
            runCatching {
                forwardEventUseCase(eventId, destinationGroupId, System.currentTimeMillis())
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    fun updateEvent(eventId: Long, text: String, eventDateMillis: Long) {
        viewModelScope.launch {
            runCatching {
                updateEventUseCase(eventId, text, eventDateMillis, voicePath = null)
                // Cancel old alarm; if new date is future, schedule new alarm
                _onCancelNotification.tryEmit(eventId)
                if (eventDateMillis > System.currentTimeMillis()) {
                    _onScheduleNotification.tryEmit(OnScheduleNotification(eventId, eventDateMillis))
                }
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    // --- Selection ---

    fun toggleSelection(eventId: Long) {
        val current = _selectedEventIds.value
        _selectedEventIds.value = if (eventId in current) {
            current - eventId
        } else {
            current + eventId
        }
    }

    fun clearSelection() {
        _selectedEventIds.value = emptySet()
    }

    fun enterSelectionMode(eventId: Long) {
        _selectedEventIds.value = setOf(eventId)
    }

    fun deleteSelectedEvents() {
        val ids = _selectedEventIds.value.toList()
        if (ids.isEmpty()) return
        clearSelection()
        viewModelScope.launch {
            ids.forEach { id ->
                runCatching { deleteEventUseCase(id) }
                _onCancelNotification.tryEmit(id)
            }
        }
    }

    fun findEvent(eventId: Long): MemoryEvent? {
        return timelineItems.value
            .filterIsInstance<TimelineItem.EventRow>()
            .firstOrNull { it.event.id == eventId }
            ?.event
    }

    fun startEdit(eventId: Long) {
        val event = findEvent(eventId) ?: return
        // The screen reads these state vars; this function signals which event to edit
    }

    fun consumeMessage() {
        _message.value = null
    }

    // --- Search ---

    fun onSearchQueryChange(value: String) {
        _searchQuery.value = value
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(180)
            _searchResults.value = searchMemoriesUseCase(value, groupId)
        }
    }

    class Factory(
        private val groupId: Long,
        private val observeTimelineUseCase: ObserveTimelineUseCase,
        private val observeGroupsUseCase: ObserveGroupsUseCase,
        private val getGroupUseCase: GetGroupUseCase,
        private val createEventUseCase: CreateEventUseCase,
        private val forwardEventUseCase: ForwardEventUseCase,
        private val deleteEventUseCase: DeleteEventUseCase,
        private val updateEventUseCase: UpdateEventUseCase,
        private val searchMemoriesUseCase: SearchMemoriesUseCase,
        private val eventTypeResolver: EventTypeResolver
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GroupTimelineViewModel(
                groupId = groupId,
                observeTimelineUseCase = observeTimelineUseCase,
                observeGroupsUseCase = observeGroupsUseCase,
                getGroupUseCase = getGroupUseCase,
                createEventUseCase = createEventUseCase,
                forwardEventUseCase = forwardEventUseCase,
                deleteEventUseCase = deleteEventUseCase,
                updateEventUseCase = updateEventUseCase,
                searchMemoriesUseCase = searchMemoriesUseCase,
                eventTypeResolver = eventTypeResolver
            ) as T
        }
    }
}

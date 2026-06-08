package com.dimento.app.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.usecase.CreateEventUseCase
import com.dimento.app.domain.usecase.DeleteEventUseCase
import com.dimento.app.domain.usecase.ForwardEventUseCase
import com.dimento.app.domain.usecase.GetGroupUseCase
import com.dimento.app.domain.usecase.MarkEventCompleteUseCase
import com.dimento.app.domain.usecase.ObserveGroupsUseCase
import com.dimento.app.domain.usecase.ObserveTimelineUseCase
import com.dimento.app.domain.util.EventTypeResolver
import com.dimento.app.presentation.model.TimelineItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupTimelineViewModel(
    private val groupId: Long,
    observeTimelineUseCase: ObserveTimelineUseCase,
    observeGroupsUseCase: ObserveGroupsUseCase,
    getGroupUseCase: GetGroupUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val forwardEventUseCase: ForwardEventUseCase,
    private val markEventCompleteUseCase: MarkEventCompleteUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val eventTypeResolver: EventTypeResolver
) : ViewModel() {
    private val nowTicker = MutableStateFlow(System.currentTimeMillis())
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val group: StateFlow<MemoryGroup?> = kotlinx.coroutines.flow.flow {
        emit(getGroupUseCase(groupId))
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
            }.onFailure { _message.value = it.message }
        }
    }

    fun markComplete(eventId: Long) {
        viewModelScope.launch {
            markEventCompleteUseCase(eventId, System.currentTimeMillis())
        }
    }

    fun delete(eventId: Long) {
        viewModelScope.launch {
            deleteEventUseCase(eventId)
        }
    }

    fun forward(eventId: Long, destinationGroupId: Long) {
        viewModelScope.launch {
            runCatching {
                forwardEventUseCase(eventId, destinationGroupId, System.currentTimeMillis())
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    class Factory(
        private val groupId: Long,
        private val observeTimelineUseCase: ObserveTimelineUseCase,
        private val observeGroupsUseCase: ObserveGroupsUseCase,
        private val getGroupUseCase: GetGroupUseCase,
        private val createEventUseCase: CreateEventUseCase,
        private val forwardEventUseCase: ForwardEventUseCase,
        private val markEventCompleteUseCase: MarkEventCompleteUseCase,
        private val deleteEventUseCase: DeleteEventUseCase,
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
                markEventCompleteUseCase = markEventCompleteUseCase,
                deleteEventUseCase = deleteEventUseCase,
                eventTypeResolver = eventTypeResolver
            ) as T
        }
    }
}

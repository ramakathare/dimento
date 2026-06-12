package com.dimento.app.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.EventType
import com.dimento.app.domain.usecase.CreateEventUseCase
import com.dimento.app.core.ServiceLocator
import com.dimento.app.core.ValidationConstants
import com.dimento.app.R
import com.dimento.app.presentation.timeline.OnScheduleNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventDraft(
    val text: String = "",
    val eventDateMillis: Long = System.currentTimeMillis(),
    val hasCustomDateTime: Boolean = false,
    val voicePath: String? = null,
    val sourceGroupId: Long? = null
)

class CreateEventSharedViewModel(
    private val createEventUseCase: CreateEventUseCase
) : ViewModel() {
    private val _draft = MutableStateFlow(EventDraft())
    val draft: StateFlow<EventDraft> = _draft.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _onScheduleNotification = MutableSharedFlow<OnScheduleNotification>(extraBufferCapacity = 1)
    val onScheduleNotification: SharedFlow<OnScheduleNotification> = _onScheduleNotification.asSharedFlow()

    fun updateText(text: String) {
        _draft.value = _draft.value.copy(text = text.take(ValidationConstants.MAX_EVENT_TEXT_LENGTH))
    }

    fun updateEventDateMillis(value: Long) {
        _draft.value = _draft.value.copy(
            eventDateMillis = value,
            hasCustomDateTime = true
        )
    }

    fun clearDateTime() {
        _draft.value = _draft.value.copy(
            eventDateMillis = System.currentTimeMillis(),
            hasCustomDateTime = false
        )
    }

    fun setSourceGroupId(groupId: Long?) {
        _draft.value = _draft.value.copy(sourceGroupId = groupId)
    }

    fun commit(groupId: Long, onDone: () -> Unit) {
        val snapshot = _draft.value
        if (snapshot.text.isBlank()) {
            val ctx = ServiceLocator.container.appContext
            _message.value = ctx.getString(R.string.memory_text_required)
            return
        }
        viewModelScope.launch {
            val eventDateMillis = if (snapshot.hasCustomDateTime) {
                snapshot.eventDateMillis
            } else {
                System.currentTimeMillis()
            }
            runCatching {
                createEventUseCase(
                    groupId = groupId,
                    text = snapshot.text,
                    eventDateMillis = eventDateMillis,
                    recordedDateMillis = System.currentTimeMillis(),
                    voicePath = snapshot.voicePath
                )
            }.onSuccess { createResult ->
                _draft.value = EventDraft(sourceGroupId = snapshot.sourceGroupId)
                if (createResult.eventType == EventType.FUTURE) {
                    _onScheduleNotification.tryEmit(OnScheduleNotification(createResult.eventId, eventDateMillis))
                }
                onDone()
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

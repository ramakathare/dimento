package com.dimento.app.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.usecase.CreateEventUseCase
import com.dimento.app.core.ServiceLocator
import com.dimento.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventDraft(
    val text: String = "",
    val eventDateMillis: Long = System.currentTimeMillis(),
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

    fun updateText(text: String) {
        _draft.value = _draft.value.copy(text = text.take(200))
    }

    fun updateEventDateMillis(value: Long) {
        _draft.value = _draft.value.copy(eventDateMillis = value)
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
            runCatching {
                createEventUseCase(
                    groupId = groupId,
                    text = snapshot.text,
                    eventDateMillis = snapshot.eventDateMillis,
                    recordedDateMillis = System.currentTimeMillis(),
                    voicePath = snapshot.voicePath
                )
            }.onSuccess {
                _draft.value = EventDraft(sourceGroupId = snapshot.sourceGroupId)
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

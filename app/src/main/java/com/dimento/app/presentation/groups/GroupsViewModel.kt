package com.dimento.app.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.usecase.CreateGroupUseCase
import com.dimento.app.domain.usecase.ObserveGroupSummariesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupsViewModel(
    observeGroupSummariesUseCase: ObserveGroupSummariesUseCase,
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {
    val groups: StateFlow<List<GroupSummary>> = observeGroupSummariesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun createGroup(name: String) {
        viewModelScope.launch {
            runCatching { createGroupUseCase(name) }
                .onSuccess { _message.value = "Group created" }
                .onFailure { _message.value = it.message }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

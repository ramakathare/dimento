package com.dimento.app.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.domain.usecase.CreateGroupUseCase
import com.dimento.app.domain.usecase.DeleteGroupUseCase
import com.dimento.app.domain.usecase.ObserveGroupSummariesUseCase
import com.dimento.app.domain.usecase.RenameGroupUseCase
import com.dimento.app.domain.usecase.SearchMemoriesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupsViewModel(
    observeGroupSummariesUseCase: ObserveGroupSummariesUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val searchMemoriesUseCase: SearchMemoriesUseCase
) : ViewModel() {
    val groups: StateFlow<List<GroupSummary>> = observeGroupSummariesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow(SearchResult(emptyList(), emptyList()))
    val results: StateFlow<SearchResult> = _results.asStateFlow()

    private var searchJob: Job? = null

    fun createGroup(name: String) {
        viewModelScope.launch {
            runCatching { createGroupUseCase(name) }
                .onSuccess { _message.value = "Group created" }
                .onFailure { _message.value = it.message }
        }
    }

    fun renameGroup(groupId: Long, name: String) {
        viewModelScope.launch {
            runCatching { renameGroupUseCase(groupId, name) }
                .onSuccess { _message.value = "Group updated" }
                .onFailure { _message.value = it.message }
        }
    }

    fun deleteGroup(groupId: Long) {
        viewModelScope.launch {
            runCatching { deleteGroupUseCase(groupId) }
                .onSuccess { _message.value = "Group deleted" }
                .onFailure { _message.value = it.message }
        }
    }

    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(180)
            _results.value = if (value.isBlank()) {
                SearchResult(emptyList(), emptyList())
            } else {
                searchMemoriesUseCase(value, null)
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

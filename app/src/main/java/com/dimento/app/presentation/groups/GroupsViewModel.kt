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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val results: StateFlow<SearchResult> = _query
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(SearchResult(emptyList(), emptyList()))
            else flowOf(searchMemoriesUseCase(q))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResult(emptyList(), emptyList()))

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun createGroup(name: String, icon: String?) {
        viewModelScope.launch {
            runCatching { createGroupUseCase(name, icon) }
                .onFailure { _message.value = it.message }
        }
    }

    fun renameGroup(groupId: Long, name: String, icon: String?) {
        viewModelScope.launch {
            runCatching { renameGroupUseCase(groupId, name, icon) }
                .onFailure { _message.value = it.message }
        }
    }

    fun deleteGroup(groupId: Long) {
        viewModelScope.launch {
            runCatching { deleteGroupUseCase(groupId) }
                .onFailure { _message.value = it.message }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

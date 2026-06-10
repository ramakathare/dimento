package com.dimento.app.presentation.groups

import kotlinx.coroutines.FlowPreview
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class GroupsViewModel(
    observeGroupSummariesUseCase: ObserveGroupSummariesUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val renameGroupUseCase: RenameGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val searchMemoriesUseCase: SearchMemoriesUseCase
) : ViewModel() {

    private val _pendingDeletions = MutableStateFlow<Set<Long>>(emptySet())
    private var undoJob: Job? = null

    private val _selectedGroupIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedGroupIds: StateFlow<Set<Long>> = _selectedGroupIds.asStateFlow()

    val groups: StateFlow<List<GroupSummary>> = observeGroupSummariesUseCase()
        .combine(_pendingDeletions) { list, pending ->
            list.filter { it.groupId !in pending }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

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

    fun toggleSelection(groupId: Long) {
        val current = _selectedGroupIds.value
        _selectedGroupIds.value = if (groupId in current) {
            current - groupId
        } else {
            current + groupId
        }
    }

    fun clearSelection() {
        _selectedGroupIds.value = emptySet()
    }

    fun enterSelectionMode(groupId: Long) {
        _selectedGroupIds.value = setOf(groupId)
    }

    fun createGroup(name: String, icon: String?, description: String?) {
        viewModelScope.launch {
            runCatching { createGroupUseCase(name, icon, description) }
                .onFailure { _message.value = it.message }
        }
    }

    fun renameGroup(groupId: Long, name: String, icon: String?, description: String?) {
        viewModelScope.launch {
            runCatching { renameGroupUseCase(groupId, name, icon, description) }
                .onFailure { _message.value = it.message }
        }
    }

    fun deleteSelectedGroups() {
        val idsToDelete = _selectedGroupIds.value
        if (idsToDelete.isEmpty()) return
        
        clearSelection()
        
        undoJob?.cancel()
        commitPendingDeletions()

        _pendingDeletions.value = idsToDelete
        _message.value = if (idsToDelete.size == 1) DELETE_SINGLE_MSG else "${idsToDelete.size}$DELETE_MULTI_MSG"

        undoJob = viewModelScope.launch {
            delay(5000)
            commitPendingDeletions()
        }
    }

    private fun commitPendingDeletions() {
        val idsToDelete = _pendingDeletions.value
        if (idsToDelete.isNotEmpty()) {
            val snapshot = idsToDelete.toSet()
            _pendingDeletions.value = emptySet()
            viewModelScope.launch {
                snapshot.forEach { id ->
                    runCatching { deleteGroupUseCase(id) }
                }
            }
        }
    }

    fun undoDelete() {
        undoJob?.cancel()
        _pendingDeletions.value = emptySet()
        _message.value = null
    }

    fun consumeMessage() {
        _message.value = null
    }

    override fun onCleared() {
        commitPendingDeletions()
        super.onCleared()
    }

    private companion object {
        const val DELETE_SINGLE_MSG = "Group deleted"
        const val DELETE_MULTI_MSG = " groups deleted"
    }
}

package com.dimento.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.domain.usecase.SearchMemoriesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val groupId: Long?,
    private val searchMemoriesUseCase: SearchMemoriesUseCase
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow(SearchResult(emptyList(), emptyList()))
    val results: StateFlow<SearchResult> = _results.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(value: String) {
        _query.value = value
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(180)
            _results.value = searchMemoriesUseCase(value, groupId)
        }
    }
}

package com.dimento.app.domain.model

data class SearchResult(
    val groups: List<MemoryGroup>,
    val matchedEvents: List<MatchedEvent>
) {
    data class MatchedEvent(
        val event: MemoryEvent,
        val groupName: String
    )
}

package com.dimento.domain.repository

import com.dimento.domain.model.Event

interface CsvExporter {
    suspend fun export(events: List<Event>): Result<String>
}

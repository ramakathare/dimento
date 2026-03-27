package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.CsvExportData
import com.dimento.app.domain.repository.MemoryRepository
import com.dimento.app.domain.util.EventTypeResolver

class ExportGroupEventsCsvUseCase(
    private val repository: MemoryRepository,
    private val eventTypeResolver: EventTypeResolver
) {
    suspend operator fun invoke(groupId: Long, nowMillis: Long): CsvExportData {
        val rows = repository.getEventsWithGroupName(groupId)
        val groupName = rows.firstOrNull()?.second ?: "group_$groupId"
        val header = "event_id,group_id,group_name,text,event_date,recorded_date,completed_date,type,voice_path"
        val body = rows.joinToString(separator = "\n") { (event, rowGroupName) ->
            val type = eventTypeResolver.resolve(event.eventDateMillis, nowMillis).name
            listOf(
                event.id.toString(),
                event.groupId.toString(),
                escape(rowGroupName),
                escape(event.text),
                escape(eventTypeResolver.formatCsvDate(event.eventDateMillis)),
                escape(eventTypeResolver.formatCsvDate(event.recordedDateMillis)),
                escape(event.completedDateMillis?.let(eventTypeResolver::formatCsvDate) ?: ""),
                type,
                escape(event.voicePath ?: "")
            ).joinToString(",")
        }
        val fileName = "dimento_${sanitize(groupName)}_$nowMillis.csv"
        return CsvExportData(fileName = fileName, content = "$header\n$body")
    }

    private fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    private fun sanitize(value: String): String {
        return value.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "group" }
    }
}

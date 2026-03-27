package com.dimento.app.domain.usecase

import com.dimento.app.domain.model.CsvExportData
import com.dimento.app.domain.repository.MemoryRepository
import com.dimento.app.domain.util.EventTypeResolver

class ExportEventsCsvUseCase(
    private val repository: MemoryRepository,
    private val eventTypeResolver: EventTypeResolver
) {
    suspend operator fun invoke(nowMillis: Long): CsvExportData {
        val rows = repository.getAllEventsWithGroupNames()
        val header = "event_id,group_id,group_name,text,event_date,recorded_date,completed_date,type,voice_path"
        val body = rows.joinToString(separator = "\n") { (event, groupName) ->
            val type = eventTypeResolver.resolve(event.eventDateMillis, nowMillis).name
            listOf(
                event.id.toString(),
                event.groupId.toString(),
                escape(groupName),
                escape(event.text),
                escape(eventTypeResolver.formatCsvDate(event.eventDateMillis)),
                escape(eventTypeResolver.formatCsvDate(event.recordedDateMillis)),
                escape(event.completedDateMillis?.let(eventTypeResolver::formatCsvDate) ?: ""),
                type,
                escape(event.voicePath ?: "")
            ).joinToString(",")
        }
        val fileName = "dimento_export_$nowMillis.csv"
        return CsvExportData(fileName = fileName, content = "$header\n$body")
    }

    private fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }
}

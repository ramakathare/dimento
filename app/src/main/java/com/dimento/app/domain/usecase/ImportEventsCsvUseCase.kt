package com.dimento.app.domain.usecase

import com.dimento.app.domain.repository.MemoryRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Imports events from CSV content with strict schema validation.
 * Wipes existing data before importing.
 * CSV format: event_id,group_id,group_name,text,event_date,recorded_date,completed_date,type,voice_path
 */
class ImportEventsCsvUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(csvContent: String) {
        val lines = csvContent.trim().split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        if (lines.isEmpty()) {
            throw ValidationException("CSV is empty")
        }

        // Validate header
        val header = lines.firstOrNull()
            ?: throw ValidationException("CSV has no header")
        
        if (header != CSV_HEADER) {
            throw ValidationException(
                "Invalid CSV header.\nExpected: $CSV_HEADER\nGot: $header"
            )
        }

        // Parse and validate all rows before deleting
        val eventsToInsert = mutableListOf<CsvRow>()
        val groupNames = mutableSetOf<String>()

        for ((lineIndex, line) in lines.drop(1).withIndex()) {
            val rowNumber = lineIndex + 2 // +2 because we skip header (line 1) and use 1-based counting
            try {
                val row = parseCsvRow(line, rowNumber)
                eventsToInsert.add(row)
                groupNames.add(row.groupName)
            } catch (e: ValidationException) {
                throw ValidationException("Row $rowNumber: ${e.message}")
            }
        }

        if (eventsToInsert.isEmpty()) {
            throw ValidationException("No valid events found in CSV")
        }

        // Delete all existing data
        repository.wipeAllData()

        // Create groups first
        val groupNameToId = mutableMapOf<String, Long>()
        for (groupName in groupNames) {
            val groupId = repository.createGroup(
                name = groupName,
                icon = null,
                description = null
            )
            groupNameToId[groupName] = groupId
        }

        // Create events with new group IDs
        for (row in eventsToInsert) {
            val newGroupId = groupNameToId[row.groupName]
                ?: throw ValidationException("Group '${row.groupName}' was not created properly")
            
            repository.createEvent(
                groupId = newGroupId,
                text = row.text,
                eventDateMillis = row.eventDateMillis,
                recordedDateMillis = row.recordedDateMillis,
                voicePath = row.voicePath,
                completedDateMillis = row.completedDateMillis
            )
        }
    }

    private fun parseCsvRow(line: String, rowNumber: Int): CsvRow {
        val fields = parseCsvLine(line)
        
        if (fields.size != 9) {
            throw ValidationException(
                "Expected 9 fields, got ${fields.size}"
            )
        }

        val eventId = fields[0].takeIf { it.isNotEmpty() }
            ?.toLongOrNull()
            ?: throw ValidationException("Invalid event_id: '${fields[0]}'")
        
        val groupId = fields[1].takeIf { it.isNotEmpty() }
            ?.toLongOrNull()
            ?: throw ValidationException("Invalid group_id: '${fields[1]}'")
        
        val groupName = fields[2].takeIf { it.isNotEmpty() }
            ?: throw ValidationException("group_name cannot be empty")
        
        if (groupName.length > 100) {
            throw ValidationException("group_name exceeds 100 characters")
        }

        val text = fields[3].takeIf { it.isNotEmpty() }
            ?: throw ValidationException("text cannot be empty")
        
        if (text.length > 5000) {
            throw ValidationException("text exceeds 5000 characters")
        }

        val eventDateMillis = parseDate(fields[4], "event_date")
        val recordedDateMillis = parseDate(fields[5], "recorded_date")
        val completedDateMillis = if (fields[6].isEmpty()) null else parseDate(fields[6], "completed_date")
        val type = fields[7]
        
        if (type.isEmpty() || !listOf("PAST", "TODAY", "FUTURE").contains(type)) {
            throw ValidationException("Invalid type: '$type' (must be PAST, TODAY, or FUTURE)")
        }

        // Validate voice_path (can be empty)
        val voicePath = fields[8].takeIf { it.isNotEmpty() }

        // Validate date consistency
        if (recordedDateMillis < 0 || eventDateMillis < 0) {
            throw ValidationException("Dates cannot be negative")
        }

        if (eventDateMillis > Long.MAX_VALUE / 2 || recordedDateMillis > Long.MAX_VALUE / 2) {
            throw ValidationException("Dates are unreasonably large")
        }

        return CsvRow(
            eventId = eventId,
            groupId = groupId,
            groupName = groupName,
            text = text,
            eventDateMillis = eventDateMillis,
            recordedDateMillis = recordedDateMillis,
            completedDateMillis = completedDateMillis,
            type = type,
            voicePath = voicePath
        )
    }

    private fun parseDate(dateStr: String, fieldName: String): Long {
        return try {
            // Parse ISO 8601 format (e.g., "2026-06-12T14:30:45" or "2026-06-12T14:30:45.123")
            val localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val zoneId = ZoneId.systemDefault()
            localDateTime.atZone(zoneId).toInstant().toEpochMilli()
        } catch (e: Exception) {
            throw ValidationException("Invalid $fieldName: '$dateStr' (expected ISO 8601 format like 2026-06-12T14:30:45)")
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]
            when {
                char == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        // Escaped quote
                        current.append('"')
                        i++ // Skip next quote
                    } else {
                        // Toggle quote mode
                        inQuotes = !inQuotes
                    }
                }
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
            i++
        }

        result.add(current.toString().trim())
        return result
    }

    private data class CsvRow(
        val eventId: Long,
        val groupId: Long,
        val groupName: String,
        val text: String,
        val eventDateMillis: Long,
        val recordedDateMillis: Long,
        val completedDateMillis: Long?,
        val type: String,
        val voicePath: String?
    )

    class ValidationException(message: String) : Exception(message)

    private companion object {
        const val CSV_HEADER = "event_id,group_id,group_name,text,event_date,recorded_date,completed_date,type,voice_path"
    }
}

package com.dimento.app.core

object CsvUtils {
    fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    /**
     * Splits CSV content into lines, respecting quoted fields that may contain newlines.
     */
    fun splitLines(content: String): List<String> {
        val lines = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false

        for (char in content) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == '\n' && !inQuotes -> {
                    lines.add(current.toString().trim())
                    current.clear()
                    continue
                }
            }
            current.append(char)
        }

        val last = current.toString().trim()
        if (last.isNotEmpty()) lines.add(last)
        return lines
    }

    fun parseLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]
            when {
                char == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                char == ',' && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
            i++
        }

        result.add(current.toString())
        return result
    }

    fun sanitizeFileName(value: String): String {
        return value.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "group" }
    }
}

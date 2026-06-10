package com.dimento.app.core

object CsvUtils {
    fun escape(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return "\"$escaped\""
    }

    fun sanitizeFileName(value: String): String {
        return value.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "group" }
    }
}

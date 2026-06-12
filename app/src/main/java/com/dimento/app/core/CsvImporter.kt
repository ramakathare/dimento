package com.dimento.app.core

import android.content.Context
import android.net.Uri

class CsvImporter(private val context: Context) {
    
    /**
     * Reads CSV content from a URI (typically from file picker).
     * Returns the raw CSV content as a string.
     */
    fun readCsvContent(uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().use { reader ->
                reader.readText()
            }
        } ?: throw IllegalStateException("Unable to read from URI: $uri")
    }
}

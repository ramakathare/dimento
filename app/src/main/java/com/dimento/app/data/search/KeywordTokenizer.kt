package com.dimento.app.data.search

class KeywordTokenizer {
    fun tokenize(text: String): Set<String> {
        return text
            .lowercase()
            .split(Regex("[^a-z0-9]+"))
            .map { it.trim() }
            .filter { it.length >= 2 }
            .toSet()
    }
}

package com.dimento.app.linkpreview

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Fetches Open Graph / meta preview data from a URL.
 * Runs network I/O on [Dispatchers.IO].
 */
object LinkPreviewFetcher {

    private const val TIMEOUT_MS = 5_000
    private val URL_PATTERN by lazy { Regex("https?://[^\\s\"'<>]+") }

    /** Extracts the first URL from text, if any. */
    fun extractUrl(text: String): String? {
        val match = URL_PATTERN.find(text.trim())
        return match?.value?.trimEnd('.', ',', ';', '!', '?')
    }

    /**
     * Fetches preview metadata from the given URL.
     * Returns a [LinkPreview] with whatever fields could be extracted, or null on failure.
     */
    suspend fun fetch(url: String): LinkPreview? = withContext(Dispatchers.IO) {
        try {
            val doc: Document = Jsoup.connect(url)
                .timeout(TIMEOUT_MS)
                .followRedirects(true)
                .userAgent("Mozilla/5.0 (Linux; Android 15) DiMento/1.0")
                .get()

            val title = og(doc, "title")
                ?: doc.select("meta[name=twitter:title]").attr("content").ifBlank { null }
                ?: doc.title().ifBlank { null }

            val imageUrl = og(doc, "image")
                ?: doc.select("meta[name=twitter:image]").attr("content").ifBlank { null }
                ?: doc.select("link[rel=image_src]").attr("href").ifBlank { null }

            if (title == null && imageUrl == null) return@withContext null

            LinkPreview(
                url = url,
                title = title?.take(200),
                imageUrl = imageUrl?.let { resolveRelativeUrl(url, it) }
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun og(doc: Document, property: String): String? {
        val selector = "meta[property=og:$property]"
        return doc.select(selector).attr("content").ifBlank { null }
    }

    private fun resolveRelativeUrl(baseUrl: String, maybeRelative: String): String {
        if (maybeRelative.startsWith("http://") || maybeRelative.startsWith("https://")) {
            return maybeRelative
        }
        return try {
            val base = java.net.URL(baseUrl)
            java.net.URL(base, maybeRelative).toString()
        } catch (_: Exception) {
            maybeRelative
        }
    }
}

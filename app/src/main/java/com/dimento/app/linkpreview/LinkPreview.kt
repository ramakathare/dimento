package com.dimento.app.linkpreview

import org.json.JSONObject

/**
 * Represents a link preview extracted from a URL.
 */
data class LinkPreview(
    val url: String,
    val title: String? = null,
    val imageUrl: String? = null
) {
    val hasContent: Boolean get() = title != null || imageUrl != null

    fun toJson(): String = JSONObject().apply {
        put("url", url)
        title?.let { put("title", it) }
        imageUrl?.let { put("imageUrl", it) }
    }.toString()

    companion object {
        fun fromJson(json: String?): LinkPreview? {
            if (json.isNullOrBlank()) return null
            return try {
                val obj = JSONObject(json)
                LinkPreview(
                    url = obj.getString("url"),
                    title = obj.optString("title", null),
                    imageUrl = obj.optString("imageUrl", null)
                )
            } catch (_: Exception) { null }
        }
    }
}

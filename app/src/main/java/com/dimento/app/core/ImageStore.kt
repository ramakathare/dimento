package com.dimento.app.core

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageStore {
    private const val IMAGES_DIR = "dimento_images"

    /**
     * Save the provided Uri to the app's files/images directory and return the absolute file path,
     * or null on failure.
     */
    fun saveUriToAppImages(context: Context, uri: Uri): String? {
        return try {
            val resolver: ContentResolver = context.contentResolver
            val imagesDir = File(context.filesDir, IMAGES_DIR)
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val fileName = "img_${System.currentTimeMillis()}.jpg"
            val outFile = File(imagesDir, fileName)

            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            outFile.absolutePath
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}

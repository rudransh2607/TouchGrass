package com.touchgrass.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-performance LRU icon cache and asynchronous loader.
 * Employs background worker decoding and in-memory caching to eliminate
 * main-thread UI hitches and provide instant 120fps scrolling.
 */
object AppIconLoader {

    private val iconCache = LruCache<String, ImageBitmap>(300)

    fun getCached(packageName: String): ImageBitmap? {
        return iconCache[packageName]
    }

    fun put(packageName: String, bitmap: ImageBitmap) {
        iconCache.put(packageName, bitmap)
    }

    suspend fun loadIcon(context: Context, packageName: String): ImageBitmap? {
        // Fast path: in-memory cache hit
        iconCache[packageName]?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(packageName)
                val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth.coerceIn(48, 144) else 96
                val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight.coerceIn(48, 144) else 96
                val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                val imageBitmap = bitmap.asImageBitmap()
                iconCache.put(packageName, imageBitmap)
                imageBitmap
            } catch (_: Exception) {
                null
            }
        }
    }
}


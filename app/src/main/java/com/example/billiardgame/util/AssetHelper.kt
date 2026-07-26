package com.example.billiardgame.util

import android.content.Context
import java.io.InputStream

object AssetHelper {
    fun loadBitmap(context: Context, fileName: String): android.graphics.Bitmap? {
        return try {
            val stream: InputStream = context.assets.open(fileName)
            android.graphics.BitmapFactory.decodeStream(stream).also { stream.close() }
        } catch (_: Exception) {
            null
        }
    }
}

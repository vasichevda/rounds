package com.rounds.imageloader

import android.graphics.drawable.Drawable
import android.widget.ImageView

interface ImageLoader {
    fun invalidateCache(url: String)
    fun clearCache()
    fun load(
        url: String,
        targetView: ImageView,
        placeholder: Drawable? = null
    )
}

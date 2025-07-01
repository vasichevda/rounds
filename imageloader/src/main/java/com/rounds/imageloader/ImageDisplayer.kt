package com.rounds.imageloader

import android.graphics.Bitmap
import android.widget.ImageView

interface ImageDisplayer {
    fun display(bitmap: Bitmap, targetView: ImageView)
}

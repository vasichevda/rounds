package com.rounds.imageloader

import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import androidx.transition.Fade
import androidx.transition.TransitionManager

class SimpleImageDisplayer : ImageDisplayer {
    override fun display(bitmap: Bitmap, targetView: ImageView) {
        val sceneRoot = targetView.parent as? ViewGroup ?: return
        TransitionManager.beginDelayedTransition(sceneRoot, Fade().apply { duration = 300 })
        targetView.setImageBitmap(bitmap)
    }
}

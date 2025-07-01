package com.rounds.imageloader

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set
import kotlin.coroutines.cancellation.CancellationException

class ImageLoaderImpl(
    private val downloader: Downloader,
    private val cacheManager: CacheManager,
    private val displayer: ImageDisplayer,
    private val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()) // Для обновления UI
) : ImageLoader {

    private val activeLoads = mutableMapOf<String, Job>()

    override fun load(url: String, targetView: ImageView, placeholder: Drawable?) {
        placeholder?.let { targetView.setImageDrawable(it) }
        activeLoads[targetView.toString()]?.cancel()
        val cachedBitmap = cacheManager.get(url)
        if (cachedBitmap != null) {
            displayer.display(cachedBitmap, targetView)
            return
        }
        val job = mainScope.launch {
            val maxRetries = 3
            var currentAttempt = 0

            try {
                var success = false
                while (!success && currentAttempt < maxRetries) {
                    try {
                        currentAttempt++
                        val downloadedBitmap = downloader.download(url)
                        if (downloadedBitmap != null) {
                            cacheManager.put(url, downloadedBitmap)
                            if (targetView.getTag(R.id.image_loader_url_tag) == url) {
                                displayer.display(downloadedBitmap, targetView)
                            }
                            success = true
                        } else {
                            delay(300)
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Log.w("ImageLoader", "Attempt $currentAttempt failed for $url", e)
                        delay(300)
                    }
                }
                if (!success) {
                    Log.e("ImageLoader", "Failed to load image after $maxRetries attempts: $url")
                    placeholder?.let { targetView.setImageDrawable(it) }
                }
            } finally {
                activeLoads.remove(targetView.toString())
            }
        }
        activeLoads[targetView.toString()] = job
        targetView.setTag(R.id.image_loader_url_tag, url)
    }

    override fun invalidateCache(url: String) {
        cacheManager.remove(url)
    }

    override fun clearCache() {
        cacheManager.clearAll()
    }
}

package com.rounds.imageloader

import android.content.Context

object ImageLoaderLocator {
    private lateinit var instance: ImageLoader

    fun initialize(context: Context) {
        if (::instance.isInitialized) return
        val diskCache = SimpleDiskCacheManager(context.applicationContext)
        val memoryCache = InMemoryLruCacheManager(diskCacheManager = diskCache)
        instance = ImageLoaderImpl(
            downloader = HttpUrlConnectionDownloader(),
            cacheManager = memoryCache,
            displayer = SimpleImageDisplayer()
        )
    }

    fun get(): ImageLoader {
        if (!::instance.isInitialized) {
            throw UninitializedPropertyAccessException("MyImageLoader must be initialized first in Application.onCreate()")
        }
        return instance
    }
}

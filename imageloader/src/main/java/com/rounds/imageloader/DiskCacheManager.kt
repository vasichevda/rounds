package com.rounds.imageloader

import android.graphics.Bitmap

interface DiskCacheManager {
    fun get(url: String): InMemoryLruCacheManager.CacheEntry?
    fun put(url: String, bitmap: Bitmap, timestamp: Long)
    fun remove(url: String)
    fun clearAll()
    fun clearExpired()
}

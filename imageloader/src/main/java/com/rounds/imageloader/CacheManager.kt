package com.rounds.imageloader

import android.graphics.Bitmap

interface CacheManager {
    fun get(url: String): Bitmap?
    fun put(url: String, bitmap: Bitmap)
    fun remove(url: String)
    fun clearAll()
    fun clearExpired()
}

package com.rounds.imageloader

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache

class InMemoryLruCacheManager(
    maxSizeKb: Int = defaultCacheSizeKb(),
    private val diskCacheManager: DiskCacheManager? = null
) : CacheManager {

    private val memoryCache: LruCache<String, CacheEntry> =
        object : LruCache<String, CacheEntry>(maxSizeKb) {
            override fun sizeOf(key: String, value: CacheEntry): Int {
                return value.bitmap.byteCount / 1024
            }
        }

    data class CacheEntry(val bitmap: Bitmap, val timestamp: Long)

    companion object {
        const val CACHE_VALIDITY_MS = 4 * 60 * 60 * 1000L

        private fun defaultCacheSizeKb(): Int {
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
            return maxMemory / 8
        }
    }

    override fun get(url: String): Bitmap? {
        val entry = synchronized(memoryCache) { memoryCache.get(url) }
        if (entry != null) {
            if (System.currentTimeMillis() - entry.timestamp < CACHE_VALIDITY_MS) {
                Log.d("CacheManager", "Mem cache HIT for $url")
                return entry.bitmap
            } else {
                Log.d("CacheManager", "Mem cache EXPIRED for $url")
                synchronized(memoryCache) { memoryCache.remove(url) }
            }
        }
        val diskBitmap = diskCacheManager?.get(url)
        if (diskBitmap != null) {
            Log.d("CacheManager", "Disk cache HIT for $url")
            putInMemory(url, diskBitmap.bitmap, diskBitmap.timestamp)
            return diskBitmap.bitmap
        }
        Log.d("CacheManager", "Cache MISS for $url")
        return null
    }

    override fun put(url: String, bitmap: Bitmap) {
        val timestamp = System.currentTimeMillis()
        putInMemory(url, bitmap, timestamp)
        diskCacheManager?.put(url, bitmap, timestamp)
    }

    private fun putInMemory(url: String, bitmap: Bitmap, timestamp: Long) {
        if (getMemoryCache(url) == null) {
            synchronized(memoryCache) {
                memoryCache.put(url, CacheEntry(bitmap, timestamp))
            }
            Log.d("CacheManager", "Put in mem cache: $url")
        }
    }

    private fun getMemoryCache(url: String): Bitmap? {
        return memoryCache.get(url)?.bitmap
    }

    override fun remove(url: String) {
        synchronized(memoryCache) { memoryCache.remove(url) }
        diskCacheManager?.remove(url)
        Log.d("CacheManager", "Removed from cache: $url")
    }

    override fun clearAll() {
        memoryCache.evictAll()
        diskCacheManager?.clearAll()
        Log.d("CacheManager", "Cleared all caches")
    }

    override fun clearExpired() {
        val snapshot = synchronized(memoryCache) { memoryCache.snapshot() }
        for ((key, entry) in snapshot) {
            if (System.currentTimeMillis() - entry.timestamp >= CACHE_VALIDITY_MS) {
                synchronized(memoryCache) { memoryCache.remove(key) }
            }
        }
        diskCacheManager?.clearExpired()
        Log.d("CacheManager", "Cleared expired mem cache entries")
    }
}

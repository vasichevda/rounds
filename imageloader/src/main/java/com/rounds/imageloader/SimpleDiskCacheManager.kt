package com.rounds.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.security.MessageDigest
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

class SimpleDiskCacheManager(
    context: Context
) : DiskCacheManager {
    private val cacheDir: File = File(context.cacheDir, "image_cache").apply { mkdirs() }
    private val journalFile = File(cacheDir, "cache_journal.json")
    private val journal = mutableMapOf<String, Long>()

    init {
        loadJournal()
    }

    private fun loadJournal() {
        if (journalFile.exists()) {
            try {
                val jsonString = journalFile.readText()
                val typeToken = object : TypeToken<Map<String, Long>>() {}.type
                val loadedJournal: Map<String, Long> = Gson().fromJson(jsonString, typeToken)
                journal.putAll(loadedJournal)
            } catch (e: Exception) {
                Log.e("DiskCache", "Failed to load journal", e)
                journal.clear()
                journalFile.delete()
            }
        }
    }

    private fun saveJournal() {
        try {
            val jsonString = Gson().toJson(journal)
            journalFile.writeText(jsonString)
        } catch (e: Exception) {
            Log.e("DiskCache", "Failed to save journal", e)
        }
    }


    private fun getFileForUrl(url: String): File {
        val fileName = url.toSHA256()
        return File(cacheDir, fileName)
    }


    private fun String.toSHA256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
        return bytes.fold("") { str, it -> str + "%02x".format(it) }
    }


    override fun get(url: String): InMemoryLruCacheManager.CacheEntry? {
        val file = getFileForUrl(url)
        val timestamp = journal[url]
        if (file.exists() && timestamp != null) {
            if (System.currentTimeMillis() - timestamp < InMemoryLruCacheManager.CACHE_VALIDITY_MS) {
                try {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        Log.d("DiskCache", "Disk HIT for $url")
                        return InMemoryLruCacheManager.CacheEntry(bitmap, timestamp)
                    } else {
                        remove(url)
                    }
                } catch (e: Exception) {
                    Log.e("DiskCache", "Error decoding bitmap from disk: $url", e)
                    remove(url)
                }
            } else {
                Log.d("DiskCache", "Disk EXPIRED for $url")
                remove(url)
            }
        }
        return null
    }

    override fun put(url: String, bitmap: Bitmap, timestamp: Long) {
        val file = getFileForUrl(url)
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out) // Или JPEG
                journal[url] = timestamp
                saveJournal()
                Log.d("DiskCache", "Put on disk: $url")
            }
        } catch (e: Exception) {
            Log.e("DiskCache", "Error saving bitmap to disk: $url", e)
        }
    }

    override fun remove(url: String) {
        val file = getFileForUrl(url)
        if (file.exists()) {
            file.delete()
        }
        if (journal.containsKey(url)) {
            journal.remove(url)
            saveJournal()
        }
        Log.d("DiskCache", "Removed from disk: $url")
    }

    override fun clearAll() {
        cacheDir.listFiles()?.forEach { it.delete() }
        journal.clear()
        saveJournal()
        Log.d("DiskCache", "Cleared all disk cache")
    }

    override fun clearExpired() {
        val currentTime = System.currentTimeMillis()
        val urlsToRemove = journal.filter { (_, timestamp) ->
            currentTime - timestamp >= InMemoryLruCacheManager.CACHE_VALIDITY_MS
        }.keys.toList()

        urlsToRemove.forEach { url ->
            remove(url)
        }
        Log.d("DiskCache", "Cleared expired disk cache entries")
    }
}

package com.rounds.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HttpUrlConnectionDownloader(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Downloader {
    override suspend fun download(url: String): Bitmap? {
        return withContext(dispatcher) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = TIMEOUT
                connection.readTimeout = TIMEOUT
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } else {
                    Log.w("Downloader", "Server returned HTTP ${connection.responseCode} for $url")
                    null
                }
            } catch (e: Exception) {
                Log.e("Downloader", "Error downloading image: $url", e)
                null
            }
        }
    }

    companion object {
        private const val TIMEOUT = 15_000
    }
}
package com.rounds.imageloader

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class ImageListFetcher(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun fetchImages(urlString: String): List<RemoteImage>? {
        return withContext(dispatcher) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = TIMEOUT
                connection.readTimeout = TIMEOUT
                connection.connect()
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { reader ->
                        val jsonString = reader.readText()
                        val result = mutableListOf<RemoteImage>()
                        val jsonArray = JSONArray(jsonString)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            result.add(
                                RemoteImage(
                                    id = jsonObject.optString(ID, UUID.randomUUID().toString()),
                                    imageUrl = jsonObject.getString(URL)
                                )
                            )
                        }
                        result
                    }
                } else {
                    Log.e(
                        "ImageListFetcher",
                        "Failed to fetch image list: ${connection.responseCode}"
                    )
                    null
                }
            } catch (e: Exception) {
                Log.e("ImageListFetcher", "Error fetching image list", e)
                null
            }
        }
    }

    companion object {
        private const val TIMEOUT = 10_000
        private const val ID = "id"
        private const val URL = "imageUrl"
    }
}

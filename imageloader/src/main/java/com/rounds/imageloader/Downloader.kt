package com.rounds.imageloader

import android.graphics.Bitmap

interface Downloader {
    suspend fun download(url: String): Bitmap?
}

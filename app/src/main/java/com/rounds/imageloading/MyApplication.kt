package com.rounds.imageloading

import android.app.Application
import com.rounds.imageloader.ImageLoaderLocator

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoaderLocator.initialize(applicationContext)
    }
}

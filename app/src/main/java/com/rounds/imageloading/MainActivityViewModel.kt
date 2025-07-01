package com.rounds.imageloading

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rounds.imageloader.ImageListFetcher
import com.rounds.imageloader.ImageLoader
import com.rounds.imageloader.ImageLoaderLocator
import com.rounds.imageloader.RemoteImage
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val imageListFetcher = ImageListFetcher()
    val images = MutableLiveData<List<RemoteImage>?>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String?>()

    init {
        ImageLoaderLocator.initialize(application.applicationContext)
    }

    fun loadImages() {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            val result =
                imageListFetcher.fetchImages("https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/image_list.json")
            if (result != null) {
                images.postValue(result)
            } else {
                error.postValue("Failed to load image list.")
            }
            isLoading.postValue(false)
        }
    }

    fun getImageLoader(): ImageLoader = ImageLoaderLocator.get()
}

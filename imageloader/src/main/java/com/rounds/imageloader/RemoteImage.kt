package com.rounds.imageloader

import com.google.gson.annotations.SerializedName

data class RemoteImage(
    @SerializedName("id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: String
)
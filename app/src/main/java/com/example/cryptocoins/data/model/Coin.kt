package com.example.cryptocoins.data.model

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

data class Coin(
    val name: String,
    val symbol: String,
    @SerializedName("is_new")
    val isNew: Boolean,
    @SerializedName("is_active")
    val isActive: Boolean,
    val type: String,
    var icon: Drawable
)
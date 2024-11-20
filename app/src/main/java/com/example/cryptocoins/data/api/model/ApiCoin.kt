package com.example.cryptocoins.data.api.model

import android.graphics.drawable.Drawable
import com.example.cryptocoins.data.local.entity.Coin
import com.google.gson.annotations.SerializedName

data class ApiCoin(
    val name: String,
    val symbol: String,
    @SerializedName("is_new")
    val isNew: Boolean,
    @SerializedName("is_active")
    val isActive: Boolean,
    val type: String,
    var icon: Int,
    var itemBackGroundColor: Int
)

fun ApiCoin.toCoinEntity(): Coin {
    return Coin(
        name = name,
        symbol = symbol,
        isNew = isNew,
        isActive = isActive,
        type = type,
        icon = icon,
        itemBackGroundColor = itemBackGroundColor
    )
}
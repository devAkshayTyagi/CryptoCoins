package com.example.cryptocoins.data.local.entity

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin")
data class Coin(
    val name: String,
    @PrimaryKey
    val symbol: String,
    @ColumnInfo(name = "is_new")
    val isNew: Boolean,
    @ColumnInfo(name ="is_active")
    val isActive: Boolean,
    val type: String,
    var icon: Int,
    @ColumnInfo(name = "bg_color")
    var itemBackGroundColor: Int
)

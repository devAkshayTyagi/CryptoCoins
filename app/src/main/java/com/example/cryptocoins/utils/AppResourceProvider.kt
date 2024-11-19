package com.example.cryptocoins.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

class AppResourceProvider (private val context: Context) {
    fun getString(id: Int): String {
        return context.getString(id)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable {
        return ContextCompat.getDrawable(context, id)!!
    }

    fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}
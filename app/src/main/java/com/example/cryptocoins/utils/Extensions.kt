package com.example.cryptocoins.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG)
        .show()
}
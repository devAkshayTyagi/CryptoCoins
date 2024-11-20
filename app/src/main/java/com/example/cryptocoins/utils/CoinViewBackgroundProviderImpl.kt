package com.example.cryptocoins.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.cryptocoins.R
import com.example.cryptocoins.data.local.entity.Coin

class CoinViewBackgroundProviderImpl(private val context: Context) : CoinViewBackgroundProvider {
    override fun setBackgroundColor(coin: Coin): Int {
        return if (coin.isActive) ContextCompat.getColor(context,R.color.white)
        else ContextCompat.getColor(context,R.color.light_gray)
    }
}
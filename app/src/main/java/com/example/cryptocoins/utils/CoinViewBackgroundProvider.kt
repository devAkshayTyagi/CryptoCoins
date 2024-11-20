package com.example.cryptocoins.utils

import com.example.cryptocoins.data.local.entity.Coin

interface CoinViewBackgroundProvider {
    fun setBackgroundColor(coin: Coin): Int
}
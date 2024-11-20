package com.example.cryptocoins.utils

import com.example.cryptocoins.data.local.entity.Coin

interface CoinIconProvider {
    fun setIcon(coin: Coin): Int
}
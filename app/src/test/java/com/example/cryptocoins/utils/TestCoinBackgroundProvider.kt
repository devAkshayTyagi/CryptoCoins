package com.example.cryptocoins.utils

import com.example.cryptocoins.data.local.entity.Coin

class TestCoinBackgroundProvider : CoinViewBackgroundProvider {
    override fun setBackgroundColor(coin: Coin): Int {
        return Int.MIN_VALUE
    }
}
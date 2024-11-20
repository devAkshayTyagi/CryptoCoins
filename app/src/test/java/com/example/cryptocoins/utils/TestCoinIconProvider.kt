package com.example.cryptocoins.utils

import com.example.cryptocoins.R
import com.example.cryptocoins.data.local.entity.Coin

class TestCoinIconProvider : CoinIconProvider {
    override fun setIcon(coin: Coin): Int {
        return  R.drawable.ic_launcher_background
    }
}
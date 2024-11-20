package com.example.cryptocoins.utils

import com.example.cryptocoins.R
import com.example.cryptocoins.data.local.entity.Coin

class CoinIconProviderImpl : CoinIconProvider {
    override fun setIcon(coin: Coin): Int {
        return when (coin.type) {
            AppConstants.COIN -> {
                if (coin.isActive)  R.drawable.ic_enabled_coin
                else R.drawable.ic_disabled_coin
            }
            else -> R.drawable.ic_token
        }
    }
}
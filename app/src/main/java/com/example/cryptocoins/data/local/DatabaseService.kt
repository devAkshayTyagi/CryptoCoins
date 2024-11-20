package com.example.cryptocoins.data.local

import com.example.cryptocoins.data.local.entity.Coin
import kotlinx.coroutines.flow.Flow

interface DatabaseService {

    fun getCoins(): Flow<List<Coin>>

    fun deleteAllAndInsertAll(coins: List<Coin>)

}
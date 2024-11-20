package com.example.cryptocoins.data.local

import com.example.cryptocoins.data.local.entity.Coin
import kotlinx.coroutines.flow.Flow

class AppDatabaseService constructor(private val appDatabase: AppDatabase) : DatabaseService {
    override fun getCoins(): Flow<List<Coin>> {
        return appDatabase.coinDao().getAll()
    }

    override fun deleteAllAndInsertAll(coins: List<Coin>) {
        appDatabase.coinDao().deleteAllAndInsertAll(coins)
    }
}
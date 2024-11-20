package com.example.cryptocoins.data.repository

import com.example.cryptocoins.data.api.INetworkService
import com.example.cryptocoins.data.api.model.toCoinEntity
import com.example.cryptocoins.data.local.DatabaseService
import com.example.cryptocoins.data.local.entity.Coin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val iNetworkService: INetworkService,
    private val databaseService: DatabaseService
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCoins(): Flow<List<Coin>> {
        return flow {
            emit(iNetworkService.getCoins())
        }.map {  apiCoins ->
            apiCoins.map { apiCoin -> apiCoin.toCoinEntity()}
        }.flatMapConcat { coins ->
            flow { emit(databaseService.deleteAllAndInsertAll((coins))) }
        }.flatMapConcat {
            databaseService.getCoins()
        }
    }

    fun getCoinsDirectlyFromDB(): Flow<List<Coin>> {
        return databaseService.getCoins()
    }
}
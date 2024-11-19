package com.example.cryptocoins.data.repository

import com.example.cryptocoins.data.api.INetworkService
import com.example.cryptocoins.data.model.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CoinRepository @Inject constructor(private val iNetworkService: INetworkService) {
    fun getCoins(): Flow<List<Coin>> {
        return flow {
            emit(iNetworkService.getCoins())
        }
    }
}
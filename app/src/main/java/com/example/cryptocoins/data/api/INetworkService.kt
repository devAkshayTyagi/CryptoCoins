package com.example.cryptocoins.data.api

import com.example.cryptocoins.data.model.Coin
import retrofit2.http.GET

interface INetworkService {
    @GET
    suspend fun getCoins() : List<Coin>
}
package com.example.cryptocoins.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.cryptocoins.data.local.entity.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coin")
    fun getAll(): Flow<List<Coin>>

    @Insert
    fun insertAll(articles: List<Coin>)

    @Query("DELETE FROM coin")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsertAll(articles: List<Coin>) {
        deleteAll()
        return insertAll(articles)
    }
}
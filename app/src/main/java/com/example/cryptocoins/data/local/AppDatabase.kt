package com.example.cryptocoins.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cryptocoins.data.local.dao.CoinDao
import com.example.cryptocoins.data.local.entity.Coin
import com.example.cryptocoins.utils.AppConstants


@Database(entities = [Coin::class], version = AppConstants.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
}
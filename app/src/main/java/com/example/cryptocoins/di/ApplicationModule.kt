package com.example.cryptocoins.di

import android.content.Context
import androidx.room.Room
import com.example.cryptocoins.data.api.INetworkService
import com.example.cryptocoins.data.local.AppDatabase
import com.example.cryptocoins.data.local.AppDatabaseService
import com.example.cryptocoins.data.local.DatabaseService
import com.example.cryptocoins.utils.AppConstants
import com.example.cryptocoins.utils.CoinIconProvider
import com.example.cryptocoins.utils.CoinIconProviderImpl
import com.example.cryptocoins.utils.CoinViewBackgroundProvider
import com.example.cryptocoins.utils.CoinViewBackgroundProviderImpl
import com.example.cryptocoins.utils.DefaultDispatcherProvider
import com.example.cryptocoins.utils.DispatcherProvider
import com.example.cryptocoins.utils.NetworkHelper
import com.example.cryptocoins.utils.NetworkHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @BaseUrl
    @Provides
    fun providesBaseUrl():String = AppConstants.BASE_URL

    @Provides
    @Singleton
    fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().build()
    }

    @Provides
    @Singleton
    fun providesNetworkService(
        @BaseUrl baseUrl : String,
        okHttpClient : OkHttpClient,
        gsonConverterFactory: GsonConverterFactory ) : INetworkService {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(INetworkService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelperImpl(context)
    }

    @DatabaseName
    @Provides
    fun provideDatabaseName(): String = AppConstants.DATABASE_NAME

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        ).build()
    }

    @Provides
    @Singleton
    fun provideDatabaseService(appDatabase: AppDatabase): DatabaseService {
        return AppDatabaseService(appDatabase)
    }

    @Provides
    @Singleton
    fun providesIconProvider() : CoinIconProvider {
        return CoinIconProviderImpl()
    }

    @Provides
    @Singleton
    fun providesBackGroundProvider(@ApplicationContext context: Context) : CoinViewBackgroundProvider {
        return CoinViewBackgroundProviderImpl(context)
    }

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatcherProvider()

}
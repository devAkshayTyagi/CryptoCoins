package com.example.cryptocoins.di

import android.content.Context
import com.example.cryptocoins.data.api.INetworkService
import com.example.cryptocoins.utils.AppConstants
import com.example.cryptocoins.utils.AppResourceProvider
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
    fun provideAppResourceProvider(@ApplicationContext context: Context): AppResourceProvider {
        return AppResourceProvider(context)
    }

}
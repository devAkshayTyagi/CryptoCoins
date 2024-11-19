package com.example.cryptocoins.di

import com.example.cryptocoins.ui.coins.CoinsAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @ActivityScoped
    @Provides
    fun providesCoinsAdapter() = CoinsAdapter(ArrayList())

}
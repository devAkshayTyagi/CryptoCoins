package com.example.cryptocoins.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {

    val main: CoroutineDispatcher

    val mainImmediate: CoroutineDispatcher

    val io: CoroutineDispatcher

    val default: CoroutineDispatcher

}

class DefaultDispatcherProvider : DispatcherProvider {

    override val main: CoroutineDispatcher
        get() = Dispatchers.Main

    override val mainImmediate: CoroutineDispatcher
        get() = Dispatchers.Main.immediate

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO

    override val default: CoroutineDispatcher
        get() = Dispatchers.Default

}
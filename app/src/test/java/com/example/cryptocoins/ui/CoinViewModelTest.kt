package com.example.cryptocoins.ui

import app.cash.turbine.test
import com.example.cryptocoins.data.local.entity.Coin
import com.example.cryptocoins.data.repository.CoinRepository
import com.example.cryptocoins.ui.coins.CoinViewModel
import com.example.cryptocoins.utils.CoinIconProvider
import com.example.cryptocoins.utils.CoinViewBackgroundProvider
import com.example.cryptocoins.utils.DispatcherProvider
import com.example.cryptocoins.utils.NetworkHelper
import com.example.cryptocoins.utils.TestCoinBackgroundProvider
import com.example.cryptocoins.utils.TestCoinIconProvider
import com.example.cryptocoins.utils.TestDispatcherProvider
import com.example.cryptocoins.utils.TestNetworkHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.times


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CoinViewModelTest {

    @Mock
    private lateinit var coinRepository: CoinRepository

    private lateinit var dispatcherProvider: DispatcherProvider

    private lateinit var networkHelper: NetworkHelper

    private lateinit var iconProvider: CoinIconProvider

    private lateinit var iconBackgroundProvider: CoinViewBackgroundProvider


    @Before
    fun setUp() {
        dispatcherProvider = TestDispatcherProvider()
        networkHelper = TestNetworkHelper()
        iconProvider = TestCoinIconProvider()
        iconBackgroundProvider = TestCoinBackgroundProvider()
    }

    @Test
    fun fetchCoins_whenRepositoryResponseSuccess_shouldSetSuccessUiState() {
        runTest {
            doReturn(flowOf(emptyList<Coin>()))
                .`when`(coinRepository).getCoins()

            val viewModel = CoinViewModel(
                coinRepository,
                networkHelper,
                iconProvider,
                iconBackgroundProvider,
                dispatcherProvider
            )
            viewModel.uiStateCoins.test {
                assertEquals(UiState.Success(emptyList<Coin>()), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(coinRepository, times(1)).getCoins()
        }
    }

    @Test
    fun fetchCoins_whenRepositoryResponseError_shouldSetErrorUiState() {
        runTest {
            val errorMessage = "Error Message For You"
            doReturn(flow<List<Coin>> {
                throw IllegalStateException(errorMessage)
            }).`when`(coinRepository).getCoins()

            val viewModel = CoinViewModel(
                coinRepository,
                networkHelper,
                iconProvider,
                iconBackgroundProvider,
                dispatcherProvider
            )
            viewModel.uiStateCoins.test {
                assertEquals(
                    UiState.Error(IllegalStateException(errorMessage).toString()),
                    awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify(coinRepository, times(1)).getCoins()
        }
    }

    @Test
    fun fetchCoinsFromDB_whenRepositoryResponseSuccess_shouldSetSuccessUiState() {
        runTest {
            doReturn(flowOf(emptyList<Coin>()))
                .`when`(coinRepository).getCoinsDirectlyFromDB()

            val viewModel = CoinViewModel(
                coinRepository,
                networkHelper,
                iconProvider,
                iconBackgroundProvider,
                dispatcherProvider
            )
            viewModel.fetchCoinsFromDB()

            advanceUntilIdle()

            viewModel.uiStateCoins.test {
                assertEquals(UiState.Success(emptyList<Coin>()), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify(coinRepository, times(1)).getCoinsDirectlyFromDB()
        }
    }
}


package com.example.cryptocoins.ui.coins

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocoins.R
import com.example.cryptocoins.data.model.Coin
import com.example.cryptocoins.data.repository.CoinRepository
import com.example.cryptocoins.ui.UiState
import com.example.cryptocoins.utils.AppConstants
import com.example.cryptocoins.utils.AppResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(private val coinRepository: CoinRepository,private val resourceProvider: AppResourceProvider) : ViewModel() {

    private var _uiStateCoins = MutableStateFlow<UiState<List<Coin>>>(UiState.Loading)
    val uiStateCoins: StateFlow<UiState<List<Coin>>> = _uiStateCoins

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            coinRepository.getCoins()
                .flowOn(Dispatchers.IO)
                .catch {
                    _uiStateCoins.value = UiState.Error(it.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(icon = getCoinIcon(coin))
                    }
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                }
        }
    }

    private fun getCoinIcon(coin: Coin): Drawable {
        return when (coin.type) {
            AppConstants.COIN -> {
                if (coin.isActive) resourceProvider.getDrawable(R.drawable.ic_enabled_coin)
                else resourceProvider.getDrawable(R.drawable.ic_disabled_coin)
            }
            else -> resourceProvider.getDrawable(R.drawable.ic_token)
        }
    }

}
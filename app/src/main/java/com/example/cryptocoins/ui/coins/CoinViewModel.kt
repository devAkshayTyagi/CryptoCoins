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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(private val coinRepository: CoinRepository,private val resourceProvider: AppResourceProvider) : ViewModel() {

    private var _uiStateCoins = MutableStateFlow<UiState<List<Coin>>>(UiState.Loading)
    val uiStateCoins: StateFlow<UiState<List<Coin>>> = _uiStateCoins

    private var coinList = listOf<Coin>()

    fun fetchCoins() {
        viewModelScope.launch {
            coinRepository.getCoins()
                .flowOn(Dispatchers.IO)
                .catch {
                    _uiStateCoins.value = UiState.Error(it.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(icon = setCoinIcon(coin), itemBackGroundColor = setItemBackGroundColor(coin))
                    }
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                    coinList = it
                }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun setUpSearchStateFlow(searchFlow: StateFlow<String>) {
        viewModelScope.launch {
            searchFlow.debounce(AppConstants.DEBOUNCE_TIME).filter { query ->
                if (query.isEmpty()) {
                    _uiStateCoins.value = UiState.Success(coinList)
                    return@filter false
                } else {
                    return@filter true
                }
            }.distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        val filteredList =  coinList.filter { coin ->
                            coin.name.contains(query, ignoreCase = true) ||
                                    coin.symbol.contains(query, ignoreCase = true)
                        }
                        emit(filteredList)
                    }
            }.flowOn(Dispatchers.Default).collect {
                    _uiStateCoins.value = UiState.Success(it)
            }
        }
    }

    fun fetchFilteredCoins(filterByList: List<String>) {
        viewModelScope.launch(Dispatchers.Default) {
            val filteredList = coinList.filter { coin ->
                filterByList.all { filter ->
                    when (filter) {
                        AppConstants.ACTIVE_COINS -> coin.isActive
                        AppConstants.IN_ACTIVE_COINS -> coin.isActive.not()
                        AppConstants.ONLY_COINS -> coin.type == AppConstants.COIN
                        AppConstants.ONLY_TOKENS -> coin.type == AppConstants.TOKEN
                        AppConstants.NEW_COINS -> coin.isNew
                        else -> true // Default to include if filter is unknown
                    }
                }
            }
            _uiStateCoins.value = UiState.Success(filteredList)
        }
    }

    private fun setCoinIcon(coin: Coin): Drawable {
        return when (coin.type) {
            AppConstants.COIN -> {
                if (coin.isActive) resourceProvider.getDrawable(R.drawable.ic_enabled_coin)
                else resourceProvider.getDrawable(R.drawable.ic_disabled_coin)
            }
            else -> resourceProvider.getDrawable(R.drawable.ic_token)
        }
    }

    private fun setItemBackGroundColor(coin: Coin): Int {
        return if (coin.isActive) {
            resourceProvider.getColor(R.color.white)
        }else resourceProvider.getColor(R.color.light_gray)
    }
}
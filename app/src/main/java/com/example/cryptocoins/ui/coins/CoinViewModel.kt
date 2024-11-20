package com.example.cryptocoins.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocoins.R
import com.example.cryptocoins.data.local.entity.Coin
import com.example.cryptocoins.data.repository.CoinRepository
import com.example.cryptocoins.ui.UiState
import com.example.cryptocoins.utils.AppConstants
import com.example.cryptocoins.utils.AppResourceProvider
import com.example.cryptocoins.utils.NetworkHelper
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
import kotlin.text.contains
import kotlin.text.isEmpty

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val resourceProvider: AppResourceProvider,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private var _uiStateCoins = MutableStateFlow<UiState<List<Coin>>>(UiState.Loading)
    val uiStateCoins: StateFlow<UiState<List<Coin>>> = _uiStateCoins

    private var coinList = listOf<Coin>()
    private val searchQuery = MutableStateFlow("")
    private fun checkInternetConnection(): Boolean = networkHelper.isNetworkConnected()

    init {
        if (checkInternetConnection()) {
            fetchCoins()
        } else {
            fetchCoinsFromDB()
        }
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            coinRepository.getCoins()
                .flowOn(Dispatchers.IO)
                .catch {
                    _uiStateCoins.value = UiState.Error(it.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(
                            icon = setCoinIconRes(coin),
                            itemBackGroundColor = setItemBackGroundColor(coin)
                        )
                    }
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                    coinList = it
                }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun applyFiltersAndSearch(filterByList: List<String>, query: String) {
        searchQuery.value = query
        viewModelScope.launch(Dispatchers.Default) {
            //Filter on base of chip selected
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

            //Search on the filter list
            searchQuery.debounce(AppConstants.DEBOUNCE_TIME).filter { query ->
                if (query.isEmpty()) {
                    _uiStateCoins.value = UiState.Success(filteredList)
                    return@filter false
                } else {
                    return@filter true
                }
            }.distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        val searchList = filteredList.filter { coin ->
                            coin.name.contains(query, ignoreCase = true) ||
                                    coin.symbol.contains(query, ignoreCase = true)
                        }
                        emit(searchList)
                    }
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                }
        }
    }

    private fun fetchCoinsFromDB() {
        viewModelScope.launch {
            coinRepository.getArticlesDirectlyFromDB()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _uiStateCoins.value = UiState.Error(e.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(
                            icon = setCoinIconRes(coin),
                            itemBackGroundColor = setItemBackGroundColor(coin)
                        )
                    }
                }
                .collect {
                    _uiStateCoins.value = UiState.Success(it)
                    coinList = it
                }
        }
    }

    private fun setCoinIconRes(coin: Coin): Int {
        return when (coin.type) {
            AppConstants.COIN -> {
                if (coin.isActive) R.drawable.ic_enabled_coin
                else R.drawable.ic_disabled_coin
            }

            else -> R.drawable.ic_token
        }
    }

    private fun setItemBackGroundColor(coin: Coin): Int {
        return if (coin.isActive) {
            resourceProvider.getColor(R.color.white)
        } else resourceProvider.getColor(R.color.light_gray)
    }
}
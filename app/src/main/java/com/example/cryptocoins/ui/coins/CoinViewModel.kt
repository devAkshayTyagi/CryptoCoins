package com.example.cryptocoins.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocoins.data.local.entity.Coin
import com.example.cryptocoins.data.repository.CoinRepository
import com.example.cryptocoins.ui.UiState
import com.example.cryptocoins.utils.AppConstants
import com.example.cryptocoins.utils.CoinIconProvider
import com.example.cryptocoins.utils.CoinViewBackgroundProvider
import com.example.cryptocoins.utils.DispatcherProvider
import com.example.cryptocoins.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
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
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.text.contains
import kotlin.text.isEmpty

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    private val networkHelper: NetworkHelper,
    private val iconProvider: CoinIconProvider,
    private val backgroundProvider: CoinViewBackgroundProvider,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private var _uiStateCoins = MutableStateFlow<UiState<List<Coin>>>(UiState.Loading)
    val uiStateCoins: StateFlow<UiState<List<Coin>>> = _uiStateCoins

    private var coinList = listOf<Coin>()
    private val searchQuery = MutableStateFlow("")
   // private var filteredList: List<Coin> = coinList
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
                .flowOn(dispatcherProvider.io)
                .catch {
                    _uiStateCoins.value = UiState.Error(it.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(
                            icon = iconProvider.setIcon(coin),
                            itemBackGroundColor = backgroundProvider.setBackgroundColor(coin)
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
        viewModelScope.launch(dispatcherProvider.mainImmediate) {
            searchQuery.value = query
            withContext(dispatcherProvider.default){
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
    }

     fun fetchCoinsFromDB() {
        viewModelScope.launch(dispatcherProvider.mainImmediate) {
            coinRepository.getCoinsDirectlyFromDB()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiStateCoins.value = UiState.Error(e.toString())
                }.map { coins ->
                    coins.map { coin ->
                        coin.copy(
                            icon = iconProvider.setIcon(coin),
                            itemBackGroundColor = backgroundProvider.setBackgroundColor(coin)
                        )
                    }
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                    coinList = it
                }
        }
    }
}
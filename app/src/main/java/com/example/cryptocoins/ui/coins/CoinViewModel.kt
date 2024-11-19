package com.example.cryptocoins.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocoins.data.model.Coin
import com.example.cryptocoins.data.repository.CoinRepository
import com.example.cryptocoins.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(private val coinRepository: CoinRepository) : ViewModel() {

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
                }.collect {
                    _uiStateCoins.value = UiState.Success(it)
                }
        }
    }

}
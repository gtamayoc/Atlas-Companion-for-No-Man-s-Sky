package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val discoveries: List<Discovery>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: DiscoveryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Initialize mock data to verify UI functionality
        viewModelScope.launch {
            repository.syncWithMockData()
            loadDiscoveries()
        }
    }

    private fun loadDiscoveries() {
        viewModelScope.launch {
            repository.getAllDiscoveries()
                .onStart { _uiState.value = HomeUiState.Loading }
                .catch { error -> _uiState.value = HomeUiState.Error(error.message ?: "Unknown error") }
                .collect { discoveries ->
                    _uiState.value = HomeUiState.Success(discoveries)
                }
        }
    }
}

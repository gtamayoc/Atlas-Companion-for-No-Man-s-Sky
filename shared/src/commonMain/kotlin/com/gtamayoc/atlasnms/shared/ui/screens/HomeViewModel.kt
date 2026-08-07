package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val discoveries: List<Discovery>,
        val filteredDiscoveries: List<Discovery>,
        val selectedType: DiscoveryType?,
        val searchQuery: String
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: DiscoveryRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow<DiscoveryType?>(null)
    val selectedType: StateFlow<DiscoveryType?> = _selectedType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllDiscoveries(),
        _selectedType,
        _searchQuery
    ) { discoveries, typeFilter, query ->
        val filtered = discoveries.filter { d ->
            val matchesType = typeFilter == null || d.type == typeFilter
            val matchesQuery = query.isBlank() || 
                d.name.contains(query, ignoreCase = true) ||
                d.systemName.contains(query, ignoreCase = true) ||
                d.galaxy.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
        HomeUiState.Success(
            discoveries = discoveries,
            filteredDiscoveries = filtered,
            selectedType = typeFilter,
            searchQuery = query
        ) as HomeUiState
    }
    .catch { e ->
        emit(HomeUiState.Error(e.message ?: "Error al cargar la bitácora"))
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun setTypeFilter(type: DiscoveryType?) {
        _selectedType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearFilters() {
        _selectedType.value = null
        _searchQuery.value = ""
    }
}

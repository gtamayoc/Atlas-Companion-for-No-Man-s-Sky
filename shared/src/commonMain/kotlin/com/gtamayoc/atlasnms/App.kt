package com.gtamayoc.atlasnms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.screens.HomeScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeViewModel

@Composable
fun App(repository: DiscoveryRepository) {
    // Create the ViewModel using a custom factory to inject the repository
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
    
    val viewModel: HomeViewModel = viewModel(factory = factory)
    
    HomeScreen(
        viewModel = viewModel,
        onFabClick = { /* TODO: Launch capture/OCR flow */ }
    )
}
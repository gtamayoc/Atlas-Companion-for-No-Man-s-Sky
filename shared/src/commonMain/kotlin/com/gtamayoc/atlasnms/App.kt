package com.gtamayoc.atlasnms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.screens.ExploreScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeViewModel
import com.gtamayoc.atlasnms.shared.ui.screens.ScanScreen
import com.gtamayoc.atlasnms.shared.ui.screens.SettingsScreen

@Composable
fun App(repository: DiscoveryRepository) {
    // Factory para inyectar el repositorio en HomeViewModel
    val factory = remember(repository) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
    
    val viewModel: HomeViewModel = viewModel(factory = factory)
    
    // Estado global de navegación para la barra inferior (Descubrimientos, Explorar, Ajustes, Analizar)
    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERIES) }

    when (currentScreen) {
        AppScreen.DISCOVERIES -> {
            HomeScreen(
                viewModel = viewModel,
                currentScreen = currentScreen,
                onScreenSelected = { newScreen -> currentScreen = newScreen },
                onFabClick = { currentScreen = AppScreen.SCAN }
            )
        }
        AppScreen.EXPLORE -> {
            ExploreScreen(
                viewModel = viewModel,
                currentScreen = currentScreen,
                onScreenSelected = { newScreen -> currentScreen = newScreen },
                onFabClick = { currentScreen = AppScreen.SCAN }
            )
        }
        AppScreen.SETTINGS -> {
            SettingsScreen(
                currentScreen = currentScreen,
                onScreenSelected = { newScreen -> currentScreen = newScreen },
                onFabClick = { currentScreen = AppScreen.SCAN }
            )
        }
        AppScreen.SCAN -> {
            ScanScreen(
                repository = repository,
                currentScreen = currentScreen,
                onScreenSelected = { newScreen -> currentScreen = newScreen },
                onDiscoverySaved = {
                    currentScreen = AppScreen.DISCOVERIES
                }
            )
        }
    }
}
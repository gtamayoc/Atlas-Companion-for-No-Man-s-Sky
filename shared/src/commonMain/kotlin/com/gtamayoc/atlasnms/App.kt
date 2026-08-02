package com.gtamayoc.atlasnms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBackHandler
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.screens.DiscoveryDetailScreen
import com.gtamayoc.atlasnms.shared.ui.screens.ExploreScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeViewModel
import com.gtamayoc.atlasnms.shared.ui.screens.ScanScreen
import com.gtamayoc.atlasnms.shared.ui.screens.SettingsScreen
import com.gtamayoc.atlasnms.shared.ui.screens.WikiScreen

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
    
    // Estado global de navegación para la barra inferior (Descubrimientos, Explorar, Wiki, Ajustes, Analizar)
    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERIES) }
    var selectedDiscovery by remember { mutableStateOf<Discovery?>(null) }

    // Manejo de ciclo de vida y botón de retroceso (BackHandler)
    AtlasBackHandler(
        enabled = selectedDiscovery != null || currentScreen != AppScreen.DISCOVERIES,
        onBack = {
            if (selectedDiscovery != null) {
                selectedDiscovery = null
            } else if (currentScreen != AppScreen.DISCOVERIES) {
                currentScreen = AppScreen.DISCOVERIES
            }
        }
    )

    if (selectedDiscovery != null) {
        DiscoveryDetailScreen(
            discovery = selectedDiscovery!!,
            onBack = { selectedDiscovery = null }
        )
    } else {
        // Transición de 1.5 segundos (entre 1 y 2 segundos) al cambiar de pantalla
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing))
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                AppScreen.DISCOVERIES -> {
                    HomeScreen(
                        viewModel = viewModel,
                        currentScreen = targetScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onFabClick = { currentScreen = AppScreen.SCAN },
                        onDiscoveryClick = { discovery -> selectedDiscovery = discovery }
                    )
                }
                AppScreen.EXPLORE -> {
                    ExploreScreen(
                        viewModel = viewModel,
                        currentScreen = targetScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onFabClick = { currentScreen = AppScreen.SCAN },
                        onDiscoveryClick = { discovery -> selectedDiscovery = discovery }
                    )
                }
                AppScreen.WIKI -> {
                    WikiScreen(
                        currentScreen = targetScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onFabClick = { currentScreen = AppScreen.SCAN }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        currentScreen = targetScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onFabClick = { currentScreen = AppScreen.SCAN }
                    )
                }
                AppScreen.SCAN -> {
                    ScanScreen(
                        repository = repository,
                        currentScreen = targetScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onDiscoverySaved = {
                            currentScreen = AppScreen.DISCOVERIES
                        }
                    )
                }
            }
        }
    }
}
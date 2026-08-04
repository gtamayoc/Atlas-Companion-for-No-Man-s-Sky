package com.gtamayoc.atlasnms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBackHandler
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.AtlasNavigationDrawer
import com.gtamayoc.atlasnms.shared.ui.components.AtlasTopHeader
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.screens.DiscoveryDetailScreen
import com.gtamayoc.atlasnms.shared.ui.screens.GalacticCalculatorScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeScreen
import com.gtamayoc.atlasnms.shared.ui.screens.HomeViewModel
import com.gtamayoc.atlasnms.shared.ui.screens.ScanScreen
import com.gtamayoc.atlasnms.shared.ui.screens.SettingsScreen
import com.gtamayoc.atlasnms.shared.ui.screens.WikiScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
import kotlinx.coroutines.launch

@Composable
fun App(repository: DiscoveryRepository = remember { com.gtamayoc.atlasnms.shared.data.repository.InMemoryDiscoveryRepository() }) {
    val viewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
    
    // Estado global de navegación
    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERIES) }
    var selectedDiscovery by remember { mutableStateOf<Discovery?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Manejo del botón de retroceso (BackHandler)
    AtlasBackHandler(
        enabled = selectedDiscovery != null || drawerState.isOpen || currentScreen != AppScreen.DISCOVERIES,
        onBack = {
            if (selectedDiscovery != null) {
                selectedDiscovery = null
            } else if (drawerState.isOpen) {
                coroutineScope.launch { drawerState.close() }
            } else if (currentScreen != AppScreen.DISCOVERIES) {
                currentScreen = AppScreen.DISCOVERIES
            }
        }
    )

    AtlasNMSTheme {
        if (selectedDiscovery != null) {
            DiscoveryDetailScreen(
                discovery = selectedDiscovery!!,
                onBack = { selectedDiscovery = null }
            )
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AtlasNavigationDrawer(
                        currentScreen = currentScreen,
                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                        onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        AtlasTopHeader(
                            onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                            currentScreen = currentScreen
                        )
                    },
                    bottomBar = {
                        AtlasBottomNav(
                            currentScreen = currentScreen,
                            onScreenSelected = { newScreen -> currentScreen = newScreen },
                            onFabClick = { currentScreen = AppScreen.SCAN }
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing))
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
                                    GalacticCalculatorScreen(
                                        repository = repository,
                                        currentScreen = targetScreen,
                                        onScreenSelected = { newScreen -> currentScreen = newScreen },
                                        onDiscoverySaved = {
                                            currentScreen = AppScreen.DISCOVERIES
                                        }
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
            }
        }
    }
}
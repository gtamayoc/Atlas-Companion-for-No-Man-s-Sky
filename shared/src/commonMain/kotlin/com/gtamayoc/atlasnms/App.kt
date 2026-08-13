package com.gtamayoc.atlasnms

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.SizeTransform
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

import com.gtamayoc.atlasnms.shared.ui.screens.GalacticCalculatorViewModel
import com.gtamayoc.atlasnms.shared.ui.screens.ScanViewModel

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.runtime.saveable.rememberSaveableStateHolder

@Composable
fun App(repository: DiscoveryRepository = remember { com.gtamayoc.atlasnms.shared.data.repository.InMemoryDiscoveryRepository() }) {
    val homeViewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
    val galacticViewModel: GalacticCalculatorViewModel = viewModel { GalacticCalculatorViewModel(repository) }
    val scanViewModel: ScanViewModel = viewModel { ScanViewModel(repository) }
    
    // Estado global de navegación
    var currentScreen by remember { mutableStateOf(AppScreen.DISCOVERIES) }
    var selectedDiscovery by remember { mutableStateOf<Discovery?>(null) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val saveableStateHolder = rememberSaveableStateHolder()

    val onScreenSelected: (AppScreen) -> Unit = remember { { newScreen -> currentScreen = newScreen } }
    val onOpenDrawer: () -> Unit = remember(drawerState, coroutineScope) { { coroutineScope.launch { drawerState.open() } } }
    val onCloseDrawer: () -> Unit = remember(drawerState, coroutineScope) { { coroutineScope.launch { drawerState.close() } } }
    val onFabClick: () -> Unit = remember { { currentScreen = AppScreen.SCAN } }
    val onDiscoveryClick: (Discovery) -> Unit = remember { { discovery -> selectedDiscovery = discovery } }
    val onDiscoverySaved: () -> Unit = remember { { currentScreen = AppScreen.DISCOVERIES } }
    val onClearSelectedDiscovery: () -> Unit = remember { { selectedDiscovery = null } }

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
                onBack = onClearSelectedDiscovery
            )
        } else {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AtlasNavigationDrawer(
                        currentScreen = currentScreen,
                        onScreenSelected = onScreenSelected,
                        onCloseDrawer = onCloseDrawer
                    )
                }
            ) {
                Scaffold(
                    topBar = {
                        AtlasTopHeader(
                            onOpenDrawer = onOpenDrawer,
                            currentScreen = currentScreen
                        )
                    },
                    bottomBar = {
                        AtlasBottomNav(
                            currentScreen = currentScreen,
                            onScreenSelected = onScreenSelected,
                            onFabClick = onFabClick
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
                                (fadeIn(animationSpec = tween(durationMillis = 50, easing = LinearOutSlowInEasing)) togetherWith
                                fadeOut(animationSpec = tween(durationMillis = 40, easing = LinearOutSlowInEasing)))
                                    .using(SizeTransform(clip = false))
                            },
                            label = "ScreenTransition"
                        ) { targetScreen ->
                            saveableStateHolder.SaveableStateProvider(key = targetScreen) {
                                when (targetScreen) {
                                    AppScreen.DISCOVERIES -> {
                                        HomeScreen(
                                            viewModel = homeViewModel,
                                            currentScreen = targetScreen,
                                            onScreenSelected = onScreenSelected,
                                            onFabClick = onFabClick,
                                            onDiscoveryClick = onDiscoveryClick
                                        )
                                    }
                                    AppScreen.EXPLORE -> {
                                        GalacticCalculatorScreen(
                                            viewModel = galacticViewModel,
                                            currentScreen = targetScreen,
                                            onScreenSelected = onScreenSelected,
                                            onDiscoverySaved = onDiscoverySaved
                                        )
                                    }
                                    AppScreen.WIKI -> {
                                        WikiScreen(
                                            currentScreen = targetScreen,
                                            onScreenSelected = onScreenSelected,
                                            onFabClick = onFabClick
                                        )
                                    }
                                    AppScreen.SETTINGS -> {
                                        SettingsScreen(
                                            currentScreen = targetScreen,
                                            onScreenSelected = onScreenSelected,
                                            onFabClick = onFabClick
                                        )
                                    }
                                    AppScreen.SCAN -> {
                                        ScanScreen(
                                            viewModel = scanViewModel,
                                            currentScreen = targetScreen,
                                            onScreenSelected = onScreenSelected,
                                            onDiscoverySaved = onDiscoverySaved
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
}
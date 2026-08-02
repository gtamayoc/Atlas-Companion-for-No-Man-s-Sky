package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.DiscoveryCard
import com.gtamayoc.atlasnms.shared.ui.components.ScanFab
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AtlasNMSTheme {
        Scaffold(
            bottomBar = {
                AtlasBottomNav(
                    currentScreen = currentScreen,
                    onScreenSelected = onScreenSelected
                )
            },
            floatingActionButton = { ScanFab(onClick = onFabClick) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is HomeUiState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is HomeUiState.Success -> {
                        // LazyColumn optimizado con key y contentType para fluidez en gama baja / low-RAM
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.discoveries,
                                key = { discovery -> discovery.id },
                                contentType = { discovery -> discovery.type }
                            ) { discovery ->
                                DiscoveryCard(discovery = discovery)
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.DiscoveryCard
import com.gtamayoc.atlasnms.shared.ui.components.ScanFab
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme

@Composable
fun ExploreScreen(
    viewModel: HomeViewModel,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit,
    onDiscoveryClick: (Discovery) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<DiscoveryType?>(null) }

    AtlasNMSTheme {
        Scaffold(
            topBar = {
                com.gtamayoc.atlasnms.shared.ui.components.AtlasTopNav(
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
                        val filteredDiscoveries by remember(searchQuery, selectedTypeFilter, state.discoveries) {
                            derivedStateOf {
                                state.discoveries.filter { item ->
                                    val matchesQuery = searchQuery.isBlank() ||
                                            item.name.contains(searchQuery, ignoreCase = true) ||
                                            item.systemName.contains(searchQuery, ignoreCase = true) ||
                                            item.galaxy.contains(searchQuery, ignoreCase = true)
                                    
                                    val matchesType = selectedTypeFilter == null || item.type == selectedTypeFilter
                                    
                                    matchesQuery && matchesType
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            // Panel de resumen de estadísticas de exploración
                            ExploreHeaderStats(
                                totalCount = state.discoveries.size,
                                shipCount = state.discoveries.count { it.type == DiscoveryType.SHIP },
                                planetCount = state.discoveries.count { it.type == DiscoveryType.PLANET }
                            )

                            // Barra de Búsqueda
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar por sistema, planeta o nave...", style = MaterialTheme.typography.bodyMedium) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            )

                            // Chips de Filtro Horizontal
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    FilterChip(
                                        label = "TODOS",
                                        isSelected = selectedTypeFilter == null,
                                        onClick = { selectedTypeFilter = null }
                                    )
                                }
                                items(DiscoveryType.entries) { type ->
                                    FilterChip(
                                        label = type.name,
                                        isSelected = selectedTypeFilter == type,
                                        onClick = { selectedTypeFilter = type }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Lista de Descubrimientos Optimizada
                            if (filteredDiscoveries.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No se encontraron descubrimientos que coincidan.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(
                                        items = filteredDiscoveries,
                                        key = { discovery -> discovery.id },
                                        contentType = { discovery -> discovery.type }
                                    ) { discovery ->
                                        DiscoveryCard(
                                            discovery = discovery,
                                            onClick = { onDiscoveryClick(discovery) }
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

@Composable
private fun ExploreHeaderStats(
    totalCount: Int,
    shipCount: Int,
    planetCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(4.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(label = "HALLAZGOS", value = totalCount.toString())
        StatItem(label = "NAVES", value = shipCount.toString())
        StatItem(label = "PLANETAS", value = planetCount.toString())
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

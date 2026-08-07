package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.ui.components.DiscoveryCard
import com.gtamayoc.atlasnms.shared.ui.components.DualFabGroup
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit,
    onDiscoveryClick: (Discovery) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val currentState = uiState
    val isFilterActive = currentState is HomeUiState.Success && 
        (currentState.selectedType != null || currentState.searchQuery.isNotBlank())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HomeUiState.Success -> {
                    if (state.discoveries.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "BITÁCORA VACÍA",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Aún no se han registrado descubrimientos en este sector galáctico. Presiona ANALIZAR para registrar tu primer hallazgo.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (state.filteredDiscoveries.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "SIN RESULTADOS CON EL FILTRO ACTUAL",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(
                                    onClick = { viewModel.clearFilters() },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("LIMPIAR FILTROS")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.filteredDiscoveries,
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

                    // ModalBottomSheet de Filtros M3
                    if (showFilterSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showFilterSheet = false },
                            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "⚡ FILTRAR BITÁCORA",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = state.searchQuery,
                                    onValueChange = { viewModel.setSearchQuery(it) },
                                    label = { Text("Buscar por nombre, sistema o galaxia") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )

                                Text(
                                    text = "CATEGORÍA DE HALLAZGO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilterChip(
                                        selected = state.selectedType == null,
                                        onClick = { viewModel.setTypeFilter(null) },
                                        label = { Text("TODOS") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                    for (type in DiscoveryType.entries) {
                                        key(type.name) {
                                            FilterChip(
                                                selected = state.selectedType == type,
                                                onClick = { viewModel.setTypeFilter(type) },
                                                label = { Text(type.name) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.clearFilters()
                                            showFilterSheet = false
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("RESETEAR")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = { showFilterSheet = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("APLICAR", color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            }
                        }
                    }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            DualFabGroup(
                onAnalyzeClick = onFabClick,
                onFilterClick = { showFilterSheet = true },
                isFilterActive = isFilterActive
            )
        }
    }
}

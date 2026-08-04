package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.components.GalaxySelectorModal
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSelector
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSequence
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinate
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinateUtils
import com.gtamayoc.atlasnms.shared.util.NmsGalaxies
import com.gtamayoc.atlasnms.shared.util.NmsGalaxy
import com.gtamayoc.atlasnms.shared.util.currentTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GalacticCalculatorScreen(
    repository: DiscoveryRepository,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allDiscoveries by repository.getAllDiscoveries().collectAsState(initial = emptyList())

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("🌌 REGISTRO PORTAL", "🧮 CALCULADORA GLIFOS", "📡 RADAR TELEPORTS")

    // --- ESTADO 1: REGISTRO DE PORTAL ---
    var portalName by remember { mutableStateOf("") }
    var selectedGalaxy by remember { mutableStateOf(NmsGalaxies.getByNumber(1)) }
    var showGalaxyModal by remember { mutableStateOf(false) }
    var portalGlyphs by remember { mutableStateOf(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) }

    // Coordenada calculada reactiva para el Portal
    val portalCoordinate by remember(portalGlyphs) {
        derivedStateOf { GalacticCoordinateUtils.parseGlyphsToCoordinate(portalGlyphs) }
    }

    // Portales relacionados que comparten la misma dirección de sistema/planeta (últimos 9 glifos)
    val relatedPortals by remember(portalGlyphs, allDiscoveries) {
        derivedStateOf {
            val systemAddressKey = portalGlyphs.drop(3).take(9)
            allDiscoveries.filter { discovery ->
                discovery.type == DiscoveryType.PORTAL &&
                discovery.glyphs.size >= 12 &&
                discovery.glyphs.drop(3).take(9) == systemAddressKey
            }
        }
    }

    // --- ESTADO 2: CALCULADORA MANUAL ---
    var calcGlyphs by remember { mutableStateOf(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) }
    val calcCoordinate by remember(calcGlyphs) {
        derivedStateOf { GalacticCoordinateUtils.parseGlyphsToCoordinate(calcGlyphs) }
    }

    // --- ESTADO 3: RADAR DE TELEPORTS ALEATORIOS ---
    var isSpinning by remember { mutableStateOf(false) }
    var displayedGlyphs by remember { mutableStateOf((1..12).map { (1..16).random() }) }
    var randomTeleport by remember { mutableStateOf<GalacticCoordinate?>(null) }
    var teleportName by remember { mutableStateOf("") }
    var teleportGalaxy by remember { mutableStateOf("Euclid") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // PESTAÑAS PRINCIPALES DEL MÓDULO RADAR
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // =========================================================================
                    // PESTAÑA 0: REGISTRO DE PORTALES (PRINCIPAL)
                    // =========================================================================
                    Text(
                        text = "🌌 REGISTRO GENERAL DE PORTAL",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Guarda una dirección de portal de planeta y vincúlala automáticamente a otros hallazgos dentro del mismo sistema estelar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // FORMULARIO DE PORTAL
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = portalName,
                                onValueChange = { portalName = it },
                                label = { Text("Nombre del Portal / Planeta / Base") },
                                placeholder = { Text("Ej. Portal Alfa - Planeta Paraíso") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            // CAMPO DE GALAXIA CON MODAL DE 256 GALAXIAS
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { showGalaxyModal = true }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "GALAXIA SELECCIONADA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "#${selectedGalaxy.number} - ${selectedGalaxy.name} (${selectedGalaxy.type})",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Button(
                                    onClick = { showGalaxyModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("CAMBIAR 🔍", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }

                            // SECTOR DE SELECCIÓN DE GLIFOS CON TECLADO UNIFICADO
                            Text(
                                text = "SECUENCIA DE 12 GLIFOS DE PORTAL",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )

                            GlyphSelector(
                                selectedGlyphs = portalGlyphs,
                                onGlyphsChanged = { portalGlyphs = it }
                            )

                            // DATOS CALCULADOS DE DIRECCIÓN DE PLANETA
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "📍 DIRECCIÓN TÁCTICA Y VOXEL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Coordenada NMS: ${portalCoordinate.formattedString}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Distancia al Centro Galáctico: ${portalCoordinate.distanceToCoreLightYears} Años Luz",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Clase de Sistema: ${portalCoordinate.systemClass}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // BOTÓN GUARDAR PORTAL
                            Button(
                                onClick = {
                                    if (portalGlyphs.size == 12) {
                                        coroutineScope.launch {
                                            val nameToSave = portalName.ifBlank { "Portal #${portalCoordinate.formattedString}" }
                                            val newDiscovery = Discovery(
                                                id = "portal_${currentTimeMillis()}",
                                                type = DiscoveryType.PORTAL,
                                                name = nameToSave,
                                                galaxy = selectedGalaxy.name,
                                                systemName = "Sistema ${portalCoordinate.systemIndex}",
                                                glyphs = portalGlyphs,
                                                imageUrl = "",
                                                timestamp = currentTimeMillis(),
                                                status = DiscoveryStatus.VALIDATED,
                                                confidence = 1.0
                                            )
                                            repository.saveDiscovery(newDiscovery)
                                            portalName = ""
                                            onDiscoverySaved()
                                        }
                                    }
                                },
                                enabled = portalGlyphs.size == 12,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (portalGlyphs.size == 12) "💾 GUARDAR PORTAL EN BITÁCORA" else "COMPLETA LOS 12 GLIFOS PARA GUARDAR",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // SECCIÓN PORTALES RELACIONADOS EN LA MISMA DIRECCIÓN
                    Text(
                        text = "🔗 PORTALES RELACIONADOS EN LA MISMA DIRECCIÓN (${relatedPortals.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    if (relatedPortals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay otros portales registrados con esta misma dirección de sistema estelar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            relatedPortals.forEach { related ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = related.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Galaxia: ${related.galaxy} | Sistema: ${related.systemName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        GlyphSequence(glyphs = related.glyphs, iconSize = 18)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // =========================================================================
                    // PESTAÑA 1: CALCULADORA DE GLIFOS (DECODIFICADOR)
                    // =========================================================================
                    Text(
                        text = "🧮 CALCULADORA & DECODIFICADOR DE GLIFOS",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    GlyphSelector(
                        selectedGlyphs = calcGlyphs,
                        onGlyphsChanged = { calcGlyphs = it }
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "RESULTADOS DE DECODIFICACIÓN GALÁCTICA",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Coordenada Táctica NMS: ${calcCoordinate.formattedString}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Secuencia Hexadecimal: ${calcCoordinate.glyphHexSequence}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Distancia al Centro Galáctico: ${calcCoordinate.distanceToCoreLightYears} AL",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Clase de Sistema: ${calcCoordinate.systemClass} (${calcCoordinate.regionType})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                2 -> {
                    // =========================================================================
                    // PESTAÑA 2: RADAR DE TELEPORTS ALEATORIOS
                    // =========================================================================
                    Text(
                        text = "📡 RADAR DE TELEPORTS SALVAJES",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            GlyphSequence(glyphs = displayedGlyphs, iconSize = 28)

                            Button(
                                onClick = {
                                    if (!isSpinning) {
                                        isSpinning = true
                                        randomTeleport = null
                                        coroutineScope.launch {
                                            repeat(12) {
                                                displayedGlyphs = (1..12).map { (1..16).random() }
                                                delay(80)
                                            }
                                            val generated = GalacticCoordinateUtils.generateRandomTeleport()
                                            randomTeleport = generated
                                            displayedGlyphs = generated.glyphIndices
                                            isSpinning = false
                                        }
                                    }
                                },
                                enabled = !isSpinning,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isSpinning) "ESCANEAR SECTOR GALÁCTICO..." else "🎲 GENERAR TELEPORT ALEATORIO",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            randomTeleport?.let { teleport ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "📍 TELEPORT DETECTADO EN EL VACÍO",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = "Coordenada NMS: ${teleport.formattedString}")
                                    Text(text = "Distancia al Centro: ${teleport.distanceToCoreLightYears} Años Luz")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // MODAL DE SELECCIÓN DE GALAXIA (256 GALAXIAS CON BARRA DE BÚSQUEDA)
    if (showGalaxyModal) {
        GalaxySelectorModal(
            selectedGalaxyName = selectedGalaxy.name,
            onGalaxySelected = { selectedGalaxy = it },
            onDismiss = { showGalaxyModal = false }
        )
    }
}

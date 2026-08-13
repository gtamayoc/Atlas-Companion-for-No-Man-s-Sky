package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.components.GalaxySelectorModal
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSelector
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSequence
import com.gtamayoc.atlasnms.shared.ui.components.MantraPatternBackground
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
    viewModel: GalacticCalculatorViewModel,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val tabs = listOf("🌌 REGISTRO PORTAL", "🧮 CALCULADORA GLIFOS", "📡 RADAR TELEPORTS")

    // --- ESTADO 1: REGISTRO DE PORTAL ---
    val portalName by viewModel.portalName.collectAsState()
    val selectedGalaxy by viewModel.selectedGalaxy.collectAsState()
    var showGalaxyModal by remember { mutableStateOf(false) }
    val portalGlyphs by viewModel.portalGlyphs.collectAsState()

    val portalCoordinate by viewModel.portalCoordinate.collectAsState()
    val relatedPortals by viewModel.relatedPortals.collectAsState()

    // --- ESTADO 2: CALCULADORA MANUAL ---
    val calcGlyphs by viewModel.calcGlyphs.collectAsState()
    val calcCoordinate by viewModel.calcCoordinate.collectAsState()

    // --- ESTADO 3: RADAR DE TELEPORTS ALEATORIOS ---
    val isSpinning by viewModel.isSpinning.collectAsState()
    val displayedGlyphs by viewModel.displayedGlyphs.collectAsState()
    val randomTeleport by viewModel.randomTeleport.collectAsState()
    val teleportName by viewModel.teleportName.collectAsState()
    val teleportGalaxy by viewModel.teleportGalaxy.collectAsState()
    var showTeleportGalaxyModal by remember { mutableStateOf(false) }

    val savedTeleports by viewModel.savedTeleports.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // PESTAÑAS PRINCIPALES DEL MÓDULO RADAR
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            for (index in tabs.indices) {
                val title = tabs[index]
                val onTabClick = remember(index) { { viewModel.setSelectedTabIndex(index) } }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = onTabClick,
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
                                onValueChange = { viewModel.setPortalName(it) },
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
                                onGlyphsChanged = { viewModel.setPortalGlyphs(it) }
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
                                        viewModel.savePortalDiscovery(onDiscoverySaved)
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
                            for (related in relatedPortals) {
                                key(related.id) {
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
                }

                1 -> {
                    // =========================================================================
                    // PESTAÑA 1: CALCULADORA DE GLIFOS (DECODIFICADOR)
                    // =========================================================================
                    GlyphSelector(
                        selectedGlyphs = calcGlyphs,
                        onGlyphsChanged = { viewModel.setCalcGlyphs(it) }
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
                                    viewModel.spinRadar()
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

                            // INFORMACIÓN DEL TELEPORT Y FORMULARIO PARA GUARDAR
                            val currentTeleport = randomTeleport ?: GalacticCoordinateUtils.parseGlyphsToCoordinate(displayedGlyphs)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "📍 TELEPORT / COORDENADA DETECTADA",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Coordenada NMS: ${currentTeleport.formattedString}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Distancia al Centro: ${currentTeleport.distanceToCoreLightYears} Años Luz",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                // NOMBRE PERSONALIZADO DEL TELEPORT
                                OutlinedTextField(
                                    value = teleportName,
                                    onValueChange = { viewModel.setTeleportName(it) },
                                    label = { Text("Nombre del Teleport") },
                                    placeholder = { Text("Ej. Teleport Salvaje #${currentTeleport.formattedString.takeLast(6)}") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )

                                // SELECCIÓN DE GALAXIA PARA EL TELEPORT
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                        .clickable { showTeleportGalaxyModal = true }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "GALAXIA DESTINO",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "#${teleportGalaxy.number} - ${teleportGalaxy.name}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Button(
                                        onClick = { showTeleportGalaxyModal = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("CAMBIAR 🔍", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }

                                // BOTÓN GUARDAR TELEPORT EN BITÁCORA
                                Button(
                                    onClick = {
                                        if (displayedGlyphs.size == 12 && !isSpinning) {
                                            viewModel.saveTeleportDiscovery(onDiscoverySaved)
                                        }
                                    },
                                    enabled = displayedGlyphs.size == 12 && !isSpinning,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "💾 GUARDAR TELEPORT EN BITÁCORA",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // SECCIÓN COORDENADAS Y TELEPORTS GUARDADOS EN BITÁCORA
                    Text(
                        text = "📍 COORDENADAS Y TELEPORTS GUARDADOS (${savedTeleports.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    if (savedTeleports.isEmpty()) {
                        // TARJETA MOCK CUANDO NO HAY NINGÚN GLIFO GUARDADO
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = "https://static.wikia.nocookie.net/nomanssky_gamepedia/images/5/59/The_Portal_Repository_Logo.jpg/revision/latest?cb=20180802150523",
                                        contentDescription = "The Portal Repository Mock Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Text(
                                    text = "NO HAY TELEPORTS O COORDENADAS GUARDADAS",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Usa el radar para escanear teleports salvajes o ingresa coordenadas en el registro de portales para conservarlos en tu bitácora.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // LISTA DE TELEPORTS CON FONDO DE MANTRA GEOMÉTRICO DINÁMICO ÚNICO POR CAPTURA
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (saved in savedTeleports) {
                                key(saved.id) {
                                    val savedCoord = remember(saved.glyphs) {
                                        GalacticCoordinateUtils.parseGlyphsToCoordinate(saved.glyphs)
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            // Esquema dinámico divertido y único de fondo (Mantra Pattern) para cada captura
                                            MantraPatternBackground(
                                                glyphs = saved.glyphs,
                                                seed = saved.id,
                                                modifier = Modifier.matchParentSize()
                                            )

                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = saved.name,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                        Text(
                                                            text = "🌌 ${saved.galaxy} | ${saved.systemName}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }

                                                    Button(
                                                        onClick = {
                                                            viewModel.deleteDiscovery(saved.id)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)),
                                                        shape = RoundedCornerShape(6.dp),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "🗑️ ELIMINAR",
                                                            fontSize = 11.sp,
                                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.Black.copy(alpha = 0.40f), RoundedCornerShape(6.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        Text(
                                                            text = "📍 Coordenada: ${savedCoord.formattedString}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = "Distancia al Centro: ${savedCoord.distanceToCoreLightYears} Años Luz",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = Color.White.copy(alpha = 0.85f)
                                                        )
                                                    }
                                                }

                                                GlyphSequence(glyphs = saved.glyphs, iconSize = 22)
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
    }

    // MODAL DE SELECCIÓN DE GALAXIA PARA PORTALES
    if (showGalaxyModal) {
        GalaxySelectorModal(
            selectedGalaxyName = selectedGalaxy.name,
            onGalaxySelected = { viewModel.setSelectedGalaxy(it) },
            onDismiss = { showGalaxyModal = false }
        )
    }

    // MODAL DE SELECCIÓN DE GALAXIA PARA TELEPORTS
    if (showTeleportGalaxyModal) {
        GalaxySelectorModal(
            selectedGalaxyName = teleportGalaxy.name,
            onGalaxySelected = { viewModel.setTeleportGalaxy(it) },
            onDismiss = { showTeleportGalaxyModal = false }
        )
    }
}


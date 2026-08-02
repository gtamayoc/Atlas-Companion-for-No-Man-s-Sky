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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import atlasnms.shared.generated.resources.Res
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.ui.components.AtlasTopNav
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSequence
import com.gtamayoc.atlasnms.shared.ui.components.getGlyphDrawableResource
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinate
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun GalacticCalculatorScreen(
    repository: DiscoveryRepository,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("RADAR TELEPORTS", "CALCULADORA DE GLIFOS")

    // Estado del Teleport Aleatorio (Animación Tragaperras)
    var isSpinning by remember { mutableStateOf(false) }
    var displayedGlyphs by remember { mutableStateOf((1..12).map { (1..16).random() }) }
    var randomTeleport by remember { mutableStateOf<GalacticCoordinate?>(null) }
    var teleportName by remember { mutableStateOf("") }
    var teleportGalaxy by remember { mutableStateOf("Euclid") }

    // Estado de la Calculadora Manual
    var selectedGlyphs by remember { mutableStateOf(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) }
    var calculatedCoordinate by remember {
        mutableStateOf(GalacticCoordinateUtils.parseGlyphsToCoordinate(selectedGlyphs))
    }

    AtlasNMSTheme {
        Scaffold(
            topBar = {
                AtlasTopNav(
                    currentScreen = currentScreen,
                    onScreenSelected = onScreenSelected
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Selector de pestañas superiores
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
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
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
                    if (selectedTabIndex == 0) {
                        // --- PESTAÑA 1: RADAR DE TELEPORTS (ANIMACIÓN TRAGAPERRAS) ---
                        Text(
                            text = "RADAR DE TELEPORTS SALVAJES",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Gira el rodillo para descubrir coordenadas galácticas aleatorias únicas. La ubicación NO se guarda automáticamente en la bitácora hasta que decidas marcarla como visitada.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Muestra visual de los 12 glifos en rotación
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp))
                                .border(1.dp, if (isSpinning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isSpinning) "🎰 GIRANDO RODILLO TRAGAPERRAS..." else "SECUENCIA DE PORTAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSpinning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GlyphSequence(glyphs = displayedGlyphs, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        // Botón Tragaperras
                        Button(
                            onClick = {
                                if (!isSpinning) {
                                    coroutineScope.launch {
                                        isSpinning = true
                                        // Animación estilo Tragaperras (~1000ms de giros rápidos)
                                        repeat(16) {
                                            displayedGlyphs = (1..12).map { (1..16).random() }
                                            delay(60)
                                        }

                                        val finalGenerated = GalacticCoordinateUtils.generateRandomTeleport()
                                        displayedGlyphs = finalGenerated.glyphIndices
                                        randomTeleport = finalGenerated
                                        teleportName = "Portal Inexplorado ${finalGenerated.formattedString.takeLast(4)}"
                                        isSpinning = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSpinning,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isSpinning) "GIRANDO PORTAL..." else "🎰 GENERAR TELEPORT (GIRAR TRAGAPERRAS)",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Vista previa de Teleport Generado (Sólo cuando se ha terminado el giro)
                        AnimatedVisibility(visible = randomTeleport != null && !isSpinning) {
                            val coord = randomTeleport ?: return@AnimatedVisibility

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "NUEVA UBICACIÓN FIJADA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${coord.distanceToCoreLightYears} AL del Centro",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Text(
                                    text = "Coordenadas Galácticas: ${coord.formattedString}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "${coord.systemClass} | ${coord.regionType} (Planeta #${coord.planetIndex})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedTextField(
                                    value = teleportName,
                                    onValueChange = { teleportName = it },
                                    label = { Text("Nombre / Notas de la Ubicación") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp)
                                )

                                OutlinedTextField(
                                    value = teleportGalaxy,
                                    onValueChange = { teleportGalaxy = it },
                                    label = { Text("Galaxia") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Botón 1: Marcar como VISITADO y Guardar
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val newDiscovery = Discovery(
                                                id = "teleport_${System.currentTimeMillis()}",
                                                type = DiscoveryType.PLANET,
                                                name = teleportName.ifEmpty { "Portal Galáctico ${coord.formattedString}" },
                                                galaxy = teleportGalaxy.ifEmpty { "Euclid" },
                                                systemName = "Sistema ${coord.formattedString.takeLast(4)}",
                                                glyphs = coord.glyphIndices,
                                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuANDVTdBGRPWiCGa3yW4-3DpXoVxnRPQUz7Yl1Z4DyH-5zFzKECRCCXZ9ACwwd9hSnyUsQWsA7zrot3hu4mRczWYmIjHH6hoqvKWjVcS8nYfa8D8XKGLGekayFplez03jYGea5xWUKMIECAGvCWCuFjEEAgLY24VIIP75Cnxem4mJTiVFXHL6lhoOpqbMl5onumBfcTZKrVmK0RqdS3_6WgUDwpzviJqT7jzDDDHEVWmLWhU9bX_IjVLQ",
                                                timestamp = System.currentTimeMillis(),
                                                status = DiscoveryStatus.CONFIRMED,
                                                confidence = 1.0
                                            )

                                            repository.saveDiscovery(newDiscovery)
                                            onDiscoverySaved()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("MARCAR COMO VISITADO Y GUARDAR EN BITÁCORA", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                }

                                // Botón 2: Guardar como PENDIENTE
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val pendingDiscovery = Discovery(
                                                id = "pending_${System.currentTimeMillis()}",
                                                type = DiscoveryType.PLANET,
                                                name = teleportName.ifEmpty { "Portal Pendiente ${coord.formattedString}" },
                                                galaxy = teleportGalaxy.ifEmpty { "Euclid" },
                                                systemName = "Sistema ${coord.formattedString.takeLast(4)}",
                                                glyphs = coord.glyphIndices,
                                                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuANDVTdBGRPWiCGa3yW4-3DpXoVxnRPQUz7Yl1Z4DyH-5zFzKECRCCXZ9ACwwd9hSnyUsQWsA7zrot3hu4mRczWYmIjHH6hoqvKWjVcS8nYfa8D8XKGLGekayFplez03jYGea5xWUKMIECAGvCWCuFjEEAgLY24VIIP75Cnxem4mJTiVFXHL6lhoOpqbMl5onumBfcTZKrVmK0RqdS3_6WgUDwpzviJqT7jzDDDHEVWmLWhU9bX_IjVLQ",
                                                timestamp = System.currentTimeMillis(),
                                                status = DiscoveryStatus.PENDING,
                                                confidence = 0.9
                                            )

                                            repository.saveDiscovery(pendingDiscovery)
                                            onDiscoverySaved()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("GUARDAR COMO SEÑAL PENDIENTE DE VIAJE")
                                }
                            }
                        }
                    } else {
                        // --- PESTAÑA 2: CALCULADORA MANUAL CON TECLADO DE GLIFOS (2 FILAS DE 8) ---
                        Text(
                            text = "CALCULADORA DE GLIFOS DE PORTAL",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Toca los glifos de la matriz (8 arriba y 8 abajo) para construir o modificar tu secuencia de 12 glifos en tiempo real:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Visualización de la Secuencia Seleccionada
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SECUENCIA ACTUAL (12 GLIFOS)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GlyphSequence(glyphs = selectedGlyphs, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        // MATRIZ DE GLIFOS INTERACTIVA: 2 FILAS DE 8 GLIFOS
                        Text(
                            text = "TECLADO MATRIZ DE GLIFOS (8 ARRIBA / 8 ABAJO)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )

                        GlyphKeyboard2x8(
                            onGlyphClick = { clickedGlyph ->
                                // Reemplazar cíclicamente o agregar al final
                                val currentList = selectedGlyphs.toMutableList()
                                if (currentList.size >= 12) {
                                    currentList.removeAt(0)
                                }
                                currentList.add(clickedGlyph)
                                selectedGlyphs = currentList
                                calculatedCoordinate = GalacticCoordinateUtils.parseGlyphsToCoordinate(currentList)
                            }
                        )

                        // Resultado de Decodificación
                        val coord = calculatedCoordinate
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "DECODIFICACIÓN DE COORDENADAS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Formato Galáctico: ${coord.formattedString}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Distancia al Centro Galáctico: ${coord.distanceToCoreLightYears} Años Luz",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Tipo de Sistema: ${coord.systemClass} (${coord.regionType})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlyphKeyboard2x8(
    onGlyphClick: (glyphIndex: Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // FILA 1: Primeros 8 Glifos (1..8)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..8).forEach { index ->
                val res = getGlyphDrawableResource(index - 1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .clickable { onGlyphClick(index) }
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = "Glifo $index",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // FILA 2: Segundos 8 Glifos (9..16)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (9..16).forEach { index ->
                val res = getGlyphDrawableResource(index - 1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .clickable { onGlyphClick(index) }
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(res),
                        contentDescription = "Glifo $index",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

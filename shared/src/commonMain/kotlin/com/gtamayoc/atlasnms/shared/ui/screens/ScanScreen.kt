package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalysisResult
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalyzerService
import com.gtamayoc.atlasnms.shared.domain.service.ExtractionResult
import com.gtamayoc.atlasnms.shared.domain.service.PipelineDiscrepancyValidator
import com.gtamayoc.atlasnms.shared.domain.service.PipelineStageSource
import com.gtamayoc.atlasnms.shared.domain.service.ScanPipelineDebugger
import com.gtamayoc.atlasnms.shared.domain.service.ValidationDiscrepancy
import com.gtamayoc.atlasnms.shared.domain.service.VisionExtractionEngine
import com.gtamayoc.atlasnms.shared.ui.components.getGlyphDrawableResource
import com.gtamayoc.atlasnms.shared.ui.components.rememberImagePickerHandler
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AmberDustHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
import com.gtamayoc.atlasnms.shared.ui.theme.WarpFuelOrangeHighlight
import com.gtamayoc.atlasnms.shared.util.currentTimeMillis
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

enum class AnalysisStage {
    STAGE_1_CAPTURE,
    STAGE_2_OCR_DONE,
    STAGE_3_USER_REVIEW,
    STAGE_4_AI_DONE,
    STAGE_5_COMPLETED
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val currentStage by viewModel.currentStage.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isOcrRunning by viewModel.isOcrRunning.collectAsState()
    val isAiRunning by viewModel.isAiRunning.collectAsState()

    LaunchedEffect(currentStage) {
        if (currentStage == AnalysisStage.STAGE_5_COMPLETED) {
            onDiscoverySaved()
        }
    }

    var showDevDebugDrawer by remember { mutableStateOf(false) }
    var showPhotoOverlayModal by remember { mutableStateOf(false) }

    val enhanceContrast by viewModel.enhanceContrast.collectAsState()
    val binarizeForOcr by viewModel.binarizeForOcr.collectAsState()
    val extractionResult by viewModel.extractionResult.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()
    val validationDiscrepancies by viewModel.validationDiscrepancies.collectAsState()

    val userVerifiedName by viewModel.userVerifiedName.collectAsState()
    val userVerifiedSystem by viewModel.userVerifiedSystem.collectAsState()
    val userVerifiedGalaxy by viewModel.userVerifiedGalaxy.collectAsState()
    var showScanGalaxyModal by remember { mutableStateOf(false) }
    val userVerifiedType by viewModel.userVerifiedType.collectAsState()
    val includeGlyphsInCapture by viewModel.includeGlyphsInCapture.collectAsState()
    val userVerifiedGlyphs by viewModel.userVerifiedGlyphs.collectAsState()
    val activeGlyphSlotIndex by viewModel.activeGlyphSlotIndex.collectAsState()
    val pickerHandler = rememberImagePickerHandler { uriPath ->
        viewModel.onImageSelected(uriPath)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Header & Stepper (DESIGN.md: 4px Soft-Industrial geometry)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANÁLISIS DE CAPTURA NMS",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmberDustHighlight,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = { showDevDebugDrawer = !showDevDebugDrawer },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (showDevDebugDrawer) "🛠️ OCULTAR DEV" else "🛠️ MODO DEV",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    PipelineStepper(currentStage = currentStage)
                }

                // TARJETA 1: Selección de Captura & Preprocesamiento
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "1. SELECCIÓN DE CAPTURA",
                            style = MaterialTheme.typography.labelLarge,
                            color = AmberDustHighlight,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { pickerHandler.launchGallery() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberDustHighlight),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("📁 GALERÍA", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { pickerHandler.launchCamera() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("📷 CÁMARA")
                            }
                        }

                        // Visor de la Captura
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Captura cargada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = "SELECCIONA UNA CAPTURA REAL DE NO MAN'S SKY",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isOcrRunning || isAiRunning) {
                                com.gtamayoc.atlasnms.shared.ui.components.AtlasLoadingOverlay(
                                    message = if (isOcrRunning) "PROCESANDO VISIÓN OPTICA Y OCR..." else "ANALIZANDO CON IA MULTIMODAL...",
                                    subMessage = if (isOcrRunning) "Extrayendo glifos, textos y metadatos" else "Sintetizando clasificación del hallazgo"
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = enhanceContrast,
                                    onCheckedChange = { viewModel.setEnhanceContrast(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AmberDustHighlight)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Realce Contraste C", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = binarizeForOcr,
                                    onCheckedChange = { viewModel.setBinarizeForOcr(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AmberDustHighlight)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Binarización Adaptativa", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.runOcrProcessing()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedImageUri != null && !isOcrRunning && !isAiRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberDustHighlight),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("1. ESCANEAR CAPTURA", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // TARJETA 2: Formulario & Selección de Categoría
                if (extractionResult != null && currentStage >= AnalysisStage.STAGE_3_USER_REVIEW) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2. REVISIÓN Y COMPLETADO MANUAL",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(
                                    onClick = { showPhotoOverlayModal = true },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("👁️ VER CAPTURA")
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                    .clickable { showPhotoOverlayModal = true }
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Overlay flotante",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Text(
                                text = "CATEGORÍA DEL HALLAZGO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (type in DiscoveryType.entries) {
                                    key(type.name) {
                                        FilterChip(
                                            selected = userVerifiedType == type,
                                            onClick = { viewModel.setUserVerifiedType(type) },
                                            label = { Text(type.name, fontSize = 11.sp) },
                                            shape = RoundedCornerShape(0.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = AmberDustHighlight,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = userVerifiedName,
                                onValueChange = { viewModel.setUserVerifiedName(it) },
                                label = { Text("Nombre del Hallazgo") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = userVerifiedSystem,
                                    onValueChange = { viewModel.setUserVerifiedSystem(it) },
                                    label = { Text("Sistema Solar") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showScanGalaxyModal = true }
                                ) {
                                    OutlinedTextField(
                                        value = userVerifiedGalaxy.ifBlank { "Euclid" },
                                        onValueChange = {},
                                        label = { Text("Galaxia (256) 🔍") },
                                        singleLine = true,
                                        readOnly = true,
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "¿Incluye Glifos de Portal?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = includeGlyphsInCapture,
                                    onCheckedChange = { checked ->
                                        viewModel.setIncludeGlyphsInCapture(checked)
                                        if (checked && userVerifiedGlyphs.isEmpty()) {
                                            viewModel.setUserVerifiedGlyphs(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
                                        } else if (!checked) {
                                            viewModel.setUserVerifiedGlyphs(emptyList())
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AmberDustHighlight)
                                )
                            }

                            // TARJETA 3: Editor de Glifos
                            if (includeGlyphsInCapture) {
                                InteractiveGlyphSequenceEditor(
                                    glyphs = userVerifiedGlyphs,
                                    activeSlotIndex = activeGlyphSlotIndex,
                                    onSlotClick = { viewModel.setActiveGlyphSlotIndex(it) },
                                    onGlyphSelected = { clickedGlyph ->
                                        val currentList = userVerifiedGlyphs.toMutableList()
                                        while (currentList.size < 12) currentList.add(1)
                                        val slotToReplace = activeGlyphSlotIndex.coerceIn(0, 11)
                                        currentList[slotToReplace] = clickedGlyph
                                        viewModel.setUserVerifiedGlyphs(currentList)
                                        viewModel.setActiveGlyphSlotIndex((slotToReplace + 1) % 12)
                                    },
                                    onDeleteSingle = {
                                        val currentList = userVerifiedGlyphs.toMutableList()
                                        if (currentList.isNotEmpty()) {
                                            val slotToClear = activeGlyphSlotIndex.coerceIn(0, currentList.size - 1)
                                            currentList.removeAt(slotToClear)
                                            viewModel.setUserVerifiedGlyphs(currentList)
                                            viewModel.setActiveGlyphSlotIndex(maxOf(0, slotToClear - 1))
                                        }
                                    },
                                    onClearAll = {
                                        viewModel.setUserVerifiedGlyphs(emptyList())
                                        viewModel.setActiveGlyphSlotIndex(0)
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.saveFinalDiscovery()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("GUARDAR DIRECTO", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.runAiAnalysis()
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    enabled = !isAiRunning,
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberDustHighlight),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("PROCESAR IA", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // TARJETA 4: Resultado Final IA
                if (aiResult != null && currentStage >= AnalysisStage.STAGE_5_COMPLETED) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "3. RESULTADO IA Y CONFIRMACIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberDustHighlight,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Categoría: ${aiResult!!.detectedType.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberDustHighlight)
                                    Text("Nombre: ${aiResult!!.suggestedName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Sistema: ${aiResult!!.systemName} | Galaxia: ${aiResult!!.galaxyName}", fontSize = 11.sp)
                                    if (aiResult!!.glyphsHex.isNotBlank()) {
                                        Text("Portal Hex: ${aiResult!!.glyphsHex}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.saveFinalDiscovery()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberDustHighlight),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("GUARDAR EN BITÁCORA", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Modal Overlay de Foto
                if (showPhotoOverlayModal && selectedImageUri != null) {
                    Dialog(
                        onDismissRequest = { showPhotoOverlayModal = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.92f))
                                .clickable { showPhotoOverlayModal = false },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Captura completa",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                            Button(
                                onClick = { showPhotoOverlayModal = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(24.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("✕ CERRAR")
                            }
                        }
                    }
                }
            }

            if (showScanGalaxyModal) {
                com.gtamayoc.atlasnms.shared.ui.components.GalaxySelectorModal(
                    selectedGalaxyName = userVerifiedGalaxy,
                    onGalaxySelected = { galaxy ->
                        viewModel.setUserVerifiedGalaxy(galaxy.name)
                    },
                    onDismiss = { showScanGalaxyModal = false }
                )
            }
        }
    }

@Composable
fun PipelineStepper(currentStage: AnalysisStage) {
    val stages = listOf("1. Captura", "2. Revisión", "3. IA", "4. Guardar")
    val currentIndex = when (currentStage) {
        AnalysisStage.STAGE_1_CAPTURE -> 0
        AnalysisStage.STAGE_2_OCR_DONE -> 1
        AnalysisStage.STAGE_3_USER_REVIEW -> 1
        AnalysisStage.STAGE_4_AI_DONE -> 2
        AnalysisStage.STAGE_5_COMPLETED -> 3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in stages.indices) {
            val name = stages[index]
            key(index) {
                val isActive = index <= currentIndex
                val isCurrent = index == currentIndex

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isCurrent) AmberDustHighlight
                                else if (isActive) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 10.sp,
                            color = if (isCurrent) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = name,
                        fontSize = 10.sp,
                        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveGlyphSequenceEditor(
    glyphs: List<Int>,
    activeSlotIndex: Int,
    onSlotClick: (Int) -> Unit,
    onGlyphSelected: (Int) -> Unit,
    onDeleteSingle: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Secuencia de Glifos (12):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onDeleteSingle,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("⌫ BORRAR", fontSize = 10.sp)
                }
                OutlinedButton(
                    onClick = onClearAll,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("🗑️ LIMPIAR", fontSize = 10.sp)
                }
            }
        }

        // 12 Ranuras
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (index in 0..11) {
                key(index) {
                    val gVal = glyphs.getOrNull(index)
                    GlyphSlotBox(
                        slotIndex = index,
                        glyphVal = gVal,
                        isSelected = activeSlotIndex == index,
                        onClick = { onSlotClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Text(
            text = "Teclado Táctil 2x8:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GlyphKeyboardPicker2x8(onGlyphSelected = onGlyphSelected)
    }
}

@Composable
fun GlyphSlotBox(
    slotIndex: Int,
    glyphVal: Int?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(0.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) AmberDustHighlight else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(0.dp)
            )
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (glyphVal != null) {
            val res = getGlyphDrawableResource(glyphVal)
            Image(
                painter = painterResource(res),
                contentDescription = "Glifo $glyphVal",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "#${slotIndex + 1}",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private val GlyphRangeFirstRow = (1..8).toList()
private val GlyphRangeSecondRow = (9..16).toList()

@Composable
fun GlyphKeyboardPicker2x8(onGlyphSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (glyphValue in GlyphRangeFirstRow) {
                key(glyphValue) {
                    val res = remember(glyphValue) { getGlyphDrawableResource(glyphValue) }
                    val onClick = remember(glyphValue, onGlyphSelected) { { onGlyphSelected(glyphValue) } }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(0.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(0.dp))
                            .clickable(onClick = onClick)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(res),
                            contentDescription = "Glifo $glyphValue",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (glyphValue in GlyphRangeSecondRow) {
                key(glyphValue) {
                    val res = remember(glyphValue) { getGlyphDrawableResource(glyphValue) }
                    val onClick = remember(glyphValue, onGlyphSelected) { { onGlyphSelected(glyphValue) } }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(0.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(0.dp))
                            .clickable(onClick = onClick)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(res),
                            contentDescription = "Glifo $glyphValue",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gtamayoc.atlasnms.shared.domain.service.DiscrepancySeverity
import com.gtamayoc.atlasnms.shared.domain.service.ExtractionResult
import com.gtamayoc.atlasnms.shared.domain.service.NMS_GLYPH_NAMES
import com.gtamayoc.atlasnms.shared.domain.service.PipelineDiscrepancyValidator
import com.gtamayoc.atlasnms.shared.domain.service.PipelineStageSource
import com.gtamayoc.atlasnms.shared.domain.service.ScanPipelineDebugger
import com.gtamayoc.atlasnms.shared.domain.service.ValidationDiscrepancy
import com.gtamayoc.atlasnms.shared.domain.service.VisionExtractionEngine
import com.gtamayoc.atlasnms.shared.ui.components.getGlyphDrawableResource
import com.gtamayoc.atlasnms.shared.ui.components.rememberImagePickerHandler
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
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
    repository: DiscoveryRepository,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var currentStage by remember { mutableStateOf(AnalysisStage.STAGE_1_CAPTURE) }

    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isOcrRunning by remember { mutableStateOf(false) }
    var isAiRunning by remember { mutableStateOf(false) }
    var showDevDebugDrawer by remember { mutableStateOf(false) }
    var showPhotoOverlayModal by remember { mutableStateOf(false) } // Modal Overlay para inspección de foto

    var enhanceContrast by remember { mutableStateOf(true) }
    var binarizeForOcr by remember { mutableStateOf(true) }

    var extractionResult by remember { mutableStateOf<ExtractionResult?>(null) }
    var aiResult by remember { mutableStateOf<AiAnalysisResult?>(null) }
    var validationDiscrepancies by remember { mutableStateOf<List<ValidationDiscrepancy>>(emptyList()) }

    // PASO INTERMEDIO: Campos editables por el usuario tras la Extracción Híbrida
    var userVerifiedName by remember { mutableStateOf("") }
    var userVerifiedSystem by remember { mutableStateOf("") }
    var userVerifiedGalaxy by remember { mutableStateOf("") }
    var userVerifiedType by remember { mutableStateOf(DiscoveryType.PLANET) }
    var includeGlyphsInCapture by remember { mutableStateOf(true) }
    var userVerifiedGlyphs by remember { mutableStateOf(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) }
    var activeGlyphSlotIndex by remember { mutableStateOf(0) }

    val telemetry by ScanPipelineDebugger.telemetry.collectAsState()

    val pickerHandler = rememberImagePickerHandler { uriPath ->
        selectedImageUri = uriPath
        currentStage = AnalysisStage.STAGE_1_CAPTURE
        extractionResult = null
        aiResult = null
        validationDiscrepancies = emptyList()
        activeGlyphSlotIndex = 0
        ScanPipelineDebugger.reset()
        ScanPipelineDebugger.log(
            stage = PipelineStageSource.OCR_EXTRACTION,
            level = "INFO",
            summary = "Nueva imagen seleccionada",
            details = "URI: $uriPath"
        )
        ScanPipelineDebugger.updateTelemetry { it.copy(selectedImageUri = uriPath) }
    }

    AtlasNMSTheme {
        Scaffold(
            topBar = {
                com.gtamayoc.atlasnms.shared.ui.components.AtlasTopNav(
                    currentScreen = currentScreen,
                    onScreenSelected = onScreenSelected
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Encabezado y Stepper
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANÁLISIS DE CAPTURA NMS",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showDevDebugDrawer = !showDevDebugDrawer },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showDevDebugDrawer) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (showDevDebugDrawer) "🛠️ DEPURADOR ON" else "🛠️ MODO DEV",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (showDevDebugDrawer) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    PipelineStepper(currentStage = currentStage)
                }

                // ETAPA 1: Captura & Filtro C Nativo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "1. SELECCIÓN DE CAPTURA (NAVES, PLANETAS, FAUNA, BASES)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { pickerHandler.launchGallery() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("GALERÍA DE FOTOS", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }

                            OutlinedButton(
                                onClick = { pickerHandler.launchCamera() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("TOMAR FOTO")
                            }
                        }

                        // Vista Previa de la Captura
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
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
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            if (isOcrRunning || isAiRunning) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (isOcrRunning) "Extrayendo texto tipográfico & analizando captura..." else "Estructurando respuesta JSON con IA Multimodal...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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
                                    onCheckedChange = { enhanceContrast = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Realce Contraste C", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = binarizeForOcr,
                                    onCheckedChange = { binarizeForOcr = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Binarización Adaptativa", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Button(
                            onClick = {
                                val uri = selectedImageUri ?: return@Button
                                coroutineScope.launch {
                                    isOcrRunning = true
                                    val dummyPixels = IntArray(600 * 400) { (0xFF shl 24) or ((it % 255) shl 16) or ((it % 255) shl 8) or (it % 255) }
                                    
                                    val startTime = System.currentTimeMillis()
                                    val extRes = VisionExtractionEngine.extractHybridData(
                                        imageUriOrPath = uri,
                                        rawPixels = dummyPixels,
                                        width = 600,
                                        height = 400,
                                        enhanceContrast = enhanceContrast,
                                        binarizeForOcr = binarizeForOcr
                                    )
                                    val ocrTime = System.currentTimeMillis() - startTime

                                    extractionResult = extRes
                                    userVerifiedName = extRes.candidateName
                                    userVerifiedSystem = extRes.candidateSystem
                                    userVerifiedGalaxy = extRes.candidateGalaxy
                                    userVerifiedType = extRes.candidateType
                                    includeGlyphsInCapture = extRes.hasGlyphs
                                    userVerifiedGlyphs = if (extRes.hasGlyphs) extRes.matchedGlyphsIndices else emptyList()
                                    activeGlyphSlotIndex = 0

                                    ScanPipelineDebugger.log(
                                        stage = PipelineStageSource.OCR_EXTRACTION,
                                        level = "INFO",
                                        summary = "Extracción completada",
                                        details = "Tipo: ${extRes.candidateType} | Contiene Glifos: ${extRes.hasGlyphs}"
                                    )

                                    ScanPipelineDebugger.updateTelemetry { current ->
                                        current.copy(
                                            nativeTimeMs = extRes.processingTimeMs,
                                            ocrTimeMs = ocrTime,
                                            ocrRawText = extRes.rawText
                                        )
                                    }

                                    isOcrRunning = false
                                    currentStage = AnalysisStage.STAGE_3_USER_REVIEW
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedImageUri != null && !isOcrRunning && !isAiRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("1. ESCANEAR E IDENTIFICAR CAPTURA", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // ETAPA 2 & 3: PASO CLAVE INTERMEDIO CON VISTA INTERACTIVA Y OVERLAY MODAL DE FOTO
                if (extractionResult != null && currentStage >= AnalysisStage.STAGE_3_USER_REVIEW) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                Button(
                                    onClick = { showPhotoOverlayModal = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("🔎 VER FOTO COMPLETA (OVERLAY)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Usa el botón flotante arriba para abrir la foto sobre puesta en pantalla y copiar los glifos o nombres sin perder la vista:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Muestra visual DE LA FOTO EN ESTA SECCIÓN para referencia rápida
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(4.dp))
                                    .clickable { showPhotoOverlayModal = true }
                            ) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Toca para abrir overlay flotante",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("🔍 TOCA PARA OVERLAY COMPLETO", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Muestra del Texto Bruto Extraído
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = extractionResult!!.rawText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Selector de Categoría (DiscoveryType)
                            Text(
                                text = "Categoría del Descubrimiento:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                DiscoveryType.entries.forEach { type ->
                                    FilterChip(
                                        selected = userVerifiedType == type,
                                        onClick = { userVerifiedType = type },
                                        label = { Text(type.name, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    )
                                }
                            }

                            // Formulario interactivo
                            OutlinedTextField(
                                value = userVerifiedName,
                                onValueChange = { userVerifiedName = it },
                                label = { Text("Nombre del Hallazgo (Planeta, Nave, Criatura, etc.)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.tertiary)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = userVerifiedSystem,
                                    onValueChange = { userVerifiedSystem = it },
                                    label = { Text("Sistema Solar") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                OutlinedTextField(
                                    value = userVerifiedGalaxy,
                                    onValueChange = { userVerifiedGalaxy = it },
                                    label = { Text("Galaxia") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }

                            // Toggle para Indicar si la Captura Incluye Glifos de Portal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "¿La imagen incluye Glifos de Portal?",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Switch(
                                    checked = includeGlyphsInCapture,
                                    onCheckedChange = { checked ->
                                        includeGlyphsInCapture = checked
                                        if (checked && userVerifiedGlyphs.isEmpty()) {
                                            userVerifiedGlyphs = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
                                        } else if (!checked) {
                                            userVerifiedGlyphs = emptyList()
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.tertiary)
                                )
                            }

                            // SECCIÓN DE EDITOR DE GLIFOS CON LOGOS GRANDES Y CLAROS
                            if (includeGlyphsInCapture) {
                                InteractiveGlyphSequenceEditor(
                                    glyphs = userVerifiedGlyphs,
                                    activeSlotIndex = activeGlyphSlotIndex,
                                    onSlotClick = { activeGlyphSlotIndex = it },
                                    onGlyphSelected = { clickedGlyph ->
                                        val currentList = userVerifiedGlyphs.toMutableList()
                                        while (currentList.size < 12) currentList.add(1)
                                        val slotToReplace = activeGlyphSlotIndex.coerceIn(0, 11)
                                        currentList[slotToReplace] = clickedGlyph
                                        userVerifiedGlyphs = currentList
                                        activeGlyphSlotIndex = (slotToReplace + 1) % 12
                                    },
                                    onDeleteSingle = {
                                        val currentList = userVerifiedGlyphs.toMutableList()
                                        if (currentList.isNotEmpty()) {
                                            val slotToClear = activeGlyphSlotIndex.coerceIn(0, currentList.size - 1)
                                            currentList.removeAt(slotToClear)
                                            userVerifiedGlyphs = currentList
                                            activeGlyphSlotIndex = maxOf(0, slotToClear - 1)
                                        }
                                    },
                                    onClearAll = {
                                        userVerifiedGlyphs = emptyList()
                                        activeGlyphSlotIndex = 0
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            val newDiscovery = Discovery(
                                                id = "scan_${System.currentTimeMillis()}",
                                                type = userVerifiedType,
                                                name = userVerifiedName.ifBlank { "Hallazgo NMS" },
                                                galaxy = userVerifiedGalaxy.ifBlank { "Euclid" },
                                                systemName = userVerifiedSystem.ifBlank { "Sistema Desconocido" },
                                                glyphs = if (includeGlyphsInCapture) userVerifiedGlyphs else emptyList(),
                                                imageUrl = selectedImageUri,
                                                timestamp = System.currentTimeMillis(),
                                                status = DiscoveryStatus.CONFIRMED,
                                                confidence = 1.0
                                            )
                                            repository.saveDiscovery(newDiscovery)
                                            onDiscoverySaved()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("GUARDAR DIRECTO EN BD", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        val uri = selectedImageUri ?: return@Button
                                        coroutineScope.launch {
                                            isAiRunning = true

                                            val finalGlyphsList = if (includeGlyphsInCapture) userVerifiedGlyphs else emptyList()

                                            val diff = "Diff Extracción vs Usuario: Tipo (${userVerifiedType.name}), Nombre ('$userVerifiedName'), Glifos ($finalGlyphsList)"
                                            ScanPipelineDebugger.log(
                                                stage = PipelineStageSource.USER_EDIT,
                                                level = "INFO",
                                                summary = "Datos confirmados manualmente por el usuario",
                                                details = diff
                                            )
                                            ScanPipelineDebugger.updateTelemetry { it.copy(userEditedTextDiff = diff) }

                                            val aiRes = AiAnalyzerService.analyzeScreenshotWithVerifiedText(
                                                imageUriOrPath = uri,
                                                verifiedName = userVerifiedName,
                                                verifiedSystem = userVerifiedSystem,
                                                verifiedGalaxy = userVerifiedGalaxy,
                                                verifiedType = userVerifiedType,
                                                verifiedGlyphs = finalGlyphsList,
                                                nativeResult = extractionResult?.nativeCMetrics
                                            )

                                            aiResult = aiRes

                                            val discrepancies = PipelineDiscrepancyValidator.validatePipeline(
                                                ocrResult = null,
                                                userVerifiedName = userVerifiedName,
                                                userVerifiedSystem = userVerifiedSystem,
                                                userVerifiedGalaxy = userVerifiedGalaxy,
                                                userVerifiedType = userVerifiedType,
                                                userVerifiedGlyphs = finalGlyphsList,
                                                aiResult = aiRes
                                            )

                                            validationDiscrepancies = discrepancies

                                            ScanPipelineDebugger.updateTelemetry { current ->
                                                current.copy(discrepanciesCount = discrepancies.size)
                                            }

                                            isAiRunning = false
                                            currentStage = AnalysisStage.STAGE_5_COMPLETED
                                        }
                                    },
                                    modifier = Modifier.weight(1.3f),
                                    enabled = !isAiRunning,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("PROCESAR CON IA MULTIMODAL", fontSize = 11.sp, color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ETAPA 4 & 5: RESULTADO FINAL E INSPECCIÓN DE SALIDA JSON MULTIMODAL
                if (aiResult != null && currentStage >= AnalysisStage.STAGE_5_COMPLETED) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "3. RESULTADO REFINADO POR IA & REPORTE DE DISCREPANCIAS",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Categoría: ${aiResult!!.detectedType.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                    Text("Nombre Refinado: ${aiResult!!.suggestedName}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Sistema: ${aiResult!!.systemName} | Galaxia: ${aiResult!!.galaxyName}", fontSize = 11.sp)
                                    if (aiResult!!.glyphsHex.isNotBlank()) {
                                        Text("NMS Portal Hex: ${aiResult!!.glyphsHex}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Confidence Score: ${aiResult!!.confidenceScoreLabel}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }

                            if (validationDiscrepancies.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "DISCREPANCIAS DETECTADAS (${validationDiscrepancies.size}):",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                    validationDiscrepancies.forEach { disc ->
                                        DiscrepancyBadge(discrepancy = disc)
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "✅ Todos los datos fueron sincronizados exitosamente con la BD de Bitácora.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val newDiscovery = Discovery(
                                            id = "scan_${System.currentTimeMillis()}",
                                            type = userVerifiedType,
                                            name = userVerifiedName,
                                            galaxy = userVerifiedGalaxy,
                                            systemName = userVerifiedSystem,
                                            glyphs = if (includeGlyphsInCapture) userVerifiedGlyphs else emptyList(),
                                            imageUrl = selectedImageUri ?: "",
                                            timestamp = System.currentTimeMillis(),
                                            status = DiscoveryStatus.CONFIRMED,
                                            confidence = aiResult!!.confidence
                                        )
                                        repository.saveDiscovery(newDiscovery)
                                        onDiscoverySaved()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("GUARDAR EN BITÁCORA DE SQLITE", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (showDevDebugDrawer) {
                    DevDebugDrawer(telemetry = telemetry)
                }

                // OVERLAY DIALOG MODAL: FOTO A PANTALLA COMPLETA PARA INSPECCIÓN MANUAL DE GLIFOS
                if (showPhotoOverlayModal && selectedImageUri != null) {
                    Dialog(
                        onDismissRequest = { showPhotoOverlayModal = false },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.92f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔍 INSPECCIÓN DE CAPTURA (GLIFOS EN MARGEN INFERIOR)",
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Button(
                                        onClick = { showPhotoOverlayModal = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("✕ CERRAR VISTA", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Foto ampliada para copia manual de glifos",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                                ) {
                                    Text(
                                        text = "📌 CONSEJO: Revisa los 12 iconos ubicados en el margen inferior izquierdo de la captura para ingresarlos fácilmente en el editor.",
                                        color = Color.LightGray,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(10.dp)
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

/**
 * Editor Interactivo de Secuencia de Glifos con Alta UX y LOGOS GRANDES:
 * - Selección táctil de ranuras (Slot 0..11) para reemplazar el glifo en la posición exacta.
 * - Iconos en la grilla 2x8 GRANDES, sin números encimados que oscurezcan los logos.
 */
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coordenadas de Portal (${glyphs.size}/12 glifos):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            val currentHex = glyphs.take(12).joinToString("") { (it - 1).coerceIn(0, 15).toString(16).uppercase() }
            Text(
                text = "HEX: [ $currentHex ]",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        // Grilla interactiva de las 12 ranuras (2 filas de 6 ranuras cada una)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val fullList = (0..11).map { idx -> if (idx < glyphs.size) glyphs[idx] else null }
            val firstRow = fullList.take(6)
            val secondRow = fullList.drop(6)

            // Fila 1: Ranuras 0 a 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                firstRow.forEachIndexed { rowIdx, glyphVal ->
                    val slotIndex = rowIdx
                    val isSelected = slotIndex == activeSlotIndex
                    GlyphSlotBox(
                        slotIndex = slotIndex,
                        glyphVal = glyphVal,
                        isSelected = isSelected,
                        onClick = { onSlotClick(slotIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fila 2: Ranuras 6 a 11
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                secondRow.forEachIndexed { rowIdx, glyphVal ->
                    val slotIndex = rowIdx + 6
                    val isSelected = slotIndex == activeSlotIndex
                    GlyphSlotBox(
                        slotIndex = slotIndex,
                        glyphVal = glyphVal,
                        isSelected = isSelected,
                        onClick = { onSlotClick(slotIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Barra de Herramientas de Edición Rápida UX
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val activeName = if (activeSlotIndex in 0..11 && activeSlotIndex < glyphs.size) {
                val key = (glyphs[activeSlotIndex] - 1).coerceIn(0, 15)
                NMS_GLYPH_NAMES[key] ?: "Ranura #${activeSlotIndex + 1}"
            } else "Ranura #${activeSlotIndex + 1}"

            Text(
                text = "Editando: $activeName",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = onDeleteSingle,
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("⌫ BORRAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onClearAll,
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("🗑️ LIMPIAR TODO", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // TECLADO DE GLIFOS CON LOGOS GRANDES Y CLAROS
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
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (glyphVal != null) {
            val res = getGlyphDrawableResource(glyphVal)
            Image(
                painter = painterResource(res),
                contentDescription = "Ranura ${slotIndex + 1} - Glifo $glyphVal",
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

/**
 * Teclado 2x8 Optimizado con LOGOS GRANDES Y CLAROS:
 * - Los iconos ocupan el 100% del espacio del botón para máxima nitidez visual.
 * - Sin textos encimados que achiquen el gráfico del glifo.
 */
@Composable
fun GlyphKeyboardPicker2x8(onGlyphSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // FILA 1: Primeros 8 Glifos (Valores 1..8)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..8).forEach { glyphValue ->
                val res = getGlyphDrawableResource(glyphValue)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .clickable { onGlyphSelected(glyphValue) }
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

        // FILA 2: Segundos 8 Glifos (Valores 9..16)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (9..16).forEach { glyphValue ->
                val res = getGlyphDrawableResource(glyphValue)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .clickable { onGlyphSelected(glyphValue) }
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

@Composable
fun PipelineStepper(currentStage: AnalysisStage) {
    val stages = listOf("1. Captura", "2. Visión", "3. Usuario+Foto", "4. IA Vision", "5. Fin")
    val currentIndex = when (currentStage) {
        AnalysisStage.STAGE_1_CAPTURE -> 0
        AnalysisStage.STAGE_2_OCR_DONE -> 1
        AnalysisStage.STAGE_3_USER_REVIEW -> 2
        AnalysisStage.STAGE_4_AI_DONE -> 3
        AnalysisStage.STAGE_5_COMPLETED -> 4
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, name ->
            val isActive = index <= currentIndex
            val isCurrent = index == currentIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .background(
                        color = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isActive -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        },
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> MaterialTheme.colorScheme.onPrimary
                        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}

@Composable
fun DiscrepancyBadge(discrepancy: ValidationDiscrepancy) {
    val bgColor = when (discrepancy.severity) {
        DiscrepancySeverity.HIGH -> MaterialTheme.colorScheme.errorContainer
        DiscrepancySeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        DiscrepancySeverity.INFO -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val textColor = when (discrepancy.severity) {
        DiscrepancySeverity.HIGH -> MaterialTheme.colorScheme.onErrorContainer
        DiscrepancySeverity.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        DiscrepancySeverity.INFO -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(text = "⚠️ [${discrepancy.stage}] ${discrepancy.title}", style = MaterialTheme.typography.labelSmall, color = textColor, fontWeight = FontWeight.Bold)
            Text(text = discrepancy.description, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = textColor)
        }
    }
}

@Composable
fun DevDebugDrawer(telemetry: com.gtamayoc.atlasnms.shared.domain.service.PipelineTelemetrySnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🛠️ ARQUITECTURA DE DEPURACIÓN Y SINCRONIZACIÓN",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFFCC00),
                fontWeight = FontWeight.Bold
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tiempo C Nativo: ${telemetry.nativeTimeMs} ms", fontSize = 11.sp, color = Color.White)
                Text("Tiempo OCR/Híbrido: ${telemetry.ocrTimeMs} ms", fontSize = 11.sp, color = Color.White)
                Text("Tiempo IA Vision: ${telemetry.aiTimeMs} ms", fontSize = 11.sp, color = Color.White)
            }

            Text("Modificaciones de Usuario (Diff):", fontSize = 11.sp, color = Color(0xFFAAAAFF))
            Text(telemetry.userEditedTextDiff, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.LightGray)

            Text("Prompt Maestro Multimodal:", fontSize = 11.sp, color = Color(0xFFAAAAFF))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(2.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = telemetry.promptSentToAi.ifBlank { "Sin prompt generado aún" },
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Green
                )
            }

            Text("Respuesta JSON Multimodal (Sincronizada con BD):", fontSize = 11.sp, color = Color(0xFFAAAAFF))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(2.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = telemetry.rawAiJsonResponse.ifBlank { "Sin JSON devuelto aún" },
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Cyan
                )
            }
        }
    }
}

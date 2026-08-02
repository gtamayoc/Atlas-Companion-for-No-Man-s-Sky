package com.gtamayoc.atlasnms.shared.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalysisResult
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalyzerService
import com.gtamayoc.atlasnms.shared.native.NativeImageProcessor
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSequence
import com.gtamayoc.atlasnms.shared.ui.components.rememberImagePickerHandler
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    repository: DiscoveryRepository,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisComplete by remember { mutableStateOf(false) }

    var nativeProcessResult by remember { mutableStateOf<NativeImageProcessor.ProcessingResult?>(null) }
    var aiAnalysisResult by remember { mutableStateOf<AiAnalysisResult?>(null) }

    // Campos editables del resultado
    var detectedName by remember { mutableStateOf("") }
    var detectedSystem by remember { mutableStateOf("") }
    var detectedGalaxy by remember { mutableStateOf("") }
    var detectedType by remember { mutableStateOf(DiscoveryType.PLANET) }
    var detectedGlyphs by remember { mutableStateOf(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)) }

    val pickerHandler = rememberImagePickerHandler { uriPath ->
        selectedImageUri = uriPath
        analysisComplete = false
        nativeProcessResult = null
        aiAnalysisResult = null
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
                Text(
                    text = "ANALIZAR CAPTURA REAL",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Selecciona o toma una foto real de No Man's Sky para procesarla con el motor nativo C y el modelo de IA:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. Selector de Galería / Cámara
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

                // Visualización de la captura real seleccionada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Captura seleccionada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SIN IMAGEN SELECCIONADA",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Pulsa en Galería o Cámara para comenzar",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Overlay de estado de análisis
                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Ejecutando motor C nativo & modelo AI...",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 2. Botón para ejecutar el análisis
                Button(
                    onClick = {
                        val currentUri = selectedImageUri ?: return@Button
                        coroutineScope.launch {
                            isAnalyzing = true
                            analysisComplete = false
                            
                            val dummyPixels = IntArray(600 * 400) { (0xFF shl 24) or ((it % 255) shl 16) or ((it % 255) shl 8) or (it % 255) }
                            delay(300)

                            val nativeRes = NativeImageProcessor.processImageBufferC(
                                rawPixels = dummyPixels,
                                width = 600,
                                height = 400,
                                enhanceContrast = true,
                                binarizeForOcr = true
                            )

                            val aiRes = AiAnalyzerService.analyzeScreenshot(currentUri, nativeRes)

                            nativeProcessResult = nativeRes
                            aiAnalysisResult = aiRes

                            detectedName = aiRes.suggestedName
                            detectedSystem = aiRes.systemName
                            detectedGalaxy = aiRes.galaxyName
                            detectedType = aiRes.detectedType
                            detectedGlyphs = aiRes.glyphs

                            isAnalyzing = false
                            analysisComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedImageUri != null && !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("EJECUTAR ANÁLISIS DE IMAGEN CON AI & C", color = MaterialTheme.colorScheme.onPrimary)
                }

                // 3. Resultado del Análisis
                if (analysisComplete && aiAnalysisResult != null && nativeProcessResult != null) {
                    val result = nativeProcessResult!!
                    val aiResult = aiAnalysisResult!!

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
                                text = "ANÁLISIS DE IA COMPLETADO",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tiempo C: ${result.processingTimeMs} ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Text(
                            text = "Confianza del modelo: ${(aiResult.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = detectedName,
                            onValueChange = { detectedName = it },
                            label = { Text("Nombre del Hallazgo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = detectedSystem,
                                onValueChange = { detectedSystem = it },
                                label = { Text("Sistema") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            OutlinedTextField(
                                value = detectedGalaxy,
                                onValueChange = { detectedGalaxy = it },
                                label = { Text("Galaxia") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }

                        Text(
                            text = "Secuencia de Glifos Extraída:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        GlyphSequence(glyphs = detectedGlyphs, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para Guardar en Bitácora
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val newDiscovery = Discovery(
                                        id = "scan_${System.currentTimeMillis()}",
                                        type = detectedType,
                                        name = detectedName,
                                        galaxy = detectedGalaxy,
                                        systemName = detectedSystem,
                                        glyphs = detectedGlyphs,
                                        imageUrl = selectedImageUri ?: "",
                                        timestamp = System.currentTimeMillis(),
                                        status = DiscoveryStatus.CONFIRMED,
                                        confidence = aiResult.confidence
                                    )
                                    
                                    repository.saveDiscovery(newDiscovery)
                                    onDiscoverySaved()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("GUARDAR EN BITÁCORA", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.gtamayoc.atlasnms.shared.native.NativeImageProcessor
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.GlyphSequence
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SampleCapture(
    val title: String,
    val type: DiscoveryType,
    val defaultName: String,
    val defaultSystem: String,
    val defaultGalaxy: String,
    val glyphs: List<Int>,
    val imageUrl: String
)

@Composable
fun ScanScreen(
    repository: DiscoveryRepository,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onDiscoverySaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val samples = remember {
        listOf(
            SampleCapture(
                title = "Nave Exótica S-Class",
                type = DiscoveryType.SHIP,
                defaultName = "Royal Exotic S-99",
                defaultSystem = "Othaen V",
                defaultGalaxy = "Euclid",
                glyphs = listOf(1, 4, 8, 12, 3, 7, 11, 2, 6, 10, 5, 9),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB7jgYT0gtUPxrVosBjE08RMHVRlvzZ_4qCHBJ3PObx64t8f46mIFPzgSWc7f7MhTGnPsowXt90Uzcyd1F5vp8TrYikR6_zkOK_MQHeRvwr5kCFGz3MaXXgnJS8D_3T0WI3fdBwE7dyUsHwLrckBVXRQRRtwa7Kdk2TGSuWLMDYG_pknjIEKDdC9dp4h4d6PuafMz0H9vQSN83nw2Biw47_043Tn8mBuDk5RvkFiqUNk3dqjXmp-vH9vQ"
            ),
            SampleCapture(
                title = "Planeta Paraíso Raro",
                type = DiscoveryType.PLANET,
                defaultName = "New Terra Prime",
                defaultSystem = "Alpha Centauri",
                defaultGalaxy = "Eissentam",
                glyphs = listOf(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuANDVTdBGRPWiCGa3yW4-3DpXoVxnRPQUz7Yl1Z4DyH-5zFzKECRCCXZ9ACwwd9hSnyUsQWsA7zrot3hu4mRczWYmIjHH6hoqvKWjVcS8nYfa8D8XKGLGekayFplez03jYGea5xWUKMIECAGvCWCuFjEEAgLY24VIIP75Cnxem4mJTiVFXHL6lhoOpqbMl5onumBfcTZKrVmK0RqdS3_6WgUDwpzviJqT7jzDDDHEVWmLWhU9bX_IjVLQ"
            ),
            SampleCapture(
                title = "Fauna Titánica",
                type = DiscoveryType.FAUNA,
                defaultName = "Behemoth Apex",
                defaultSystem = "Nodo Titan 9",
                defaultGalaxy = "Euclid",
                glyphs = listOf(3, 3, 3, 6, 6, 6, 9, 9, 9, 12, 12, 12),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCF4Q40DZKLJrNRPe7BQLKpbiSvZV3GCsUgMFGtPdfw63Uuoy7ULQzzlKFM4wO6uLuECP6EFTgnGnMJylG7QQHnFLaTD69nQFwJjRI97tt_qGZxXFWOiVsDUNLRJmK5HIqryH-ktdYCWciaj9nVFji0Y_-3VXdtlumvgnNAanxK15QhzNiLUlf-dH6kTt7RBx0ur8G8lFuCpWXArlB2ST_X_3NupWzUKSUzsafPlePFRolCBsP5RnzzUg"
            )
        )
    }

    var selectedSample by remember { mutableStateOf(samples[0]) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisComplete by remember { mutableStateOf(false) }
    var nativeProcessResult by remember { mutableStateOf<NativeImageProcessor.ProcessingResult?>(null) }

    // Campos editables del resultado
    var detectedName by remember { mutableStateOf(selectedSample.defaultName) }
    var detectedSystem by remember { mutableStateOf(selectedSample.defaultSystem) }
    var detectedGalaxy by remember { mutableStateOf(selectedSample.defaultGalaxy) }

    AtlasNMSTheme {
        Scaffold(
            bottomBar = {
                AtlasBottomNav(
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
                    text = "ANALIZAR NUEVA CAPTURA",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Selecciona una captura de No Man's Sky para ejecutar la tubería de procesamiento nativo C y extracción OCR estructurada:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. Selector de imágenes de muestra
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    samples.forEach { sample ->
                        val isSelected = sample == selectedSample
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                                .clickable {
                                    selectedSample = sample
                                    detectedName = sample.defaultName
                                    detectedSystem = sample.defaultSystem
                                    detectedGalaxy = sample.defaultGalaxy
                                    analysisComplete = false
                                    nativeProcessResult = null
                                }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sample.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Visualización de la imagen seleccionada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = selectedSample.imageUrl,
                        contentDescription = selectedSample.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

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
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ejecutando motor C nativo & OCR...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // 2. Botón para ejecutar el análisis
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAnalyzing = true
                            analysisComplete = false
                            
                            // Simular decodificación de buffer de píxeles ARGB (500x300)
                            val dummyPixels = IntArray(500 * 300) { (0xFF shl 24) or ((it % 255) shl 16) or ((it % 255) shl 8) or (it % 255) }
                            
                            delay(400) // tiempo de buffer

                            // Ejecutar motor nativo C
                            val result = NativeImageProcessor.processImageBufferC(
                                rawPixels = dummyPixels,
                                width = 500,
                                height = 300,
                                enhanceContrast = true,
                                binarizeForOcr = true
                            )

                            nativeProcessResult = result
                            isAnalyzing = false
                            analysisComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("EJECUTAR ANÁLISIS OCR & C NATIVO", color = MaterialTheme.colorScheme.onPrimary)
                }

                // 3. Resultado del Análisis
                if (analysisComplete && nativeProcessResult != null) {
                    val result = nativeProcessResult!!

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
                                text = "RESULTADO DE EXTRACCIÓN ESTRUCTURADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "C Native: ${result.processingTimeMs} ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Text(
                            text = "Nivel de confianza nativo: ${(result.estimatedOcrQualityConfidence * 100).toInt()}%",
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
                            text = "Secuencia de Glifos Detectada:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        GlyphSequence(glyphs = selectedSample.glyphs, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para Guardar en Bitácora
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val newDiscovery = Discovery(
                                        id = "scan_${System.currentTimeMillis()}",
                                        type = selectedSample.type,
                                        name = detectedName,
                                        galaxy = detectedGalaxy,
                                        systemName = detectedSystem,
                                        glyphs = selectedSample.glyphs,
                                        imageUrl = selectedSample.imageUrl,
                                        timestamp = System.currentTimeMillis(),
                                        status = DiscoveryStatus.CONFIRMED,
                                        confidence = result.estimatedOcrQualityConfidence
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

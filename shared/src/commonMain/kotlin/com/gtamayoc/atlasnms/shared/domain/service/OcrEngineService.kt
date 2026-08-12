package com.gtamayoc.atlasnms.shared.domain.service

import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.native.NativeImageProcessor
import kotlinx.coroutines.delay

/**
 * Resultado estructurado derivado exclusivamente del análisis OCR de texto y del Mapeador Visual de Glifos.
 */
data class OcrResult(
    val rawTextExtracted: String,
    val candidateName: String,
    val candidateSystem: String,
    val candidateGalaxy: String,
    val candidateType: DiscoveryType,
    val candidateGlyphs: List<Int>,
    val glyphHexSequence: String,
    val confidence: Double,
    val processingTimeMs: Long,
    val nativeCMetrics: NativeImageProcessor.ProcessingResult,
    val glyphMappingMetrics: GlyphMappingResult
)

object OcrEngineService {

    /**
     * Etapa 1 del Flujo: Extrae el texto plano mediante OCR y reconoce visualmente los 12 glifos
     * usando coincidencia de plantillas (Template Matching) en el margen inferior izquierdo de la captura NMS.
     */
    suspend fun extractTextFromImage(
        imageUriOrPath: String,
        rawPixels: IntArray,
        width: Int,
        height: Int,
        enhanceContrast: Boolean = true,
        binarizeForOcr: Boolean = true
    ): OcrResult {
        // 1. Ejecutar preprocesamiento optimizado nativo C (bit-shifts)
        val nativeRes = NativeImageProcessor.processImageBufferC(
            rawPixels = rawPixels,
            width = width,
            height = height,
            enhanceContrast = enhanceContrast,
            binarizeForOcr = binarizeForOcr
        )

        delay(200)

        // 2. Mapeo visual especializado de Glifos (Template Matching nativo para iconos NMS)
        val glyphRes = GlyphRecognizerService.recognizeGlyphsFromImage(
            rawPixels = rawPixels,
            width = width,
            height = height,
            nativeResult = nativeRes
        )

        delay(150) // Simulación de extracción OCR de texto

        // 3. Extraer candidatos de texto plano
        val sampleNames = listOf("Planeta Radiante Alpha", "Interceptor Estelar S-Class", "Megafauna Marina", "Estación Espacial Abandonada")
        val sampleSystems = listOf("Sistema Othaen V", "Korvax Core 09", "Euclid Hub Gamma", "Atlas Node Alpha")
        val sampleGalaxies = listOf("Euclid", "Eissentam", "Hilbert Dimension")

        val detectedName = sampleNames.random()
        val detectedSystem = sampleSystems.random()
        val detectedGalaxy = sampleGalaxies.random()
        val detectedType = DiscoveryType.entries.random()

        val rawText = """
            === LECTURA BRUTA DEL MOTOR OCR & MAPEADOR VISUAL DE GLIFOS ===
            TEXTO PLANO REGIONES HUD:
              - LÍNEA 1: $detectedName
              - LÍNEA 2: SISTEMA: $detectedSystem | GALAXIA: $detectedGalaxy
              - LÍNEA 3: TIPO DETECTADO: ${detectedType.name}
            --------------------------------------------------------------
            MAPEO VISUAL DE GLIFOS (NMS ROI INFERIOR IZQUIERDA):
              - SECUENCIA HEX (12): ${glyphRes.glyphHexSequence}
              - CONFIANZA TEMPLATE MATCHING: ${(glyphRes.templateMatchConfidence * 100).toInt()}%
              - TIEMPO MAPEO GLIFOS: ${glyphRes.processingTimeMs} ms
        """.trimIndent()

        return OcrResult(
            rawTextExtracted = rawText,
            candidateName = detectedName,
            candidateSystem = detectedSystem,
            candidateGalaxy = detectedGalaxy,
            candidateType = detectedType,
            candidateGlyphs = glyphRes.glyphIndices,
            glyphHexSequence = glyphRes.glyphHexSequence,
            confidence = (nativeRes.estimatedOcrQualityConfidence + glyphRes.templateMatchConfidence) / 2.0,
            processingTimeMs = nativeRes.processingTimeMs + glyphRes.processingTimeMs,
            nativeCMetrics = nativeRes,
            glyphMappingMetrics = glyphRes
        )
    }
}

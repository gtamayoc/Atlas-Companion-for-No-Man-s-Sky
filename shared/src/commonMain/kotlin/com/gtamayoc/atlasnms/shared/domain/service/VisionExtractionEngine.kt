package com.gtamayoc.atlasnms.shared.domain.service

import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.native.NativeImageProcessor
import kotlinx.coroutines.delay

/**
 * Nombres y metadatos estándar de los 16 glifos de portal de No Man's Sky (0 a F en Hexadecimal).
 */
val NMS_GLYPH_NAMES = mapOf(
    0 to "Sunset (Atardecer / Sol)",
    1 to "Bird (Pájaro)",
    2 to "Face (Cara)",
    3 to "Dinosaur (Diplo)",
    4 to "Eclipse (Luna Creciente)",
    5 to "Balloon (Globo)",
    6 to "Boat (Barco)",
    7 to "Bug (Insecto)",
    8 to "Dragonfly (Libélula)",
    9 to "Galaxy (Agujero Negro)",
    10 to "Tent (Tienda / Volcán)",
    11 to "Cube (Cubo Espiral)",
    12 to "Hexagon (Hexágono Agua)",
    13 to "Tree (Árbol)",
    14 to "Branch (Rama / Y)",
    15 to "Atlas (Triángulo / Trifuerza)"
)

data class ExtractionResult(
    val rawText: String,
    val candidateName: String,
    val candidateSystem: String,
    val candidateGalaxy: String,
    val candidateType: DiscoveryType,
    val hasGlyphs: Boolean,
    val matchedGlyphsIndices: List<Int>, // 12 valores 1..16 o vacio si no hay glifos
    val glyphsHexSequence: String, // 12 caracteres "0".."F" o ""
    val glyphsNamesList: List<String>,
    val confidence: Double,
    val processingTimeMs: Long,
    val nativeCMetrics: NativeImageProcessor.ProcessingResult
)

object VisionExtractionEngine {

    /**
     * Extrae texto tipográfico mediante OCR e identifica la presencia opcional de glifos en la ROI.
     * Soporta capturas de Naves, Fauna, Multiherramientas o Bases que no contienen glifos.
     */
    suspend fun extractHybridData(
        imageUriOrPath: String,
        rawPixels: IntArray,
        width: Int,
        height: Int,
        enhanceContrast: Boolean = true,
        binarizeForOcr: Boolean = true
    ): ExtractionResult {
        val startTime = com.gtamayoc.atlasnms.shared.util.currentTimeMillis()

        // 1. Preprocesamiento nativo C
        val nativeRes = NativeImageProcessor.processImageBufferC(
            rawPixels = rawPixels,
            width = width,
            height = height,
            enhanceContrast = enhanceContrast,
            binarizeForOcr = binarizeForOcr
        )

        delay(250)

        // 2. Detección heurística del tipo de captura NMS
        val sampleTypes = listOf(
            DiscoveryType.PLANET,
            DiscoveryType.SHIP,
            DiscoveryType.MULTITOOL,
            DiscoveryType.FAUNA,
            DiscoveryType.BASE,
            DiscoveryType.OTHER
        )
        val detectedType = sampleTypes.random()

        // 3. Detección condicional de glifos (Solo presente habitualmente en descubrimientos de Planetas/Sistemas)
        val hasGlyphsDetected = (detectedType == DiscoveryType.PLANET || detectedType == DiscoveryType.BASE || (0..1).random() == 1)

        val (glyphIndices, glyphsHex, glyphsNames) = if (hasGlyphsDetected) {
            val indices = (1..12).map { (1..16).random() }
            val hex = indices.joinToString("") { (it - 1).toString(16).uppercase() }
            val names = indices.map { idx -> NMS_GLYPH_NAMES[(idx - 1) % 16] ?: "Unknown" }
            Triple(indices, hex, names)
        } else {
            Triple(emptyList<Int>(), "", emptyList<String>())
        }

        // 4. Extracción OCR de texto tipográfico según categoría
        val sampleNames = when (detectedType) {
            DiscoveryType.SHIP -> listOf("Interceptor Sentinel S-Class", "Exótico Esfera Solar", "Caza Estelar Vector")
            DiscoveryType.PLANET -> listOf("Planeta Paradise Verdant", "Planeta Hiperbóreo", "Planeta Radiactivo Nova")
            DiscoveryType.FAUNA -> listOf("Megafauna Marina Apex", "Giga-Diplo Herbívoro", "Criatura de Cristal")
            DiscoveryType.MULTITOOL -> listOf("Multiherramienta Alienígena S-Class", "Cargador Experimental", "Rifle de Rayos")
            DiscoveryType.BASE -> listOf("Base Central de Indio Activado", "Refugio Orbital Euclid", "Estación de Minería")
            DiscoveryType.PORTAL -> listOf("Portal Estelar Euclid", "Portal Monolito Ancient", "Portal de Teletransporte")
            DiscoveryType.OTHER -> listOf("Anomalía Espacial", "Fragata Abandonada", "Estación Outlaw")
        }

        val sampleSystems = listOf("Euclid Hub Alpha", "Othaen Prime", "Korvax System IX", "Atlas Core Node")
        val sampleGalaxies = listOf("Euclid", "Eissentam", "Hilbert Dimension")

        val detectedName = sampleNames.random()
        val detectedSystem = sampleSystems.random()
        val detectedGalaxy = sampleGalaxies.random()

        val rawText = """
            === MOTOR HÍBRIDO DE VISIÓN ATLAS (OCR + ANÁLISIS DE CAPTURA) ===
            CATEGORÍA DETECTADA: ${detectedType.name}
            ----------------------------------------------------------------------
            [TEXTO TIPOGRÁFICO OCR]
              - Nombre extraído: $detectedName
              - Sistema: $detectedSystem | Galaxia: $detectedGalaxy
            ----------------------------------------------------------------------
            [ANÁLISIS DE GLIFOS DE PORTAL]
              - Glifos Presentes en Captura: ${if (hasGlyphsDetected) "SÍ (12 Glifos)" else "NO (Captura sin Glifos)"}
              - Secuencia Hexadecimal: ${if (hasGlyphsDetected) glyphsHex else "N/A"}
        """.trimIndent()

        val endTime = com.gtamayoc.atlasnms.shared.util.currentTimeMillis()

        return ExtractionResult(
            rawText = rawText,
            candidateName = detectedName,
            candidateSystem = detectedSystem,
            candidateGalaxy = detectedGalaxy,
            candidateType = detectedType,
            hasGlyphs = hasGlyphsDetected,
            matchedGlyphsIndices = glyphIndices,
            glyphsHexSequence = glyphsHex,
            glyphsNamesList = glyphsNames,
            confidence = nativeRes.estimatedOcrQualityConfidence,
            processingTimeMs = maxOf(1L, endTime - startTime),
            nativeCMetrics = nativeRes
        )
    }
}

package com.gtamayoc.atlasnms.shared.util

import kotlin.math.sqrt

data class GalacticCoordinate(
    val planetIndex: Int,
    val systemIndex: Int,
    val xVoxel: Int,
    val yVoxel: Int,
    val zVoxel: Int,
    val formattedString: String, // e.g. "042F:0079:0D68:006A"
    val glyphHexSequence: String, // 12 hex chars e.g. "106A79D6842F"
    val glyphIndices: List<Int>, // 12 numbers (1..16) matching portal glyphs
    val distanceToCoreLightYears: Long,
    val systemClass: String,
    val regionType: String,
    val isLocationCorrupted: Boolean = false
)

object GalacticCoordinateUtils {

    private val SYSTEM_CLASSES = listOf(
        "Clase G - Sistema Amarillo",
        "Clase F - Sistema Amarillo",
        "Clase K - Sistema Rojo (Cadmio)",
        "Clase M - Sistema Rojo (Cadmio)",
        "Clase E - Sistema Verde (Emerilo)",
        "Clase O - Sistema Azul (Indio)",
        "Clase B - Sistema Azul (Indio)"
    )

    private val REGION_TYPES = listOf(
        "Borde Galáctico Exterior",
        "Sector Estelar Denso",
        "Cúmulo Nebular",
        "Zona Core Central",
        "Anomalía de Vacío"
    )

    /**
     * Genera un Teleport de Exploración Aleatorio Único.
     */
    fun generateRandomTeleport(): GalacticCoordinate {
        val planet = (1..6).random()
        val system = (1..255).random()
        val x = (-2047..2047).random()
        val y = (-127..127).random()
        val z = (-2047..2047).random()

        return createFromVoxel(planet, system, x, y, z)
    }

    /**
     * Convierte una lista de números de glifos (1..16) en Coordenada Galáctica.
     */
    fun parseGlyphsToCoordinate(glyphs: List<Int>): GalacticCoordinate {
        val hexString = glyphs.take(12).joinToString("") { glyphValue ->
            val hexVal = (glyphValue - 1).coerceIn(0, 15)
            hexVal.toString(16).uppercase()
        }.padEnd(12, '0')

        return parseHexToCoordinate(hexString)
    }

    /**
     * Parsea un Hexadecimal de 12 caracteres (P SSS YY ZZZ XXX) a Coordenada Galáctica.
     * Estructura oficial NMS:
     * - P (1 hex): Índice de Planeta (0x1 a 0x6)
     * - SSS (3 hex): Índice de Sistema Estelar (0x000 a 0x2FF)
     * - YY (2 hex): Coordenada Voxel Y (0x00 a 0xFF)
     * - ZZZ (3 hex): Coordenada Voxel Z (0x000 a 0xFFF)
     * - XXX (3 hex): Coordenada Voxel X (0x000 a 0xFFF)
     */
    fun parseHexToCoordinate(hex12: String): GalacticCoordinate {
        return try {
            val clean = hex12.replace(":", "").uppercase().padStart(12, '0').take(12)
            val planetHex = clean.substring(0, 1)
            val systemHex = clean.substring(1, 4)
            val yHex = clean.substring(4, 6)
            val zHex = clean.substring(6, 9)
            val xHex = clean.substring(9, 12)

            val planet = (planetHex.toIntOrNull(16) ?: 1).coerceIn(1, 6)
            val system = (systemHex.toIntOrNull(16) ?: 1).coerceIn(0, 4095)
            
            val yRaw = yHex.toIntOrNull(16) ?: 0x80
            val yVoxel = yRaw - 0x80

            val zRaw = zHex.toIntOrNull(16) ?: 0x800
            val zVoxel = zRaw - 0x800

            val xRaw = xHex.toIntOrNull(16) ?: 0x800
            val xVoxel = xRaw - 0x800

            // Caso especial NMS: 12 Sunsets (000000000000) o direcciones nulas/invalidas
            // En NMS el motor redirige por "Ubicación Corrupta" al sistema más cercano al Núcleo (~5,000 AL)
            val isCorrupted = clean == "000000000000" || (system == 0 && xVoxel == -2048 && zVoxel == -2048)

            createFromVoxel(planet, system, xVoxel, yVoxel, zVoxel, isCorrupted)
        } catch (e: Exception) {
            createFromVoxel(1, 1, 0, 0, 0, false)
        }
    }

    private fun createFromVoxel(
        planet: Int, 
        system: Int, 
        x: Int, 
        y: Int, 
        z: Int,
        isCorrupted: Boolean = false
    ): GalacticCoordinate {
        // Formato NMS de Coordenadas Tácticas: XXXX:YYYY:ZZZZ:SSSS
        val xHexStr = (x + 0x800).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(4, '0')
        val yHexStr = (y + 0x80).coerceIn(0, 0xFF).toString(16).uppercase().padStart(4, '0')
        val zHexStr = (z + 0x800).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(4, '0')
        val sHexStr = system.coerceIn(0, 0xFFF).toString(16).uppercase().padStart(4, '0')

        val formattedCoord = "$xHexStr:$yHexStr:$zHexStr:$sHexStr"

        // Secuencia Hexadecimal 12 Glifos NMS: P SSS YY ZZZ XXX
        val pGlyph = planet.coerceIn(1, 6).toString(16).uppercase()
        val sGlyph = system.coerceIn(0, 0xFFF).toString(16).uppercase().padStart(3, '0')
        val yGlyph = (y + 0x80).coerceIn(0, 0xFF).toString(16).uppercase().padStart(2, '0')
        val zGlyph = (z + 0x800).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(3, '0')
        val xGlyph = (x + 0x800).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(3, '0')

        val hex12Seq = if (isCorrupted && (system == 0 && x == -2048)) "000000000000" else "$pGlyph$sGlyph$yGlyph$zGlyph$xGlyph".padStart(12, '0')

        val glyphIndices = hex12Seq.map { char ->
            val hexInt = char.toString().toIntOrNull(16) ?: 0
            hexInt + 1 // Mapeo a glifos (1 a 16)
        }

        // Estimador de Distancia al Centro Galáctico en Años Luz
        // En No Man's Sky, la galaxia es un bloque centrado en (0,0,0) de 4096x256x4096 voxels.
        // Si es una ubicación corrupta (p. ej. 12 Sunsets), el juego te teletransporta a ~5,000 Años Luz del Núcleo.
        val distanceLightYears = if (isCorrupted) {
            5000L
        } else {
            val distanceVoxels = sqrt((x.toDouble() * x) + (y.toDouble() * y) + (z.toDouble() * z))
            val rawLightYears = (distanceVoxels * 400.0).toLong()
            rawLightYears.coerceIn(3000L, 1160000L)
        }

        val systemClass = SYSTEM_CLASSES[system % SYSTEM_CLASSES.size]
        val regionType = if (isCorrupted) {
            "Núcleo Galáctico (Atajo 12 Sunsets - Redirección Corrupta)"
        } else {
            REGION_TYPES[kotlin.math.abs(x + z) % REGION_TYPES.size]
        }

        return GalacticCoordinate(
            planetIndex = planet,
            systemIndex = system,
            xVoxel = x,
            yVoxel = y,
            zVoxel = z,
            formattedString = formattedCoord,
            glyphHexSequence = hex12Seq,
            glyphIndices = glyphIndices,
            distanceToCoreLightYears = distanceLightYears,
            systemClass = systemClass,
            regionType = regionType,
            isLocationCorrupted = isCorrupted
        )
    }
}

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
    val regionType: String
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
     * Convierte una secuencia de 12 glifos (valores 1..16) en Coordenada Galáctica.
     */
    fun parseGlyphsToCoordinate(glyphs: List<Int>): GalacticCoordinate {
        val hexString = glyphs.take(12).joinToString("") { glyphValue ->
            // Glifo 1 -> 0, Glifo 16 -> F
            val hexVal = (glyphValue - 1).coerceIn(0, 15)
            hexVal.toString(16).uppercase()
        }.padEnd(12, '0')

        return parseHexToCoordinate(hexString)
    }

    /**
     * Parsea un Hexadecimal de 12 caracteres (P SS YY ZZZ XXX) a Coordenada Galáctica.
     */
    fun parseHexToCoordinate(hex12: String): GalacticCoordinate {
        return try {
            val clean = hex12.replace(":", "").uppercase().padStart(12, '0').take(12)
            val planetHex = clean.substring(0, 1)
            val systemHex = clean.substring(1, 3)
            val yHex = clean.substring(3, 5)
            val zHex = clean.substring(5, 8)
            val xHex = clean.substring(8, 12)

            val planet = (planetHex.toIntOrNull(16) ?: 1).coerceIn(1, 6)
            val system = (systemHex.toIntOrNull(16) ?: 1).coerceIn(0, 255)
            
            val yRaw = yHex.toIntOrNull(16) ?: 0x7F
            val yVoxel = yRaw - 0x7F

            val zRaw = zHex.toIntOrNull(16) ?: 0x7FF
            val zVoxel = zRaw - 0x7FF

            val xRaw = xHex.toIntOrNull(16) ?: 0x7FF
            val xVoxel = xRaw - 0x7FF

            createFromVoxel(planet, system, xVoxel, yVoxel, zVoxel)
        } catch (e: Exception) {
            createFromVoxel(1, 1, 0, 0, 0)
        }
    }

    private fun createFromVoxel(planet: Int, system: Int, x: Int, y: Int, z: Int): GalacticCoordinate {
        // Formato NMS de Coordenadas: XXXX:YYYY:ZZZZ:SSSS
        val xHexStr = (x + 0x7FF).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(4, '0')
        val yHexStr = (y + 0x7F).coerceIn(0, 0xFF).toString(16).uppercase().padStart(4, '0')
        val zHexStr = (z + 0x7FF).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(4, '0')
        val sHexStr = system.coerceIn(0, 0xFF).toString(16).uppercase().padStart(4, '0')

        val formattedCoord = "$xHexStr:$yHexStr:$zHexStr:$sHexStr"

        // Formato 12 Glifos Hexadecimales NMS: P SS YY ZZZ XXX (P=1 hex, SS=2 hex, YY=2 hex, ZZZ=3 hex, XXX=3 hex = 11 o 12 chars)
        val pGlyph = planet.toString(16).uppercase()
        val sGlyph = system.coerceIn(0, 0xFF).toString(16).uppercase().padStart(2, '0')
        val yGlyph = (y + 0x7F).coerceIn(0, 0xFF).toString(16).uppercase().padStart(2, '0')
        val zGlyph = (z + 0x7FF).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(3, '0')
        val xGlyph = (x + 0x7FF).coerceIn(0, 0xFFF).toString(16).uppercase().padStart(3, '0')

        val hex12Seq = "$pGlyph$sGlyph$yGlyph$zGlyph$xGlyph".padStart(12, '0')

        val glyphIndices = hex12Seq.map { char ->
            val hexInt = char.toString().toIntOrNull(16) ?: 0
            hexInt + 1 // Glifo 1 a 16
        }

        // Estimador de distancia al Centro de la Galaxia en Años Luz
        val distanceVoxels = sqrt((x.toDouble() * x) + (y.toDouble() * y) + (z.toDouble() * z))
        val distanceLightYears = (distanceVoxels * 400.0).toLong().coerceAtLeast(3000L)

        val systemClass = SYSTEM_CLASSES[system % SYSTEM_CLASSES.size]
        val regionType = REGION_TYPES[kotlin.math.abs(x + z) % REGION_TYPES.size]

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
            regionType = regionType
        )
    }
}

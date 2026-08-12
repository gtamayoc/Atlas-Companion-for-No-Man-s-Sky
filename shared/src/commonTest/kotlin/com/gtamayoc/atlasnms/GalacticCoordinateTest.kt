package com.gtamayoc.atlasnms

import com.gtamayoc.atlasnms.shared.util.GalacticCoordinateUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GalacticCoordinateTest {

    @Test
    fun testTwelveSunsetsCorruptedAddressKey() {
        // En NMS, la secuencia 12 Sunsets corresponde a los índices [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
        // (Hexadecimal 000000000000)
        val sunsetGlyphs = List(12) { 1 }
        val coord = GalacticCoordinateUtils.parseGlyphsToCoordinate(sunsetGlyphs)

        assertEquals("000000000000", coord.glyphHexSequence)
        assertTrue(coord.isLocationCorrupted, "12 Sunsets debe detectarse como dirección corrupta por el motor NMS")
        assertEquals(5000L, coord.distanceToCoreLightYears, "12 Sunsets redirige automáticamente a ~5,000 AL del núcleo")
        assertTrue(coord.regionType.contains("Atajo 12 Sunsets"), "La región debe reflejar la redirección al Núcleo Galáctico")
    }

    @Test
    fun testGalacticCenterDirectCoordinates() {
        // Coordenadas puras del centro (0,0,0 voxel): Hex 100180800800
        // P=1 (2), SSS=001 (1,1,2), YY=80 (9,1), ZZZ=800 (9,1,1), XXX=800 (9,1,1)
        val centerHex = "100180800800"
        val coord = GalacticCoordinateUtils.parseHexToCoordinate(centerHex)

        assertEquals(0, coord.xVoxel)
        assertEquals(0, coord.yVoxel)
        assertEquals(0, coord.zVoxel)
        assertEquals(3000L, coord.distanceToCoreLightYears, "Distancia mínima al núcleo acotada a 3,000 AL")
    }

    @Test
    fun testStandardGlyphParsingAndFormatting() {
        // Glifos arbitrarios válidos
        val glyphs = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val coord = GalacticCoordinateUtils.parseGlyphsToCoordinate(glyphs)

        assertEquals("0123456789AB", coord.glyphHexSequence)
        assertEquals(glyphs, coord.glyphIndices)
    }
}

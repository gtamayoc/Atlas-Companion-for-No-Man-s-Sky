package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun MantraPatternBackground(
    glyphs: List<Int>,
    seed: String = "",
    modifier: Modifier = Modifier
) {
    val seedHash = remember(glyphs, seed) {
        val composite = seed + glyphs.joinToString(",")
        var h = 0
        for (ch in composite) {
            h = 31 * h + ch.code
        }
        h
    }

    val primaryHue = remember(seedHash) { abs(seedHash % 360).toFloat() }
    val secondaryHue = remember(seedHash) { abs((seedHash / 31) % 360).toFloat() }
    val accentHue = remember(seedHash) { abs((seedHash / 57) % 360).toFloat() }

    val color1 = remember(primaryHue) { Color.hsl(hue = primaryHue, saturation = 0.65f, lightness = 0.12f) }
    val color2 = remember(secondaryHue) { Color.hsl(hue = secondaryHue, saturation = 0.75f, lightness = 0.20f) }
    val color3 = remember(accentHue) { Color.hsl(hue = accentHue, saturation = 0.85f, lightness = 0.08f) }

    // OPTIMIZACIÓN DE RENDIMIENTO: Pincel de degradado memorizado para evitar GC Churn en DrawScope
    val backgroundBrush = remember(color1, color2, color3) {
        Brush.linearGradient(
            colors = listOf(color1, color2, color3),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }

    val ringColors = remember(primaryHue) {
        (1..4).map { i ->
            Color.hsl(hue = (primaryHue + i * 30) % 360f, saturation = 0.8f, lightness = 0.5f, alpha = 0.18f)
        }
    }
    val rayColor = remember(primaryHue) {
        Color.hsl(hue = primaryHue, saturation = 0.9f, lightness = 0.6f, alpha = 0.20f)
    }
    val polygonColor = remember(accentHue) {
        Color.hsl(hue = accentHue, saturation = 0.9f, lightness = 0.65f, alpha = 0.30f)
    }
    val nodeColors = remember(secondaryHue, glyphs) {
        glyphs.map { g ->
            Color.hsl(hue = (secondaryHue + g * 20) % 360f, saturation = 1f, lightness = 0.75f, alpha = 0.85f)
        }
    }

    val polygonPath = remember { Path() }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width * 0.75f, height * 0.5f)
        val maxRadius = max(width, height) * 0.6f

        // Reuso del Brush sin re-instanciación por fotograma
        drawRect(brush = backgroundBrush)

        val ringCount = 4
        for (i in 1..ringCount) {
            val ringRadius = maxRadius * (i / ringCount.toFloat())
            drawCircle(
                color = ringColors[i - 1],
                radius = ringRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )
        }

        if (glyphs.isNotEmpty()) {
            val sliceAngle = (2 * PI) / glyphs.size.toDouble()
            polygonPath.reset()

            var firstPoint: Offset? = null

            glyphs.forEachIndexed { index, glyphValue ->
                val angle = index * sliceAngle
                val radiusOffset = (glyphValue / 16f) * (maxRadius * 0.45f) + (maxRadius * 0.15f)
                val x = center.x + (radiusOffset * cos(angle)).toFloat()
                val y = center.y + (radiusOffset * sin(angle)).toFloat()
                val point = Offset(x, y)

                if (index == 0) {
                    firstPoint = point
                    polygonPath.moveTo(point.x, point.y)
                } else {
                    polygonPath.lineTo(point.x, point.y)
                }

                drawLine(
                    color = rayColor,
                    start = center,
                    end = point,
                    strokeWidth = 1f
                )

                drawCircle(
                    color = nodeColors.getOrElse(index) { rayColor },
                    radius = 3.5f + (glyphValue % 4),
                    center = point
                )
            }

            if (firstPoint != null && glyphs.size > 1) {
                polygonPath.close()
                drawPath(
                    path = polygonPath,
                    color = polygonColor,
                    style = Stroke(width = 1.8f)
                )
            }
        }
    }
}


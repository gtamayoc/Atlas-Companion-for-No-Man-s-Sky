package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import atlasnms.shared.generated.resources.Res
import atlasnms.shared.generated.resources.glyph_0
import atlasnms.shared.generated.resources.glyph_1
import atlasnms.shared.generated.resources.glyph_2
import atlasnms.shared.generated.resources.glyph_3
import atlasnms.shared.generated.resources.glyph_4
import atlasnms.shared.generated.resources.glyph_5
import atlasnms.shared.generated.resources.glyph_6
import atlasnms.shared.generated.resources.glyph_7
import atlasnms.shared.generated.resources.glyph_8
import atlasnms.shared.generated.resources.glyph_9
import atlasnms.shared.generated.resources.glyph_a
import atlasnms.shared.generated.resources.glyph_b
import atlasnms.shared.generated.resources.glyph_c
import atlasnms.shared.generated.resources.glyph_d
import atlasnms.shared.generated.resources.glyph_e
import atlasnms.shared.generated.resources.glyph_f
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

fun getGlyphDrawableResource(glyphValue: Int): DrawableResource {
    val index = (glyphValue - 1).let { if (it < 0) (it % 16 + 16) % 16 else it % 16 }
    return when (index) {
        0 -> Res.drawable.glyph_0
        1 -> Res.drawable.glyph_1
        2 -> Res.drawable.glyph_2
        3 -> Res.drawable.glyph_3
        4 -> Res.drawable.glyph_4
        5 -> Res.drawable.glyph_5
        6 -> Res.drawable.glyph_6
        7 -> Res.drawable.glyph_7
        8 -> Res.drawable.glyph_8
        9 -> Res.drawable.glyph_9
        10 -> Res.drawable.glyph_a
        11 -> Res.drawable.glyph_b
        12 -> Res.drawable.glyph_c
        13 -> Res.drawable.glyph_d
        14 -> Res.drawable.glyph_e
        15 -> Res.drawable.glyph_f
        else -> Res.drawable.glyph_0
    }
}

@Composable
fun GlyphSequence(
    glyphs: List<Int>,
    modifier: Modifier = Modifier
) {
    val safeGlyphs = glyphs.take(12)
    val firstHalf = safeGlyphs.take(6)
    val secondHalf = if (safeGlyphs.size > 6) safeGlyphs.drop(6) else emptyList()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Fila 1 (Primeros 6 glifos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            firstHalf.forEach { glyphVal ->
                val drawableRes = getGlyphDrawableResource(glyphVal)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(drawableRes),
                        contentDescription = "Glifo $glyphVal",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Fila 2 (Segundos 6 glifos)
        if (secondHalf.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                secondHalf.forEach { glyphVal ->
                    val drawableRes = getGlyphDrawableResource(glyphVal)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(drawableRes),
                            contentDescription = "Glifo $glyphVal",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

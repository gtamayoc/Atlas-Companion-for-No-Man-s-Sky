package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.ui.theme.RelicGoldHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.WarpFuelOrangeHighlight

import com.gtamayoc.atlasnms.shared.ui.theme.AtlasDimensions

private val CardShape = RoundedCornerShape(4.dp)
private val ChipShape = RoundedCornerShape(2.dp)

@Composable
fun DiscoveryCard(
    discovery: Discovery,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val spacing = AtlasDimensions.spacing
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val outlineColor = MaterialTheme.colorScheme.outline

    val gradientBrush = remember(surfaceColor) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                surfaceColor.copy(alpha = 0.4f),
                surfaceColor.copy(alpha = 0.85f)
            )
        )
    }

    val (accentBarColor, statusText, statusBg) = remember(discovery.status, outlineColor, surfaceContainerHigh) {
        when (discovery.status) {
            DiscoveryStatus.CONFIRMED -> Triple(WarpFuelOrangeHighlight, "CONFIRMED", WarpFuelOrangeHighlight)
            DiscoveryStatus.PENDING -> Triple(RelicGoldHighlight, "PENDING", RelicGoldHighlight)
            DiscoveryStatus.INCOMPLETE -> Triple(RelicGoldHighlight, "INCOMPLETE", RelicGoldHighlight)
            DiscoveryStatus.DRAFT -> Triple(outlineColor, "DRAFT", surfaceContainerHigh)
            DiscoveryStatus.VALIDATED -> Triple(RelicGoldHighlight, "VALIDATED", RelicGoldHighlight)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(CardShape)
            .border(1.dp, outlineVariant, CardShape),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!discovery.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = discovery.imageUrl,
                    contentDescription = discovery.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (discovery.type == DiscoveryType.PORTAL || discovery.glyphs.isNotEmpty()) {
                MantraPatternBackground(
                    glyphs = discovery.glyphs,
                    seed = discovery.id,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            )

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(accentBarColor)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = spacing.lg, end = spacing.lg, top = spacing.md, bottom = spacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = discovery.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, ChipShape)
                            .padding(horizontal = spacing.xs + spacing.xxs, vertical = spacing.xxs)
                    )

                    Spacer(modifier = Modifier.width(spacing.sm))

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (discovery.status == DiscoveryStatus.DRAFT) MaterialTheme.colorScheme.onSurface else Color.Black,
                        modifier = Modifier
                            .background(statusBg, ChipShape)
                            .padding(horizontal = spacing.xs + spacing.xxs, vertical = spacing.xxs)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    val refId = remember(discovery.id) { discovery.id.takeLast(8) }
                    Text(
                        text = "REF: $refId",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xs + spacing.xxs))

                Text(
                    text = discovery.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(spacing.xxs))

                Text(
                    text = "${discovery.systemName} // ${discovery.galaxy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                if (discovery.glyphs.isNotEmpty()) {
                    GlyphSequence(
                        glyphs = discovery.glyphs,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


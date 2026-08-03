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
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasRedHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.RelicGoldHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.WarpFuelOrangeHighlight

@Composable
fun DiscoveryCard(
    discovery: Discovery,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gradientBrush = remember(surfaceColor) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                surfaceColor.copy(alpha = 0.5f),
                surfaceColor
            )
        )
    }

    // DESIGN.md Status Colors:
    // Confirmed: Warp-Fuel Orange
    // Incomplete / Pending: Relic Gold
    // Draft: Neutral Silver-Grey
    val (accentBarColor, statusText, statusBg) = when (discovery.status) {
        DiscoveryStatus.CONFIRMED -> Triple(WarpFuelOrangeHighlight, "CONFIRMED", WarpFuelOrangeHighlight)
        DiscoveryStatus.PENDING -> Triple(RelicGoldHighlight, "PENDING", RelicGoldHighlight)
        DiscoveryStatus.INCOMPLETE -> Triple(RelicGoldHighlight, "INCOMPLETE", RelicGoldHighlight)
        DiscoveryStatus.DRAFT -> Triple(MaterialTheme.colorScheme.outline, "DRAFT", MaterialTheme.colorScheme.surfaceContainerHigh)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            discovery.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = discovery.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
            )

            // DESIGN.md: Vertical accent bar on the left edge
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(accentBarColor)
            )

            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category Chip
                    Text(
                        text = discovery.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Chip
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (discovery.status == DiscoveryStatus.DRAFT) MaterialTheme.colorScheme.onSurface else Color.Black,
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "REF: ${discovery.id.takeLast(8)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = discovery.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${discovery.systemName} // ${discovery.galaxy}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Secuencia de Glifos
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

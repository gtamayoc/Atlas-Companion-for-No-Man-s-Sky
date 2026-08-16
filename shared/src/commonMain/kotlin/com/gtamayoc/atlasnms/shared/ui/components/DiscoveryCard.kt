package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.ui.theme.RelicGoldHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.WarpFuelOrangeHighlight
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinateUtils

private val CardCornerShape = RoundedCornerShape(12.dp)
private val ChipCornerShape = RoundedCornerShape(4.dp)
private val HudBoxShape = RoundedCornerShape(6.dp)

@Composable
fun DiscoveryCard(
    discovery: Discovery,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: (() -> Unit)? = null
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh

    val (statusText, statusBg) = remember(discovery.status, outlineColor, surfaceContainerHigh) {
        when (discovery.status) {
            DiscoveryStatus.CONFIRMED -> Pair("CONFIRMED", WarpFuelOrangeHighlight)
            DiscoveryStatus.PENDING -> Pair("PENDING", RelicGoldHighlight)
            DiscoveryStatus.INCOMPLETE -> Pair("INCOMPLETE", RelicGoldHighlight)
            DiscoveryStatus.DRAFT -> Pair("DRAFT", surfaceContainerHigh)
            DiscoveryStatus.VALIDATED -> Pair("VALIDATED", RelicGoldHighlight)
        }
    }

    val savedCoord = remember(discovery.glyphs) {
        if (discovery.glyphs.isNotEmpty()) {
            GalacticCoordinateUtils.parseGlyphsToCoordinate(discovery.glyphs)
        } else null
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = CardCornerShape,
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Fondo visual: Imagen real si existe o Mantra procedimental geométrico de alto rendimiento
            if (!discovery.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = discovery.imageUrl,
                    contentDescription = discovery.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                )
            } else if (discovery.glyphs.isNotEmpty()) {
                MantraPatternBackground(
                    glyphs = discovery.glyphs,
                    seed = discovery.id,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
            }

            // Contenido estructurado de la tarjeta
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Fila Superior: Datos de Cabecera y Acción Opcional
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = discovery.type.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, ChipCornerShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )

                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (discovery.status == DiscoveryStatus.DRAFT) MaterialTheme.colorScheme.onSurface else Color.Black,
                                modifier = Modifier
                                    .background(statusBg, ChipCornerShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = discovery.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${discovery.galaxy} | ${discovery.systemName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ELIMINAR",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Panel HUD Táctico de Coordenadas y Distancia
                if (savedCoord != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.40f), HudBoxShape)
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "📍 Coordenada: ${savedCoord.formattedString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Distancia al Centro: ${savedCoord.distanceToCoreLightYears} Años Luz",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // Secuencia de Glifos Compacta (22dp)
                if (discovery.glyphs.isNotEmpty()) {
                    GlyphSequence(
                        glyphs = discovery.glyphs,
                        iconSize = 22,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


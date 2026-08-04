package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

@Composable
fun GlyphSelector(
    selectedGlyphs: List<Int>,
    onGlyphsChanged: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    // Ranura activa en la secuencia de 12 glifos (0..11)
    var activeSlotIndex by remember(selectedGlyphs.size) {
        mutableIntStateOf(if (selectedGlyphs.isEmpty()) 0 else (selectedGlyphs.size - 1).coerceIn(0, 11))
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. VISUALIZADOR DE 12 SLOTS EN 2 FILAS DE 6 GLIFOS (AGRUPADOS EN PARES 2-2-2)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fila 1: Ranuras 0 a 5 (Pares 1-2, 3-4, 5-6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (pairIndex in 0 until 3) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val slot1 = pairIndex * 2
                        val slot2 = pairIndex * 2 + 1

                        GlyphSlotCard(
                            slotIndex = slot1,
                            glyphId = selectedGlyphs.getOrNull(slot1),
                            isActive = activeSlotIndex == slot1,
                            onClick = { activeSlotIndex = slot1 },
                            modifier = Modifier.weight(1f)
                        )
                        GlyphSlotCard(
                            slotIndex = slot2,
                            glyphId = selectedGlyphs.getOrNull(slot2),
                            isActive = activeSlotIndex == slot2,
                            onClick = { activeSlotIndex = slot2 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Fila 2: Ranuras 6 a 11 (Pares 7-8, 9-10, 11-12)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (pairIndex in 3 until 6) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val slot1 = pairIndex * 2
                        val slot2 = pairIndex * 2 + 1

                        GlyphSlotCard(
                            slotIndex = slot1,
                            glyphId = selectedGlyphs.getOrNull(slot1),
                            isActive = activeSlotIndex == slot1,
                            onClick = { activeSlotIndex = slot1 },
                            modifier = Modifier.weight(1f)
                        )
                        GlyphSlotCard(
                            slotIndex = slot2,
                            glyphId = selectedGlyphs.getOrNull(slot2),
                            isActive = activeSlotIndex == slot2,
                            onClick = { activeSlotIndex = slot2 },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. BOTONES DE ACCIÓN (BORRAR Y LIMPIAR - SIEMPRE HORIZONTALES)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SELECCIÓN: ${selectedGlyphs.size}/12 GLIFOS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (selectedGlyphs.isNotEmpty()) {
                            val newList = selectedGlyphs.toMutableList()
                            if (activeSlotIndex < newList.size) {
                                newList.removeAt(activeSlotIndex)
                            } else {
                                newList.removeLast()
                            }
                            onGlyphsChanged(newList)
                            activeSlotIndex = maxOf(0, activeSlotIndex - 1)
                        }
                    },
                    enabled = selectedGlyphs.isNotEmpty(),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⌫ BORRAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Button(
                    onClick = {
                        onGlyphsChanged(emptyList())
                        activeSlotIndex = 0
                    },
                    enabled = selectedGlyphs.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✕ LIMPIAR",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        // 3. TECLADO MATRIZ DE 16 GLIFOS (2 FILAS DE 8)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Fila 1: Glifos 1 a 8
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..8) {
                    GlyphButton(
                        glyphIndex = i,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val currentList = selectedGlyphs.toMutableList()
                            if (activeSlotIndex < currentList.size) {
                                currentList[activeSlotIndex] = i
                            } else if (currentList.size < 12) {
                                currentList.add(i)
                            }
                            onGlyphsChanged(currentList)
                            if (activeSlotIndex < 11) {
                                activeSlotIndex++
                            }
                        }
                    )
                }
            }

            // Fila 2: Glifos 9 a 16
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 9..16) {
                    GlyphButton(
                        glyphIndex = i,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val currentList = selectedGlyphs.toMutableList()
                            if (activeSlotIndex < currentList.size) {
                                currentList[activeSlotIndex] = i
                            } else if (currentList.size < 12) {
                                currentList.add(i)
                            }
                            onGlyphsChanged(currentList)
                            if (activeSlotIndex < 11) {
                                activeSlotIndex++
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlyphSlotCard(
    slotIndex: Int,
    glyphId: Int?,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFilled = glyphId != null

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    isActive -> MaterialTheme.colorScheme.primaryContainer
                    isFilled -> MaterialTheme.colorScheme.surfaceContainerHighest
                    else -> MaterialTheme.colorScheme.surfaceContainer
                }
            )
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = when {
                    isActive -> MaterialTheme.colorScheme.primary
                    isFilled -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (glyphId != null) {
            Image(
                painter = painterResource(getGlyphDrawableResource(glyphId)),
                contentDescription = "Glifo en posición ${slotIndex + 1}",
                modifier = Modifier.padding(2.dp)
            )
        } else {
            Text(
                text = "${slotIndex + 1}",
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun GlyphButton(
    glyphIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier.padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(getGlyphDrawableResource(glyphIndex)),
                contentDescription = "Glifo $glyphIndex",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

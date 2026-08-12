package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.ui.theme.AmberDustHighlight
import com.gtamayoc.atlasnms.shared.ui.theme.WarpFuelOrangeHighlight

@Composable
fun ScanFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // DESIGN.md: Primary (Analyze Capture) - High-contrast Amber-Dust background with black text
    Surface(
        onClick = onClick,
        modifier = modifier
            .border(1.dp, AmberDustHighlight, RoundedCornerShape(4.dp)),
        color = AmberDustHighlight,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📷",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ANALIZAR",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }
    }
}

@Composable
fun DualFabGroup(
    onAnalyzeClick: () -> Unit,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(end = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // DESIGN.md Secondary Button: Transparent background with 1px Warp-Fuel Orange border
        FloatingActionButton(
            onClick = onFilterClick,
            containerColor = if (isFilterActive) WarpFuelOrangeHighlight else MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = if (isFilterActive) Color.Black else WarpFuelOrangeHighlight,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.border(1.dp, WarpFuelOrangeHighlight, RoundedCornerShape(4.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⚡", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFilterActive) "FILTRADO" else "FILTRAR",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Primary Analyze FAB
        ScanFab(onClick = onAnalyzeClick)
    }
}

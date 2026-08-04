package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.ui.components.AtlasWebView
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen

@Composable
fun WikiScreen(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit
) {
    var loadProgress by remember { mutableIntStateOf(0) }
    val defaultWikiUrl = "https://nomanssky.fandom.com/es/wiki/No_Man%27s_Sky_Wiki"

    val animatedProgress by animateFloatAsState(
        targetValue = loadProgress / 100f,
        animationSpec = tween(durationMillis = 500),
        label = "WikiProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header oficial alineado al diseño Grounded Material de la app
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "WIKI & NMS HUB",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (loadProgress >= 100) "Página cargada correctamente" else "Cargando Wiki de No Man's Sky... $loadProgress%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso sutil
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
        }

        // Visor Web WebView optimizado online
        Box(modifier = Modifier.fillMaxSize()) {
            AtlasWebView(
                url = defaultWikiUrl,
                onProgressChanged = { newProgress -> loadProgress = newProgress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}





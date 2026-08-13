package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtamayoc.atlasnms.shared.ui.components.AtlasWebView
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasDimensions

data class WikiTopic(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val url: String,
    val icon: ImageVector,
    val tags: List<String>
)

val SampleWikiTopics = listOf(
    WikiTopic(
        id = "official_wiki",
        title = "Wiki Oficial No Man's Sky",
        category = "Enciclopedia",
        description = "Acceso completo a la base de conocimiento global de NMS: actualizaciones, parches, planetas y tecnologías.",
        url = "https://nomanssky.fandom.com/es/wiki/No_Man%27s_Sky_Wiki",
        icon = Icons.AutoMirrored.Filled.MenuBook,
        tags = listOf("Oficial", "General", "Misiones")
    ),
    WikiTopic(
        id = "portals_glyphs",
        title = "Guía de Portales y Glifos",
        category = "Navegación",
        description = "Red de portales estelares, secuencias de glifos galácticos y métodos para viajar al centro del universo.",
        url = "https://nomanssky.fandom.com/es/wiki/Portal",
        icon = Icons.Default.Place,
        tags = listOf("Portales", "Glifos", "Viajes")
    ),
    WikiTopic(
        id = "ships_weapons",
        title = "Naves y Multinherramientas",
        category = "Equipamiento",
        description = "Catálogo técnico de naves Exóticas, Solares, Centinela Dreadnought y cazas de clase S.",
        url = "https://nomanssky.fandom.com/es/wiki/Nave_espacial",
        icon = Icons.Default.RocketLaunch,
        tags = listOf("Naves", "Cargueros", "Armas")
    ),
    WikiTopic(
        id = "refinery_recipes",
        title = "Refinería y Recetas",
        category = "Recursos",
        description = "Fórmulas de refinación doble/triple, multiplicadores de elementos raros y técnicas de ganancia de Nanites.",
        url = "https://nomanssky.fandom.com/es/wiki/Refiner%C3%ADa",
        icon = Icons.Default.Science,
        tags = listOf("Refinería", "Nanites", "Unidades")
    ),
    WikiTopic(
        id = "lore_languages",
        title = "Lore, Facciones e Idiomas",
        category = "Historia",
        description = "Diccionario y traductor de dialectos Vy'keen, Gek, Korvax e historia antigua del Atlas.",
        url = "https://nomanssky.fandom.com/es/wiki/Atlas",
        icon = Icons.Default.AutoStories,
        tags = listOf("Lore", "Vy'keen", "Gek", "Korvax")
    ),
    WikiTopic(
        id = "bases_industry",
        title = "Bases e Industria Planetaria",
        category = "Arquitectura",
        description = "Guías de construcción de bases, granjas automáticas de Indio Activado y minería industrial.",
        url = "https://nomanssky.fandom.com/es/wiki/Base",
        icon = Icons.Default.HomeWork,
        tags = listOf("Bases", "Industria", "Granjas")
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WikiScreen(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit,
    onDetailActiveChanged: (Boolean) -> Unit = {}
) {
    var selectedWikiTopic by remember { mutableStateOf<WikiTopic?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val isDetailActive = selectedWikiTopic != null
    LaunchedEffect(isDetailActive) {
        onDetailActiveChanged(isDetailActive)
    }

    if (selectedWikiTopic != null) {
        WikiDetailView(
            topic = selectedWikiTopic!!,
            onBack = { selectedWikiTopic = null }
        )
    } else {
        WikiHubView(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            selectedCategoryFilter = selectedCategoryFilter,
            onCategorySelected = { selectedCategoryFilter = it },
            onTopicSelected = { selectedWikiTopic = it }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WikiHubView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategoryFilter: String?,
    onCategorySelected: (String?) -> Unit,
    onTopicSelected: (WikiTopic) -> Unit
) {
    val spacing = AtlasDimensions.spacing
    val categories = remember { listOf("TODAS", "Enciclopedia", "Navegación", "Equipamiento", "Recursos", "Historia", "Arquitectura") }

    val filteredTopics by remember(searchQuery, selectedCategoryFilter) {
        derivedStateOf {
            SampleWikiTopics.filter { topic ->
                val matchesQuery = searchQuery.isBlank() ||
                        topic.title.contains(searchQuery, ignoreCase = true) ||
                        topic.description.contains(searchQuery, ignoreCase = true) ||
                        topic.tags.any { it.contains(searchQuery, ignoreCase = true) }

                val matchesCategory = selectedCategoryFilter == null ||
                        selectedCategoryFilter == "TODAS" ||
                        topic.category.equals(selectedCategoryFilter, ignoreCase = true)

                matchesQuery && matchesCategory
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ENCABEZADO PRINCIPAL DEL HUB DE WIKIS
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.md)
            ) {
                Text(
                    text = "HUB DE CONOCIMIENTO Y WIKIS GALÁCTICAS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Selecciona una categoría o guía especializada para navegar rápidamente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md)
        ) {
            // CAMPO DE BÚSQUEDA EN EL HUB
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Buscar temas, recursos o guías...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AtlasDimensions.corners.small),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )

            // CATEGORÍAS EN CHIPS HORIZONTALES
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                items(categories) { cat ->
                    val isSelected = (selectedCategoryFilter == null && cat == "TODAS") || selectedCategoryFilter == cat
                    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AtlasDimensions.corners.extraSmall))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(AtlasDimensions.corners.extraSmall))
                            .clickable { onCategorySelected(if (cat == "TODAS") null else cat) }
                            .padding(horizontal = spacing.md, vertical = spacing.xs + spacing.xxs)
                    ) {
                        Text(
                            text = cat.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                            color = textColor
                        )
                    }
                }
            }

            // LISTA DE TARJETAS DE WIKIS
            if (filteredTopics.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.xl),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron temas en el Hub con ese término.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    items(
                        items = filteredTopics,
                        key = { it.id }
                    ) { topic ->
                        WikiCard(
                            topic = topic,
                            onClick = { onTopicSelected(topic) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WikiCard(
    topic: WikiTopic,
    onClick: () -> Unit
) {
    val spacing = AtlasDimensions.spacing
    val corners = AtlasDimensions.corners

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(corners.small),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(corners.small))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topic.icon,
                    contentDescription = topic.title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topic.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    topic.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WikiDetailView(
    topic: WikiTopic,
    onBack: () -> Unit
) {
    var loadProgress by remember { mutableIntStateOf(0) }

    val animatedProgress by animateFloatAsState(
        targetValue = loadProgress / 100f,
        animationSpec = tween(durationMillis = 400),
        label = "WikiDetailProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // HEADER EXCLUSIVO DEL DETALLE WIKI (Reemplaza la barra superior principal para maximizar espacio)
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al Hub",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topic.title.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = if (loadProgress >= 100) "Página cargada correctamente" else "Cargando enciclopedia... $loadProgress%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (loadProgress < 100) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
            }
        }

        // Visor Web WebView optimizado ocupando todo el alto disponible
        Box(modifier = Modifier.fillMaxSize()) {
            AtlasWebView(
                url = topic.url,
                onProgressChanged = { newProgress -> loadProgress = newProgress },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

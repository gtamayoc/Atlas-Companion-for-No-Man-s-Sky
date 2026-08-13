package com.gtamayoc.atlasnms.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// AtlasNMS Adaptive & Responsive Layout Utilities
// Breakpoint classification for Mobile, Tablet, and Desktop form factors
// =============================================================================

enum class WindowWidthClass {
    COMPACT,   // Phone / Small Portrait (< 600dp)
    MEDIUM,    // Foldables / Tablets / Small Desktop (600dp - 839dp)
    EXPANDED   // Large Desktop / Wide Screens (>= 840dp)
}

/**
 * Calculates WindowWidthClass based on available screen width in Dp.
 */
fun getWindowWidthClass(widthDp: Dp): WindowWidthClass {
    return when {
        widthDp < 600.dp -> WindowWidthClass.COMPACT
        widthDp < 840.dp -> WindowWidthClass.MEDIUM
        else -> WindowWidthClass.EXPANDED
    }
}

/**
 * Responsive layout properties helper.
 */
data class ResponsiveLayoutConfig(
    val widthClass: WindowWidthClass,
    val contentPadding: Dp,
    val gridColumns: Int,
    val isLandscapeOrWide: Boolean
)

@Composable
fun rememberResponsiveLayoutConfig(screenWidthDp: Dp): ResponsiveLayoutConfig {
    val widthClass = getWindowWidthClass(screenWidthDp)
    val contentPadding = when (widthClass) {
        WindowWidthClass.COMPACT -> 16.dp
        WindowWidthClass.MEDIUM -> 24.dp
        WindowWidthClass.EXPANDED -> 32.dp
    }
    val gridColumns = when (widthClass) {
        WindowWidthClass.COMPACT -> 1
        WindowWidthClass.MEDIUM -> 2
        WindowWidthClass.EXPANDED -> 3
    }
    return ResponsiveLayoutConfig(
        widthClass = widthClass,
        contentPadding = contentPadding,
        gridColumns = gridColumns,
        isLandscapeOrWide = widthClass != WindowWidthClass.COMPACT
    )
}

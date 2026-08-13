package com.gtamayoc.atlasnms.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// AtlasNMS Unified Spacing & Dimension System
// Standardized values for paddings, margins, gaps, radiuses, and component sizes
// =============================================================================

/**
 * Standardized spacing grid tokens for layout consistency across all platforms.
 */
data class SpacingTokens(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp
)

/**
 * Common shape corner radiuses used across Atlas UI components.
 */
data class CornerTokens(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 24.dp,
    val pill: Dp = 999.dp
)

/**
 * Key component sizing metrics.
 */
data class ComponentSizeTokens(
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconExtraLarge: Dp = 48.dp,
    val buttonMinHeight: Dp = 48.dp,
    val topBarHeight: Dp = 64.dp,
    val bottomNavHeight: Dp = 80.dp,
    val cardElevation: Dp = 4.dp,
    val fabSize: Dp = 56.dp
)

val LocalSpacing = staticCompositionLocalOf { SpacingTokens() }
val LocalCorners = staticCompositionLocalOf { CornerTokens() }
val LocalComponentSizes = staticCompositionLocalOf { ComponentSizeTokens() }

/**
 * Theme extensions for quick access to dimensions in Composable code.
 */
object AtlasDimensions {
    val spacing: SpacingTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    val corners: CornerTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalCorners.current

    val sizes: ComponentSizeTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalComponentSizes.current
}

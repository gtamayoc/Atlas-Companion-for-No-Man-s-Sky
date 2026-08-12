package com.gtamayoc.atlasnms.shared.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// AtlasNMS Senior Material Design 3 System - Color Tokens
// Style: Muted Tonal, Utilitarian, Slate & Sage Palette (Legibility Focused)
// =============================================================================

// --- DARK THEME COLOR TOKENS (Grounded Sci-Fi Base #19120C) ---
val DarkPrimary = Color(0xFFFFB876)            // Símbolos de Atlas, acentos dorados
val DarkOnPrimary = Color(0xFF2A1600)
val DarkPrimaryContainer = Color(0xFFE59038)   // Botones de acción principal
val DarkOnPrimaryContainer = Color(0xFFFFDCC0)

val DarkSecondary = Color(0xFFFFB276)
val DarkOnSecondary = Color(0xFF291700)
val DarkSecondaryContainer = Color(0xFFFF7F1C) // Avisos de advertencia, señal de pulso
val DarkOnSecondaryContainer = Color(0xFFFFDBC8)

val DarkTertiary = Color(0xFFE3C4A5)
val DarkOnTertiary = Color(0xFF2F1D0A)
val DarkTertiaryContainer = Color(0xFF48321B)
val DarkOnTertiaryContainer = Color(0xFFFFDCC1)

val DarkSurface = Color(0xFF19120C)            // Fondo principal profundo (Vacío espacial)
val DarkSurfaceDim = Color(0xFF19120C)
val DarkSurfaceBright = Color(0xFF40362F)
val DarkSurfaceContainerLowest = Color(0xFF120B07)
val DarkSurfaceContainerLow = Color(0xFF221A14)  // Tarjetas de descubrimiento
val DarkSurfaceContainer = Color(0xFF261E18)     // Paneles interactivos
val DarkSurfaceContainerHigh = Color(0xFF332922)
val DarkSurfaceContainerHighest = Color(0xFF3F342C)

val DarkOnSurface = Color(0xFFEFE0D5)          // Texto principal de lectura en pantalla
val DarkOnSurfaceVariant = Color(0xFFBBA594)   // Texto secundario, subtítulos
val DarkOutline = Color(0xFFA18D7D)            // Bordes metálicos, separadores
val DarkOutlineVariant = Color(0xFF56473B)

val DarkError = Color(0xFFCF6679)              // Estados de fallo del sistema
val DarkOnError = Color(0xFF370013)
val DarkErrorContainer = Color(0xFF930026)
val DarkOnErrorContainer = Color(0xFFFFD9DF)

// --- LIGHT THEME COLOR TOKENS ---
val LightPrimary = Color(0xFF1A6577)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFBCEBFC)
val LightOnPrimaryContainer = Color(0xFF001F28)

val LightSecondary = Color(0xFF3E6647)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFC0EBC4)
val LightOnSecondaryContainer = Color(0xFF00210C)

val LightTertiary = Color(0xFF5A5A7E)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFE2E0FF)
val LightOnTertiaryContainer = Color(0xFF171737)

val LightSurface = Color(0xFFF8F9FE)
val LightSurfaceDim = Color(0xFFD8DAE0)
val LightSurfaceBright = Color(0xFFF8F9FE)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF2F3F8)
val LightSurfaceContainer = Color(0xFFECEDF2)
val LightSurfaceContainerHigh = Color(0xFFE6E8ED)
val LightSurfaceContainerHighest = Color(0xFFE0E2E7)

val LightOnSurface = Color(0xFF191C1F)
val LightOnSurfaceVariant = Color(0xFF41474D)
val LightOutline = Color(0xFF71787E)
val LightOutlineVariant = Color(0xFFC1C7CE)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

// --- LEGACY BACKWARD COMPATIBILITY TOKENS ---
val PrimaryColor get() = DarkPrimary
val OnPrimary get() = DarkOnPrimary
val PrimaryContainer get() = DarkPrimaryContainer
val OnPrimaryContainer get() = DarkOnPrimaryContainer
val SecondaryColor get() = DarkSecondary
val OnSecondary get() = DarkOnSecondary
val SecondaryContainer get() = DarkSecondaryContainer
val OnSecondaryContainer get() = DarkOnSecondaryContainer
val TertiaryColor get() = DarkTertiary
val OnTertiary get() = DarkOnTertiary
val TertiaryContainer get() = DarkTertiaryContainer
val OnTertiaryContainer get() = DarkOnTertiaryContainer
val SurfaceColor get() = DarkSurface
val SurfaceContainerLowest get() = DarkSurfaceContainerLowest
val SurfaceContainerLow get() = DarkSurfaceContainerLow
val SurfaceContainer get() = DarkSurfaceContainer
val SurfaceContainerHigh get() = DarkSurfaceContainerHigh
val SurfaceContainerHighest get() = DarkSurfaceContainerHighest
val OnSurface get() = DarkOnSurface
val OnSurfaceVariant get() = DarkOnSurfaceVariant
val OutlineColor get() = DarkOutline
val OutlineVariant get() = DarkOutlineVariant
val ErrorColor get() = DarkError
val OnError get() = DarkOnError
val ErrorContainer get() = DarkErrorContainer
val OnErrorContainer get() = DarkOnErrorContainer

val AmberDustHighlight = Color(0xFF8BB5C4)
val WarpFuelOrangeHighlight = Color(0xFFA5CFA9)
val RelicGoldHighlight = Color(0xFFC3C3EA)
val AtlasRedHighlight = Color(0xFFFFB4AB)

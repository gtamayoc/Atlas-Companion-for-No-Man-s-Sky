package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun AtlasBackHandler(enabled: Boolean = true, onBack: () -> Unit)

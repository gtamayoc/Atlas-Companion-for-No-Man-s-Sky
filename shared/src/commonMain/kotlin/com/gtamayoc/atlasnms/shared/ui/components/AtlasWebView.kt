package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AtlasWebView(
    url: String,
    onProgressChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
)



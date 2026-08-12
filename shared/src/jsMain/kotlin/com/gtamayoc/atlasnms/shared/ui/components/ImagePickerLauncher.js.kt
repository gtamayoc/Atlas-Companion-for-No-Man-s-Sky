package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerHandler(
    onImagePicked: (imagePathOrUri: String) -> Unit
): ImagePickerHandler {
    return remember {
        object : ImagePickerHandler {
            override fun launchGallery() {}
            override fun launchCamera() {}
        }
    }
}

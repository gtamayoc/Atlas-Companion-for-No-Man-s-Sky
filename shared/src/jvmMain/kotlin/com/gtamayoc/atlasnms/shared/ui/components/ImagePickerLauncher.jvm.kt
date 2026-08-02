package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberImagePickerHandler(
    onImagePicked: (imagePathOrUri: String) -> Unit
): ImagePickerHandler {
    return remember {
        object : ImagePickerHandler {
            override fun launchGallery() {
                // Desktop fallback sample or file chooser stub
            }

            override fun launchCamera() {
                // Desktop camera fallback stub
            }
        }
    }
}

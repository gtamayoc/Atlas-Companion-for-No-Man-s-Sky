package com.gtamayoc.atlasnms.shared.ui.components

import androidx.compose.runtime.Composable

/**
 * Encapsula la lógica de lanzamiento del selector de imágenes (Galería o Cámara)
 * multiplataforma.
 */
interface ImagePickerHandler {
    fun launchGallery()
    fun launchCamera()
}

@Composable
expect fun rememberImagePickerHandler(
    onImagePicked: (imagePathOrUri: String) -> Unit
): ImagePickerHandler

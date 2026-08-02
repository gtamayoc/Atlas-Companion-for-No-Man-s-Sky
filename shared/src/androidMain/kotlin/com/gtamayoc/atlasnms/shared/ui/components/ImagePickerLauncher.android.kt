package com.gtamayoc.atlasnms.shared.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberImagePickerHandler(
    onImagePicked: (imagePathOrUri: String) -> Unit
): ImagePickerHandler {
    val context = LocalContext.current

    // Launcher de Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImagePicked(it.toString())
        }
    }

    // Launcher de Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            try {
                val tempFile = File(context.cacheBufferDir(), "camera_capture_${System.currentTimeMillis()}.jpg")
                val fos = FileOutputStream(tempFile)
                it.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.flush()
                fos.close()
                onImagePicked(tempFile.toURI().toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return remember(galleryLauncher, cameraLauncher) {
        object : ImagePickerHandler {
            override fun launchGallery() {
                galleryLauncher.launch("image/*")
            }

            override fun launchCamera() {
                cameraLauncher.launch()
            }
        }
    }
}

private fun android.content.Context.cacheBufferDir(): File {
    val cache = File(cacheDir, "atlas_captures")
    if (!cache.exists()) cache.mkdirs()
    return cache
}

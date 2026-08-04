package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.domain.service.SettingsManager
import com.gtamayoc.atlasnms.shared.ui.components.AtlasTopNav
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme

@Composable
fun SettingsScreen(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(SettingsManager.deepSeekApiKey) }
    var modelInput by remember { mutableStateOf(SettingsManager.deepSeekModel) }
    var baseUrlInput by remember { mutableStateOf(SettingsManager.deepSeekBaseUrl) }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var pendingScreenSelection by remember { mutableStateOf<AppScreen?>(null) }

    val hasUnsavedChanges = apiKeyInput != SettingsManager.deepSeekApiKey ||
            modelInput != SettingsManager.deepSeekModel ||
            baseUrlInput != SettingsManager.deepSeekBaseUrl

    val safeOnScreenSelected: (AppScreen) -> Unit = { targetScreen ->
        if (hasUnsavedChanges) {
            pendingScreenSelection = targetScreen
            showUnsavedChangesDialog = true
        } else {
            onScreenSelected(targetScreen)
        }
    }

    AtlasNMSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
                // Título de Configuración
                Text(
                    text = "AJUSTES DE SISTEMA",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // 1. Sección de Motor de IA y OCR (DEEPSEEK API)
                SettingsSectionCard(title = "CONFIGURACIÓN DE INTELIGENCIA ARTIFICIAL (DEEPSEEK API)") {
                    Text(
                        text = "Ingresa tu API Key de DeepSeek para procesar capturas reales de pantalla y extraer automáticamente metadatos NMS. Los datos se guardan al pulsar 'Guardar Configuración'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("DeepSeek API Key (sk-...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = modelInput,
                            onValueChange = { modelInput = it },
                            label = { Text("Modelo AI") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp)
                        )

                        OutlinedTextField(
                            value = baseUrlInput,
                            onValueChange = { baseUrlInput = it },
                            label = { Text("Base URL") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón para Guardar Configuración
                    Button(
                        onClick = {
                            SettingsManager.saveAllSettings(apiKeyInput, modelInput, baseUrlInput)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasUnsavedChanges,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (hasUnsavedChanges) "GUARDAR CONFIGURACIÓN EN SQLITE" else "✓ CONFIGURACIÓN GUARDADA",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (SettingsManager.isApiKeyConfigured()) 
                            "✓ DeepSeek API Key activa y persistida en SQLite" 
                        else 
                            "⚠️ API Key no guardada aún. Se utilizará simulación local.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (SettingsManager.isApiKeyConfigured()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.secondary
                    )
                }

                // 2. Sección de Base de Datos y Almacenamiento
                SettingsSectionCard(title = "ALMACENAMIENTO Y BASE DE DATOS") {
                    SettingsRow(
                        title = "Base de datos local",
                        subtitle = "SQLite / SqlDelight (Persistencia local cifrada)"
                    )
                }

                // 3. Sección Legal y Requisitos de Google Play
                SettingsSectionCard(title = "INFORMACIÓN LEGAL Y PRIVACIDAD (GOOGLE PLAY)") {
                    Text(
                        text = "Consulta los términos legales y políticas de privacidad para el uso de la aplicación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showPrivacyPolicyDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("POLÍTICA PRIVACIDAD", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = { showTermsDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("TÉRMINOS Y CONDICIONES", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // 4. Sección de Información de la App
                SettingsSectionCard(title = "ACERCA DE ATLAS NMS") {
                    SettingsRow(
                        title = "Versión de la Aplicación",
                        subtitle = "v1.0.0-beta (Build 2026.08)"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Atlas NMS es una herramienta desarrollada para exploradores de No Man's Sky. No Man's Sky es marca registrada de Hello Games Ltd.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

        // Diálogo de Cambios No Guardados
        if (showUnsavedChangesDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedChangesDialog = false },
                title = {
                    Text(
                        text = "CAMBIOS NO GUARDADOS",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Tienes cambios pendientes en la configuración de la API Key. ¿Deseas guardarlos antes de salir?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            SettingsManager.saveAllSettings(apiKeyInput, modelInput, baseUrlInput)
                            showUnsavedChangesDialog = false
                            pendingScreenSelection?.let { onScreenSelected(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("GUARDAR Y SALIR")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showUnsavedChangesDialog = false
                            apiKeyInput = SettingsManager.deepSeekApiKey
                            modelInput = SettingsManager.deepSeekModel
                            baseUrlInput = SettingsManager.deepSeekBaseUrl
                            pendingScreenSelection?.let { onScreenSelected(it) }
                        }
                    ) {
                        Text("DESCARTAR CAMBIOS")
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        }

        // Diálogo modal de Política de Privacidad
        if (showPrivacyPolicyDialog) {
            LegalDialog(
                title = "POLÍTICA DE PRIVACIDAD",
                content = LegalContent.PRIVACY_POLICY,
                onDismiss = { showPrivacyPolicyDialog = false }
            )
        }

        // Diálogo modal de Términos y Condiciones
        if (showTermsDialog) {
            LegalDialog(
                title = "TÉRMINOS Y CONDICIONES",
                content = LegalContent.TERMS_AND_CONDITIONS,
                onDismiss = { showTermsDialog = false }
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LegalDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .height(350.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("ENTENDIDO", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

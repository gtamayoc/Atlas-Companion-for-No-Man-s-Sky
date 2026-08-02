package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtamayoc.atlasnms.shared.ui.components.AtlasBottomNav
import com.gtamayoc.atlasnms.shared.ui.components.ScanFab
import com.gtamayoc.atlasnms.shared.ui.navigation.AppScreen
import com.gtamayoc.atlasnms.shared.ui.theme.AtlasNMSTheme

@Composable
fun SettingsScreen(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    onFabClick: () -> Unit
) {
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    AtlasNMSTheme {
        Scaffold(
            bottomBar = {
                AtlasBottomNav(
                    currentScreen = currentScreen,
                    onScreenSelected = onScreenSelected
                )
            },
            floatingActionButton = { ScanFab(onClick = onFabClick) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Título de Configuración
                Text(
                    text = "AJUSTES DE SISTEMA",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // 1. Sección de Motor de IA y OCR
                SettingsSectionCard(title = "MOTOR DE EXTRACCIÓN Y OCR") {
                    SettingsRow(
                        title = "Proveedor de IA",
                        subtitle = "DeepSeek AI (Intérprete Estructurado JSON)"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsRow(
                        title = "Procesamiento Local",
                        subtitle = "Activado para preprocesamiento de imágenes"
                    )
                }

                // 2. Sección de Base de Datos y Almacenamiento
                SettingsSectionCard(title = "ALMACENAMIENTO Y BASE DE DATOS") {
                    SettingsRow(
                        title = "Base de datos local",
                        subtitle = "SQLite / SqlDelight (Cifrado local)"
                    )
                }

                // 3. Sección Legal y Requisitos de Google Play
                SettingsSectionCard(title = "INFORMACIÓN LEGAL Y PRIVACIDAD (GOOGLE PLAY)") {
                    Text(
                        text = "Para cumplir con las políticas de publicación en Google Play Store, consulta la documentación legal sobre el uso de datos y la exención de responsabilidad de No Man's Sky.",
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
                        text = "Atlas NMS es una herramienta no oficial desarrollada para exploradores de No Man's Sky. No Man's Sky es marca registrada de Hello Games Ltd.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
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

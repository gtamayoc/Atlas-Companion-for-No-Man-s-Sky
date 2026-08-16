package com.gtamayoc.atlasnms.shared.domain.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StageLogEntry(
    val timestampMs: Long,
    val stage: PipelineStageSource,
    val level: String, // "INFO", "WARN", "DEBUG", "ERROR"
    val summary: String,
    val details: String
)

data class PipelineTelemetrySnapshot(
    val selectedImageUri: String?,
    val nativeTimeMs: Long,
    val ocrTimeMs: Long,
    val aiTimeMs: Long,
    val ocrRawText: String,
    val userEditedTextDiff: String,
    val promptSentToAi: String,
    val rawAiJsonResponse: String,
    val confidenceScore: Double,
    val discrepanciesCount: Int,
    val stageLogs: List<StageLogEntry>
)

object ScanPipelineDebugger {

    private val _logs = MutableStateFlow<List<StageLogEntry>>(emptyList())
    val logs: StateFlow<List<StageLogEntry>> = _logs.asStateFlow()

    private val _telemetry = MutableStateFlow(
        PipelineTelemetrySnapshot(
            selectedImageUri = null,
            nativeTimeMs = 0,
            ocrTimeMs = 0,
            aiTimeMs = 0,
            ocrRawText = "",
            userEditedTextDiff = "Sin cambios",
            promptSentToAi = "",
            rawAiJsonResponse = "",
            confidenceScore = 0.0,
            discrepanciesCount = 0,
            stageLogs = emptyList()
        )
    )
    val telemetry: StateFlow<PipelineTelemetrySnapshot> = _telemetry.asStateFlow()

    fun reset() {
        _logs.value = emptyList()
        _telemetry.value = PipelineTelemetrySnapshot(
            selectedImageUri = null,
            nativeTimeMs = 0,
            ocrTimeMs = 0,
            aiTimeMs = 0,
            ocrRawText = "",
            userEditedTextDiff = "Sin cambios",
            promptSentToAi = "",
            rawAiJsonResponse = "",
            confidenceScore = 0.0,
            discrepanciesCount = 0,
            stageLogs = emptyList()
        )
    }

    fun log(stage: PipelineStageSource, level: String, summary: String, details: String = "") {
        val newEntry = StageLogEntry(
            timestampMs = com.gtamayoc.atlasnms.shared.util.currentTimeMillis(),
            stage = stage,
            level = level,
            summary = summary,
            details = details
        )
        val updatedList = (_logs.value + newEntry).takeLast(100)
        _logs.value = updatedList
    }

    fun updateTelemetry(transform: (PipelineTelemetrySnapshot) -> PipelineTelemetrySnapshot) {
        val updated = transform(_telemetry.value)
        _telemetry.value = updated.copy(stageLogs = _logs.value)
    }

    fun exportLogsAsMarkdown(): String {
        val current = _telemetry.value
        val sb = StringBuilder()
        sb.appendLine("# Reporte de Depuración de Tubería de Análisis Atlas NMS")
        sb.appendLine("Fecha: ${current.selectedImageUri ?: "N/A"}")
        sb.appendLine("---")
        sb.appendLine("## ⏱️ Métricas de Rendimiento")
        sb.appendLine("- Tiempo C Nativo: ${current.nativeTimeMs} ms")
        sb.appendLine("- Tiempo Extracción OCR: ${current.ocrTimeMs} ms")
        sb.appendLine("- Tiempo Inferencia IA: ${current.aiTimeMs} ms")
        sb.appendLine("- Confianza Estimada: ${(current.confidenceScore * 100).toInt()}%")
        sb.appendLine("- Discrepancias Detectadas: ${current.discrepanciesCount}")
        sb.appendLine()
        sb.appendLine("## 🔍 Texto OCR Bruto Extraído")
        sb.appendLine("```text")
        sb.appendLine(current.ocrRawText)
        sb.appendLine("```")
        sb.appendLine()
        sb.appendLine("## ✏️ Modificaciones del Usuario")
        sb.appendLine(current.userEditedTextDiff)
        sb.appendLine()
        sb.appendLine("## 🤖 Prompt Enviado al Modelo")
        sb.appendLine("```markdown")
        sb.appendLine(current.promptSentToAi)
        sb.appendLine("```")
        sb.appendLine()
        sb.appendLine("## 📋 Registro Cronológico de Eventos")
        current.stageLogs.forEach { entry ->
            sb.appendLine("[${entry.stage}] [${entry.level}] ${entry.summary} - ${entry.details}")
        }
        return sb.toString()
    }
}

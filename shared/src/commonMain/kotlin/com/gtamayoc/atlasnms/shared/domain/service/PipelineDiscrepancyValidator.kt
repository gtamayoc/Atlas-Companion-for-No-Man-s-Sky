package com.gtamayoc.atlasnms.shared.domain.service

import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.util.currentTimeMillis

enum class DiscrepancySeverity {
    INFO,
    WARNING,
    HIGH
}

enum class PipelineStageSource {
    OCR_EXTRACTION,
    USER_EDIT,
    AI_PROCESSING,
    DATA_MAPPING
}

data class ValidationDiscrepancy(
    val id: String,
    val stage: PipelineStageSource,
    val severity: DiscrepancySeverity,
    val title: String,
    val description: String,
    val fieldName: String,
    val ocrValue: String,
    val userValue: String,
    val aiValue: String
)

object PipelineDiscrepancyValidator {

    /**
     * Evalúa la coherencia entre las tres capas:
     * 1. Lectura del OCR / Motor Híbrido
     * 2. Correcciones ingresadas/confirmadas por el Usuario (con visualización de imagen)
     * 3. Salida procesada y sincronizable de la Inteligencia Artificial
     */
    fun validatePipeline(
        ocrResult: OcrResult?,
        userVerifiedName: String,
        userVerifiedSystem: String,
        userVerifiedGalaxy: String,
        userVerifiedType: DiscoveryType,
        userVerifiedGlyphs: List<Int>,
        aiResult: AiAnalysisResult?
    ): List<ValidationDiscrepancy> {
        val discrepancies = mutableListOf<ValidationDiscrepancy>()

        if (ocrResult != null) {
            if (ocrResult.candidateName != userVerifiedName && userVerifiedName.isNotBlank()) {
                discrepancies.add(
                    ValidationDiscrepancy(
                        id = "disc_ocr_name_${currentTimeMillis()}",
                        stage = PipelineStageSource.USER_EDIT,
                        severity = DiscrepancySeverity.INFO,
                        title = "Corrección Manual de Nombre",
                        description = "El usuario ajustó el nombre detectado de '${ocrResult.candidateName}' a '$userVerifiedName'.",
                        fieldName = "Nombre",
                        ocrValue = ocrResult.candidateName,
                        userValue = userVerifiedName,
                        aiValue = aiResult?.suggestedName ?: ""
                    )
                )
            }
        }

        if (aiResult != null) {
            if (userVerifiedName.trim().lowercase() != aiResult.suggestedName.trim().lowercase() && userVerifiedName.isNotBlank()) {
                discrepancies.add(
                    ValidationDiscrepancy(
                        id = "disc_ai_name_${currentTimeMillis()}",
                        stage = PipelineStageSource.AI_PROCESSING,
                        severity = DiscrepancySeverity.WARNING,
                        title = "Ajuste de Nombre por la IA",
                        description = "La IA devolvió '$aiResult.suggestedName', mientras que el usuario confirmó '$userVerifiedName'.",
                        fieldName = "Nombre",
                        ocrValue = ocrResult?.candidateName ?: "",
                        userValue = userVerifiedName,
                        aiValue = aiResult.suggestedName
                    )
                )
            }

            // Solo validar glifos si la secuencia no es vacía (algunos descubrimientos como naves o fauna no contienen glifos)
            if (userVerifiedGlyphs.isNotEmpty() && userVerifiedGlyphs.size != 12) {
                discrepancies.add(
                    ValidationDiscrepancy(
                        id = "disc_glyph_len_${currentTimeMillis()}",
                        stage = PipelineStageSource.DATA_MAPPING,
                        severity = DiscrepancySeverity.HIGH,
                        title = "Secuencia de Glifos Incompleta",
                        description = "Las coordenadas de portal requieren exactamente 12 glifos. Detectados: ${userVerifiedGlyphs.size}.",
                        fieldName = "Glifos",
                        ocrValue = ocrResult?.candidateGlyphs?.joinToString() ?: "",
                        userValue = userVerifiedGlyphs.joinToString(),
                        aiValue = aiResult.glyphs.joinToString()
                    )
                )
            }
        }

        return discrepancies
    }
}

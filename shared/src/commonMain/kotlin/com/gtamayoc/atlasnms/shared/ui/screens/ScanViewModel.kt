package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalysisResult
import com.gtamayoc.atlasnms.shared.domain.service.AiAnalyzerService
import com.gtamayoc.atlasnms.shared.domain.service.ExtractionResult
import com.gtamayoc.atlasnms.shared.domain.service.PipelineDiscrepancyValidator
import com.gtamayoc.atlasnms.shared.domain.service.PipelineStageSource
import com.gtamayoc.atlasnms.shared.domain.service.ScanPipelineDebugger
import com.gtamayoc.atlasnms.shared.domain.service.ValidationDiscrepancy
import com.gtamayoc.atlasnms.shared.domain.service.VisionExtractionEngine
import com.gtamayoc.atlasnms.shared.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(
    private val repository: DiscoveryRepository
) : ViewModel() {

    private val _currentStage = MutableStateFlow(AnalysisStage.STAGE_1_CAPTURE)
    val currentStage: StateFlow<AnalysisStage> = _currentStage.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _isOcrRunning = MutableStateFlow(false)
    val isOcrRunning: StateFlow<Boolean> = _isOcrRunning.asStateFlow()

    private val _isAiRunning = MutableStateFlow(false)
    val isAiRunning: StateFlow<Boolean> = _isAiRunning.asStateFlow()

    private val _enhanceContrast = MutableStateFlow(true)
    val enhanceContrast: StateFlow<Boolean> = _enhanceContrast.asStateFlow()

    private val _binarizeForOcr = MutableStateFlow(true)
    val binarizeForOcr: StateFlow<Boolean> = _binarizeForOcr.asStateFlow()

    private val _extractionResult = MutableStateFlow<ExtractionResult?>(null)
    val extractionResult: StateFlow<ExtractionResult?> = _extractionResult.asStateFlow()

    private val _aiResult = MutableStateFlow<AiAnalysisResult?>(null)
    val aiResult: StateFlow<AiAnalysisResult?> = _aiResult.asStateFlow()

    private val _validationDiscrepancies = MutableStateFlow<List<ValidationDiscrepancy>>(emptyList())
    val validationDiscrepancies: StateFlow<List<ValidationDiscrepancy>> = _validationDiscrepancies.asStateFlow()

    private val _userVerifiedName = MutableStateFlow("")
    val userVerifiedName: StateFlow<String> = _userVerifiedName.asStateFlow()

    private val _userVerifiedSystem = MutableStateFlow("")
    val userVerifiedSystem: StateFlow<String> = _userVerifiedSystem.asStateFlow()

    private val _userVerifiedGalaxy = MutableStateFlow("Euclid")
    val userVerifiedGalaxy: StateFlow<String> = _userVerifiedGalaxy.asStateFlow()

    private val _userVerifiedType = MutableStateFlow(DiscoveryType.PLANET)
    val userVerifiedType: StateFlow<DiscoveryType> = _userVerifiedType.asStateFlow()

    private val _includeGlyphsInCapture = MutableStateFlow(true)
    val includeGlyphsInCapture: StateFlow<Boolean> = _includeGlyphsInCapture.asStateFlow()

    private val _userVerifiedGlyphs = MutableStateFlow(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    val userVerifiedGlyphs: StateFlow<List<Int>> = _userVerifiedGlyphs.asStateFlow()

    private val _activeGlyphSlotIndex = MutableStateFlow(0)
    val activeGlyphSlotIndex: StateFlow<Int> = _activeGlyphSlotIndex.asStateFlow()

    fun onImageSelected(uriPath: String) {
        _selectedImageUri.value = uriPath
        _currentStage.value = AnalysisStage.STAGE_1_CAPTURE
        _extractionResult.value = null
        _aiResult.value = null
        _validationDiscrepancies.value = emptyList()
        _activeGlyphSlotIndex.value = 0
        ScanPipelineDebugger.reset()
        ScanPipelineDebugger.log(
            stage = PipelineStageSource.OCR_EXTRACTION,
            level = "INFO",
            summary = "Nueva captura seleccionada",
            details = "URI: $uriPath"
        )
        ScanPipelineDebugger.updateTelemetry { it.copy(selectedImageUri = uriPath) }
    }

    fun setEnhanceContrast(value: Boolean) { _enhanceContrast.value = value }
    fun setBinarizeForOcr(value: Boolean) { _binarizeForOcr.value = value }
    fun setUserVerifiedName(name: String) { _userVerifiedName.value = name }
    fun setUserVerifiedSystem(system: String) { _userVerifiedSystem.value = system }
    fun setUserVerifiedGalaxy(galaxy: String) { _userVerifiedGalaxy.value = galaxy }
    fun setUserVerifiedType(type: DiscoveryType) { _userVerifiedType.value = type }
    fun setIncludeGlyphsInCapture(include: Boolean) { _includeGlyphsInCapture.value = include }
    fun setUserVerifiedGlyphs(glyphs: List<Int>) { _userVerifiedGlyphs.value = glyphs }
    fun setActiveGlyphSlotIndex(index: Int) { _activeGlyphSlotIndex.value = index }
    fun setCurrentStage(stage: AnalysisStage) { _currentStage.value = stage }

    fun updateGlyphAtActiveSlot(clickedGlyph: Int) {
        val currentList = _userVerifiedGlyphs.value.toMutableList()
        while (currentList.size < 12) currentList.add(1)
        val slotToReplace = _activeGlyphSlotIndex.value.coerceIn(0, 11)
        currentList[slotToReplace] = clickedGlyph
        _userVerifiedGlyphs.value = currentList
        _activeGlyphSlotIndex.value = (slotToReplace + 1) % 12
    }

    fun deleteActiveOrLastGlyph() {
        val currentList = _userVerifiedGlyphs.value.toMutableList()
        if (currentList.isNotEmpty()) {
            val slotToClear = _activeGlyphSlotIndex.value.coerceIn(0, currentList.size - 1)
            currentList.removeAt(slotToClear)
            _userVerifiedGlyphs.value = currentList
            _activeGlyphSlotIndex.value = maxOf(0, slotToClear - 1)
        }
    }

    fun clearAllGlyphs() {
        _userVerifiedGlyphs.value = emptyList()
        _activeGlyphSlotIndex.value = 0
    }

    fun runOcrProcessing() {
        val uri = _selectedImageUri.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            _isOcrRunning.value = true
            val extRes = VisionExtractionEngine.extractHybridData(
                imageUriOrPath = uri,
                rawPixels = DUMMY_PIXELS,
                width = 600,
                height = 400,
                enhanceContrast = _enhanceContrast.value,
                binarizeForOcr = _binarizeForOcr.value
            )

            _extractionResult.value = extRes
            _userVerifiedName.value = extRes.candidateName
            _userVerifiedSystem.value = extRes.candidateSystem
            _userVerifiedGalaxy.value = extRes.candidateGalaxy
            _userVerifiedType.value = extRes.candidateType
            _includeGlyphsInCapture.value = extRes.hasGlyphs
            _userVerifiedGlyphs.value = if (extRes.hasGlyphs) extRes.matchedGlyphsIndices else emptyList()
            _activeGlyphSlotIndex.value = 0

            _isOcrRunning.value = false
            _currentStage.value = AnalysisStage.STAGE_3_USER_REVIEW
        }
    }

    fun runAiAnalysis() {
        val uri = _selectedImageUri.value ?: ""
        viewModelScope.launch(Dispatchers.Default) {
            _isAiRunning.value = true
            val ext = _extractionResult.value
            val res = AiAnalyzerService.analyzeScreenshotWithVerifiedText(
                imageUriOrPath = uri,
                verifiedName = _userVerifiedName.value,
                verifiedSystem = _userVerifiedSystem.value,
                verifiedGalaxy = _userVerifiedGalaxy.value,
                verifiedType = _userVerifiedType.value,
                verifiedGlyphs = if (_includeGlyphsInCapture.value) _userVerifiedGlyphs.value else emptyList(),
                nativeResult = ext?.nativeCMetrics
            )
            _aiResult.value = res

            val discrepancies = PipelineDiscrepancyValidator.validatePipeline(
                ocrResult = null,
                userVerifiedName = _userVerifiedName.value,
                userVerifiedSystem = _userVerifiedSystem.value,
                userVerifiedGalaxy = _userVerifiedGalaxy.value,
                userVerifiedType = _userVerifiedType.value,
                userVerifiedGlyphs = if (_includeGlyphsInCapture.value) _userVerifiedGlyphs.value else emptyList(),
                aiResult = res
            )
            _validationDiscrepancies.value = discrepancies

            _isAiRunning.value = false
            _currentStage.value = AnalysisStage.STAGE_4_AI_DONE
        }
    }

    fun saveFinalDiscovery() {
        val discovery = Discovery(
            id = currentTimeMillis().toString(),
            type = _userVerifiedType.value,
            name = _userVerifiedName.value.ifBlank { "Descubrimiento de Sector" },
            galaxy = _userVerifiedGalaxy.value,
            systemName = _userVerifiedSystem.value.ifBlank { "Sistema Desconocido" },
            glyphs = if (_includeGlyphsInCapture.value) _userVerifiedGlyphs.value else emptyList(),
            imageUrl = _selectedImageUri.value,
            timestamp = currentTimeMillis(),
            status = DiscoveryStatus.CONFIRMED,
            confidence = _aiResult.value?.confidence ?: 0.95
        )

        viewModelScope.launch(Dispatchers.Default) {
            repository.saveDiscovery(discovery)
            _currentStage.value = AnalysisStage.STAGE_5_COMPLETED
        }
    }

    companion object {
        private val DUMMY_PIXELS by lazy {
            IntArray(600 * 400) { (0xFF shl 24) or ((it % 255) shl 16) or ((it % 255) shl 8) or (it % 255) }
        }
    }
}

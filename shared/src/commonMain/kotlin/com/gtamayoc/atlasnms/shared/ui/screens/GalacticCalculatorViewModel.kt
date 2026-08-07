package com.gtamayoc.atlasnms.shared.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinate
import com.gtamayoc.atlasnms.shared.util.GalacticCoordinateUtils
import com.gtamayoc.atlasnms.shared.util.NmsGalaxies
import com.gtamayoc.atlasnms.shared.util.NmsGalaxy
import com.gtamayoc.atlasnms.shared.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalacticCalculatorViewModel(
    private val repository: DiscoveryRepository
) : ViewModel() {

    // --- PESTAÑAS ---
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    // --- ESTADO 1: REGISTRO DE PORTAL ---
    private val _portalName = MutableStateFlow("")
    val portalName: StateFlow<String> = _portalName.asStateFlow()

    private val _selectedGalaxy = MutableStateFlow(NmsGalaxies.getByNumber(1))
    val selectedGalaxy: StateFlow<NmsGalaxy> = _selectedGalaxy.asStateFlow()

    private val _portalGlyphs = MutableStateFlow(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    val portalGlyphs: StateFlow<List<Int>> = _portalGlyphs.asStateFlow()

    val portalCoordinate: StateFlow<GalacticCoordinate> = _portalGlyphs.map { glyphs ->
        GalacticCoordinateUtils.parseGlyphsToCoordinate(glyphs)
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GalacticCoordinateUtils.parseGlyphsToCoordinate(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    )

    val allDiscoveries: StateFlow<List<Discovery>> = repository.getAllDiscoveries()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val relatedPortals: StateFlow<List<Discovery>> = combine(_portalGlyphs, allDiscoveries) { glyphs, discoveries ->
        if (glyphs.size < 12) emptyList()
        else {
            val systemAddressKey = glyphs.drop(3).take(9)
            discoveries.filter { discovery ->
                discovery.type == DiscoveryType.PORTAL &&
                discovery.glyphs.size >= 12 &&
                discovery.glyphs.drop(3).take(9) == systemAddressKey
            }
        }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- ESTADO 2: CALCULADORA MANUAL ---
    private val _calcGlyphs = MutableStateFlow(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    val calcGlyphs: StateFlow<List<Int>> = _calcGlyphs.asStateFlow()

    val calcCoordinate: StateFlow<GalacticCoordinate> = _calcGlyphs.map { glyphs ->
        GalacticCoordinateUtils.parseGlyphsToCoordinate(glyphs)
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GalacticCoordinateUtils.parseGlyphsToCoordinate(listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    )

    // --- ESTADO 3: RADAR DE TELEPORTS ALEATORIOS ---
    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _displayedGlyphs = MutableStateFlow((1..12).map { (1..16).random() })
    val displayedGlyphs: StateFlow<List<Int>> = _displayedGlyphs.asStateFlow()

    private val _randomTeleport = MutableStateFlow<GalacticCoordinate?>(null)
    val randomTeleport: StateFlow<GalacticCoordinate?> = _randomTeleport.asStateFlow()

    private val _teleportName = MutableStateFlow("")
    val teleportName: StateFlow<String> = _teleportName.asStateFlow()

    private val _teleportGalaxy = MutableStateFlow(NmsGalaxies.getByNumber(1))
    val teleportGalaxy: StateFlow<NmsGalaxy> = _teleportGalaxy.asStateFlow()

    val savedTeleports: StateFlow<List<Discovery>> = allDiscoveries.map { list ->
        list.filter { it.type == DiscoveryType.PORTAL }
    }.flowOn(Dispatchers.Default).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }

    fun setPortalName(name: String) {
        _portalName.value = name
    }

    fun setSelectedGalaxy(galaxy: NmsGalaxy) {
        _selectedGalaxy.value = galaxy
    }

    fun setPortalGlyphs(glyphs: List<Int>) {
        _portalGlyphs.value = glyphs
    }

    fun setCalcGlyphs(glyphs: List<Int>) {
        _calcGlyphs.value = glyphs
    }

    fun setTeleportName(name: String) {
        _teleportName.value = name
    }

    fun setTeleportGalaxy(galaxy: NmsGalaxy) {
        _teleportGalaxy.value = galaxy
    }

    fun spinRadar() {
        if (_isSpinning.value) return
        viewModelScope.launch {
            _isSpinning.value = true
            repeat(15) {
                _displayedGlyphs.value = (1..12).map { (1..16).random() }
                delay(60)
            }
            val generated = GalacticCoordinateUtils.generateRandomTeleport()
            _randomTeleport.value = generated
            _displayedGlyphs.value = generated.glyphIndices
            _isSpinning.value = false
        }
    }

    fun savePortalDiscovery(onSuccess: () -> Unit) {
        val name = _portalName.value.ifBlank { "Portal Estelar // ${_selectedGalaxy.value.name}" }
        val glyphs = _portalGlyphs.value
        val galaxy = _selectedGalaxy.value.name
        val coord = GalacticCoordinateUtils.parseGlyphsToCoordinate(glyphs)

        val newDiscovery = Discovery(
            id = currentTimeMillis().toString(),
            type = DiscoveryType.PORTAL,
            name = name,
            galaxy = galaxy,
            systemName = "Sistema ${coord.formattedString.takeLast(4)}",
            glyphs = glyphs,
            imageUrl = null,
            timestamp = currentTimeMillis(),
            status = DiscoveryStatus.CONFIRMED,
            confidence = 1.0
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDiscovery(newDiscovery)
            _portalName.value = ""
            onSuccess()
        }
    }

    fun saveTeleportDiscovery(onSuccess: () -> Unit) {
        val teleport = _randomTeleport.value ?: return
        val name = _teleportName.value.ifBlank { "Teleport Estelar ${teleport.formattedString.takeLast(4)}" }
        val galaxy = _teleportGalaxy.value.name

        val newDiscovery = Discovery(
            id = currentTimeMillis().toString(),
            type = DiscoveryType.PORTAL,
            name = name,
            galaxy = galaxy,
            systemName = "Sistema ${teleport.formattedString}",
            glyphs = teleport.glyphIndices,
            imageUrl = null,
            timestamp = currentTimeMillis(),
            status = DiscoveryStatus.CONFIRMED,
            confidence = 1.0
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveDiscovery(newDiscovery)
            _teleportName.value = ""
            _randomTeleport.value = null
            onSuccess()
        }
    }

    fun deleteDiscovery(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDiscovery(id)
        }
    }
}

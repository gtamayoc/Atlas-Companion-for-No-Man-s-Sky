package com.gtamayoc.atlasnms.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DiscoveryType {
    SHIP, PLANET, FAUNA, MULTITOOL, BASE, OTHER
}

@Serializable
enum class DiscoveryStatus {
    CONFIRMED, PENDING, DRAFT, INCOMPLETE
}

@Serializable
data class Discovery(
    val id: String,
    val type: DiscoveryType,
    val name: String,
    val galaxy: String,
    val systemName: String,
    val glyphs: List<Int>, // 12 glyphs representing the portal address
    val imageUrl: String?,
    val timestamp: Long,
    val status: DiscoveryStatus,
    val confidence: Double = 1.0
)

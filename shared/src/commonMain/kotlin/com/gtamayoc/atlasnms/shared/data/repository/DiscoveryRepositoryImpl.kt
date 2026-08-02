package com.gtamayoc.atlasnms.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.gtamayoc.atlasnms.shared.cache.AtlasDatabase
import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryStatus
import com.gtamayoc.atlasnms.shared.domain.model.DiscoveryType
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiscoveryRepositoryImpl(
    private val database: AtlasDatabase
) : DiscoveryRepository {

    private val queries = database.atlasDatabaseQueries

    init {
        com.gtamayoc.atlasnms.shared.domain.service.SettingsManager.initialize(database)
    }

    override fun getAllDiscoveries(): Flow<List<Discovery>> {
        return queries.selectAllDiscoveries()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Discovery(
                        id = entity.id,
                        type = DiscoveryType.valueOf(entity.type),
                        name = entity.name,
                        galaxy = entity.galaxy,
                        systemName = entity.systemName,
                        glyphs = entity.glyphs.split(",").mapNotNull { it.toIntOrNull() },
                        imageUrl = entity.imageUrl,
                        timestamp = entity.timestamp,
                        status = DiscoveryStatus.valueOf(entity.status),
                        confidence = entity.confidence
                    )
                }
            }
    }

    override suspend fun getDiscoveryById(id: String): Discovery? {
        // Not implemented in queries yet, but standard implementation goes here
        return null 
    }

    override suspend fun saveDiscovery(discovery: Discovery) {
        queries.insertDiscovery(
            id = discovery.id,
            type = discovery.type.name,
            name = discovery.name,
            galaxy = discovery.galaxy,
            systemName = discovery.systemName,
            glyphs = discovery.glyphs.joinToString(","),
            imageUrl = discovery.imageUrl,
            timestamp = discovery.timestamp,
            status = discovery.status.name,
            confidence = discovery.confidence
        )
    }

    override suspend fun deleteDiscovery(id: String) {
        queries.deleteDiscovery(id)
    }

    override suspend fun syncWithMockData() {
        // La aplicación inicia limpia sin datos de prueba sintéticos
    }
}

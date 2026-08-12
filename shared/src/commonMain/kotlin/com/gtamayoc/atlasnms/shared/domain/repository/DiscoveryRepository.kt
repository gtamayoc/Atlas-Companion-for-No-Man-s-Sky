package com.gtamayoc.atlasnms.shared.domain.repository

import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import kotlinx.coroutines.flow.Flow

interface DiscoveryRepository {
    fun getAllDiscoveries(): Flow<List<Discovery>>
    suspend fun getDiscoveryById(id: String): Discovery?
    suspend fun saveDiscovery(discovery: Discovery)
    suspend fun deleteDiscovery(id: String)
    suspend fun syncWithMockData() // Useful to populate the DB initially
}

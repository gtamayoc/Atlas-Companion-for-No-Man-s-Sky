package com.gtamayoc.atlasnms.shared.data.repository

import com.gtamayoc.atlasnms.shared.domain.model.Discovery
import com.gtamayoc.atlasnms.shared.domain.repository.DiscoveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryDiscoveryRepository : DiscoveryRepository {
    private val discoveries = MutableStateFlow<List<Discovery>>(emptyList())

    override suspend fun initialize() {
        // Sin operaciones asíncronas de inicialización requeridas para simulación en memoria
    }

    override fun getAllDiscoveries(): Flow<List<Discovery>> {
        return discoveries
    }

    override suspend fun getDiscoveryById(id: String): Discovery? {
        return discoveries.value.find { it.id == id }
    }

    override suspend fun saveDiscovery(discovery: Discovery) {
        val current = discoveries.value.toMutableList()
        val index = current.indexOfFirst { it.id == discovery.id }
        if (index >= 0) {
            current[index] = discovery
        } else {
            current.add(0, discovery)
        }
        discoveries.value = current
    }

    override suspend fun deleteDiscovery(id: String) {
        discoveries.value = discoveries.value.filter { it.id != id }
    }

    override suspend fun syncWithMockData() {
        // Inicializar datos de simulación si se requiere
    }
}

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
        val mockData = listOf(
            Discovery(
                id = "mock_1",
                type = DiscoveryType.SHIP,
                name = "Exotic S-Class Royal",
                galaxy = "Euclid",
                systemName = "Othaen V",
                glyphs = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB7jgYT0gtUPxrVosBjE08RMHVRlvzZ_4qCHBJ3PObx64t8f46mIFPzgSWc7f7MhTGnPsowXt90Uzcyd1F5vp8TrYikR6_zkOK_MQHeRvwr5kCFGz3MaXXgnJS8D_3T0WI3fdBwE7dyUsHwLrckBVXRQRRtwa7Kdk2TGSuWLMDYG_pknjIEKDdC9dp4h4d6PuafMz0H9vQSN83nw2Biw47_043Tn8mBuDk5RvkFiqUNk3dqjXmp-vH9vQ",
                timestamp = 1718000000000,
                status = DiscoveryStatus.CONFIRMED
            ),
            Discovery(
                id = "mock_2",
                type = DiscoveryType.PLANET,
                name = "Elyria Prime",
                galaxy = "Euclid",
                systemName = "Alpha Sector",
                glyphs = listOf(12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuANDVTdBGRPWiCGa3yW4-3DpXoVxnRPQUz7Yl1Z4DyH-5zFzKECRCCXZ9ACwwd9hSnyUsQWsA7zrot3hu4mRczWYmIjHH6hoqvKWjVcS8nYfa8D8XKGLGekayFplez03jYGea5xWUKMIECAGvCWCuFjEEAgLY24VIIP75Cnxem4mJTiVFXHL6lhoOpqbMl5onumBfcTZKrVmK0RqdS3_6WgUDwpzviJqT7jzDDDHEVWmLWhU9bX_IjVLQ",
                timestamp = 1717000000000,
                status = DiscoveryStatus.CONFIRMED
            ),
            Discovery(
                id = "mock_3",
                type = DiscoveryType.FAUNA,
                name = "Atlas-Titan",
                galaxy = "Eissentam",
                systemName = "Nodo 44",
                glyphs = listOf(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4),
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCF4Q40DZKLJrNRPe7BQLKpbiSvZV3GCsUgMFGtPdfw63Uuoy7ULQzzlKFM4wO6uLuECP6EFTgnGnMJylG7QQHnFLaTD69nQFwJjRI97tt_qGZxXFWOiVsDUNLRJmK5HIqryH-ktdYCWciaj9nVFji0Y_-3VXdtlumvgnNAanxK15QhzNiLUlf-dH6kTt7RBx0ur8G8lFuCpWXArlB2ST_X_3NupWzUKSUzsafPlePFRolCBsP5RnzzUg",
                timestamp = 1716000000000,
                status = DiscoveryStatus.CONFIRMED
            )
        )
        
        mockData.forEach { saveDiscovery(it) }
        
        // Procedurally generated mock data to test list fluidity (LazyColumn)
        (4..20).forEach { i ->
            saveDiscovery(
                Discovery(
                    id = "mock_$i",
                    type = if (i % 2 == 0) DiscoveryType.SHIP else DiscoveryType.PLANET,
                    name = if (i % 2 == 0) "Sentinel Interceptor V$i" else "Lush Planet $i",
                    galaxy = "Euclid",
                    systemName = "System $i-Alpha",
                    glyphs = (1..12).map { (1..16).random() },
                    imageUrl = if (i % 2 == 0) "https://lh3.googleusercontent.com/aida-public/AB6AXuB7jgYT0gtUPxrVosBjE08RMHVRlvzZ_4qCHBJ3PObx64t8f46mIFPzgSWc7f7MhTGnPsowXt90Uzcyd1F5vp8TrYikR6_zkOK_MQHeRvwr5kCFGz3MaXXgnJS8D_3T0WI3fdBwE7dyUsHwLrckBVXRQRRtwa7Kdk2TGSuWLMDYG_pknjIEKDdC9dp4h4d6PuafMz0H9vQSN83nw2Biw47_043Tn8mBuDk5RvkFiqUNk3dqjXmp-vH9vQ" else "https://lh3.googleusercontent.com/aida-public/AB6AXuANDVTdBGRPWiCGa3yW4-3DpXoVxnRPQUz7Yl1Z4DyH-5zFzKECRCCXZ9ACwwd9hSnyUsQWsA7zrot3hu4mRczWYmIjHH6hoqvKWjVcS8nYfa8D8XKGLGekayFplez03jYGea5xWUKMIECAGvCWCuFjEEAgLY24VIIP75Cnxem4mJTiVFXHL6lhoOpqbMl5onumBfcTZKrVmK0RqdS3_6WgUDwpzviJqT7jzDDDHEVWmLWhU9bX_IjVLQ",
                    timestamp = 1718000000000 - (i * 100000),
                    status = DiscoveryStatus.CONFIRMED
                )
            )
        }
    }
}

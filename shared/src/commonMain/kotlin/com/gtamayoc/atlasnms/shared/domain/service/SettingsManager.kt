package com.gtamayoc.atlasnms.shared.domain.service

import com.gtamayoc.atlasnms.shared.cache.AtlasDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

object SettingsManager {
    private val mutex = Mutex()
    private var isInitialized = false
    private var database: AtlasDatabase? = null

    private val _deepSeekApiKey = MutableStateFlow("")
    val deepSeekApiKeyFlow: StateFlow<String> = _deepSeekApiKey.asStateFlow()
    val deepSeekApiKey: String get() = _deepSeekApiKey.value

    private val _deepSeekModel = MutableStateFlow("deepseek-chat")
    val deepSeekModelFlow: StateFlow<String> = _deepSeekModel.asStateFlow()
    val deepSeekModel: String get() = _deepSeekModel.value

    private val _deepSeekBaseUrl = MutableStateFlow("https://api.deepseek.com")
    val deepSeekBaseUrlFlow: StateFlow<String> = _deepSeekBaseUrl.asStateFlow()
    val deepSeekBaseUrl: String get() = _deepSeekBaseUrl.value

    suspend fun initialize(db: AtlasDatabase) = withContext(Dispatchers.Default) {
        mutex.withLock {
            if (isInitialized) return@withContext
            database = db
            try {
                val queries = db.atlasDatabaseQueries
                val savedKey = queries.selectSetting("deepseek_api_key").executeAsOneOrNull()
                val savedModel = queries.selectSetting("deepseek_model").executeAsOneOrNull()
                val savedUrl = queries.selectSetting("deepseek_base_url").executeAsOneOrNull()

                if (savedKey != null) _deepSeekApiKey.value = savedKey
                if (savedModel != null) _deepSeekModel.value = savedModel
                if (savedUrl != null) _deepSeekBaseUrl.value = savedUrl
                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Guarda la configuración de forma atómica en SQLite sin provocar escrituras constantes por tecla.
     */
    suspend fun saveAllSettings(newApiKey: String, newModel: String, newBaseUrl: String) = withContext(Dispatchers.Default) {
        mutex.withLock {
            _deepSeekApiKey.value = newApiKey
            _deepSeekModel.value = newModel
            _deepSeekBaseUrl.value = newBaseUrl

            val db = database ?: return@withContext
            try {
                val queries = db.atlasDatabaseQueries
                queries.insertSetting("deepseek_api_key", newApiKey)
                queries.insertSetting("deepseek_model", newModel)
                queries.insertSetting("deepseek_base_url", newBaseUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isApiKeyConfigured(): Boolean {
        return deepSeekApiKey.isNotBlank()
    }
}

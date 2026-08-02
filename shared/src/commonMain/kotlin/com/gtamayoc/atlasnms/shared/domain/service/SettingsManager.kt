package com.gtamayoc.atlasnms.shared.domain.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gtamayoc.atlasnms.shared.cache.AtlasDatabase

object SettingsManager {
    private var database: AtlasDatabase? = null

    var deepSeekApiKey by mutableStateOf("")
        private set

    var deepSeekModel by mutableStateOf("deepseek-chat")
        private set

    var deepSeekBaseUrl by mutableStateOf("https://api.deepseek.com")
        private set

    fun initialize(db: AtlasDatabase) {
        database = db
        try {
            val queries = db.atlasDatabaseQueries
            val savedKey = queries.selectSetting("deepseek_api_key").executeAsOneOrNull()
            val savedModel = queries.selectSetting("deepseek_model").executeAsOneOrNull()
            val savedUrl = queries.selectSetting("deepseek_base_url").executeAsOneOrNull()

            if (savedKey != null) deepSeekApiKey = savedKey
            if (savedModel != null) deepSeekModel = savedModel
            if (savedUrl != null) deepSeekBaseUrl = savedUrl
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Guarda la configuración de forma atómica en SQLite sin provocar escrituras constantes por tecla.
     */
    fun saveAllSettings(newApiKey: String, newModel: String, newBaseUrl: String) {
        deepSeekApiKey = newApiKey
        deepSeekModel = newModel
        deepSeekBaseUrl = newBaseUrl

        val db = database ?: return
        try {
            val queries = db.atlasDatabaseQueries
            queries.insertSetting("deepseek_api_key", newApiKey)
            queries.insertSetting("deepseek_model", newModel)
            queries.insertSetting("deepseek_base_url", newBaseUrl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isApiKeyConfigured(): Boolean {
        return deepSeekApiKey.isNotBlank()
    }
}

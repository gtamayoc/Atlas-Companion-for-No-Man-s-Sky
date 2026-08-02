package com.gtamayoc.atlasnms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.gtamayoc.atlasnms.shared.cache.AtlasDatabase
import com.gtamayoc.atlasnms.shared.data.repository.DiscoveryRepositoryImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicializamos el Driver de SQLDelight específico de Android
        val driver = AndroidSqliteDriver(AtlasDatabase.Schema, applicationContext, "atlasnms.db")
        val database = AtlasDatabase(driver)
        val repository = DiscoveryRepositoryImpl(database)

        setContent {
            App(repository)
        }
    }
}
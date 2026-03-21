package dev.muthukumar.anjana_crm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.store.TokenStore
import dev.muthukumar.anjana_crm.navigation.AppNavGraph
import dev.muthukumar.anjana_crm.ui.theme.AnjanaCrmTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = TokenStore(this)
        ApiClient.init { runBlocking { store.token.first() } }
        setContent {
            AnjanaCrmTheme {
                AppNavGraph()
            }
        }
    }
}
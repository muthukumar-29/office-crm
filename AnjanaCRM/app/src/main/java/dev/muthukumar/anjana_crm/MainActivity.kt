package dev.muthukumar.anjana_crm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.store.TokenStore
import dev.muthukumar.anjana_crm.navigation.AppNavGraph
import dev.muthukumar.anjana_crm.ui.theme.AnjanaCrmTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import androidx.compose.foundation.layout.Box

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init ApiClient with stored token (if any)
        val store = TokenStore(this)
        ApiClient.init {
            runBlocking { store.token.first() }
        }

        setContent {
            AnjanaCrmTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A))
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// LoginViewModel.kt
// ─────────────────────────────────────────────────────────────
package dev.muthukumar.anjana_crm.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.LoginRequest
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val role: String? = null
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val store = TokenStore(app)
    private val _ui = MutableStateFlow(LoginUiState())
    val ui = _ui.asStateFlow()

    init {
        // Wire token to ApiClient interceptor
        ApiClient.init { kotlinx.coroutines.runBlocking { store.token.collect { return@collect } ; null } }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _ui.value = LoginUiState(loading = true)
            try {
                val resp = ApiClient.service.login(LoginRequest(email, password))
                val body = resp.body()
                if (resp.isSuccessful && body?.success == true && body.data != null) {
                    val d = body.data
                    store.save(d.token, d.role, d.name, d.email, d.id.toString())
                    // Re-init ApiClient with real token now stored
                    ApiClient.init { kotlinx.coroutines.runBlocking {
                        store.token.collect { return@collect }; null
                    }}
                    _ui.value = LoginUiState(role = d.role)
                } else {
                    _ui.value = LoginUiState(error = "Invalid credentials")
                }
            } catch (e: Exception) {
                _ui.value = LoginUiState(error = "Network error: ${e.message}")
            }
        }
    }
}

package dev.muthukumar.anjana_crm.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.LoginRequest
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
        // Wire token provider synchronously using first()
        viewModelScope.launch {
            val token = store.token.first()
            ApiClient.init { kotlinx.coroutines.runBlocking { store.token.first() } }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _ui.value = LoginUiState(loading = true)
            try {
                val resp = ApiClient.service.login(LoginRequest(email.trim(), password))
                val body = resp.body()

                if (resp.isSuccessful && body?.success == true && body.data != null) {
                    val d = body.data
                    // Save to DataStore
                    store.save(d.token, d.role, d.name, d.email, d.id.toString())
                    // Re-init ApiClient with the real token
                    ApiClient.init { kotlinx.coroutines.runBlocking { store.token.first() } }
                    // Signal success
                    _ui.value = LoginUiState(role = d.role)
                } else {
                    val msg = resp.errorBody()?.string()
                    _ui.value = LoginUiState(
                        error = if (msg?.contains("password", true) == true || resp.code() == 401)
                            "Invalid email or password"
                        else
                            "Login failed (${resp.code()})"
                    )
                }
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("connect", true) == true ||
                            e.message?.contains("refused", true) == true ->
                        "Cannot reach server. Check if backend is running and BASE_URL is correct."
                    e.message?.contains("timeout", true) == true ->
                        "Connection timed out. Check your network."
                    else -> "Network error: ${e.message}"
                }
                _ui.value = LoginUiState(error = errorMsg)
            }
        }
    }
}
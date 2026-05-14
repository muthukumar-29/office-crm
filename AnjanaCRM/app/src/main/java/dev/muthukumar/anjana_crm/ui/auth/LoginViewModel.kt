package dev.muthukumar.anjana_crm.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.LoginData
import dev.muthukumar.anjana_crm.data.model.LoginRequest
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val navigateTo: String? = null   // "admin" | "employee" | "student"
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val store = TokenStore(app)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // ── Staff / Admin login ─────────────────────────────
    fun login(email: String, password: String) {
        _uiState.value = LoginUiState(loading = true)
        viewModelScope.launch {
            try {
                val res  = ApiClient.service.login(LoginRequest(email, password))
                val body = res.body()
                if (res.isSuccessful && body != null) {
                    val data = body.data
                    if (data == null) {
                        _uiState.value = LoginUiState(error = "Invalid response from server")
                        return@launch
                    }
                    store.save(data.token, data.role, data.name, data.email, data.id.toString())
                    val dest = if (data.role.contains("ADMIN")) "admin" else "employee"
                    _uiState.value = LoginUiState(navigateTo = dest)
                } else {
                    val errMsg = res.errorBody()?.string() ?: "Invalid credentials"
                    _uiState.value = LoginUiState(error = cleanError(errMsg))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Network error: ${e.message}")
            }
        }
    }

    // ── Student login — rollNo = email = password ───────
    fun loginStudent(rollNo: String) {
        _uiState.value = LoginUiState(loading = true)
        viewModelScope.launch {
            try {
                val res = ApiClient.service.login(LoginRequest(rollNo, rollNo))
                val body = res.body()

                if (res.isSuccessful && body != null) {
                    saveAndNavigate(body.data, rollNo)
                } else {
                    val res2  = ApiClient.service.login(LoginRequest("$rollNo@student.crm", rollNo))
                    val body2 = res2.body()
                    if (res2.isSuccessful && body2 != null) {
                        saveAndNavigate(body2.data, rollNo)
                    } else {
                        _uiState.value = LoginUiState(
                            error = "Roll number not found. Please contact your administrator."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Network error: ${e.message}")
            }
        }
    }

    private suspend fun saveAndNavigate(data: LoginData?, fallbackName: String) {
        if (data == null) {
            _uiState.value = LoginUiState(error = "Invalid response from server")
            return
        }
        store.save(data.token, data.role, data.name, data.email, data.id.toString())
        _uiState.value = LoginUiState(navigateTo = "student")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun cleanError(raw: String): String {
        return try {
            Regex(""""message"\s*:\s*"([^"]+)"""").find(raw)?.groupValues?.get(1) ?: raw
        } catch (e: Exception) { raw }
    }
}

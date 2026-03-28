package dev.muthukumar.anjana_crm.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.LoginRequest
import dev.muthukumar.anjana_crm.domain.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val navigateTo: String? = null   // "admin" | "employee" | "student"
)

class LoginViewModel : ViewModel() {

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
                    val data  = body.data ?: body
                    val token = data.token ?: ""
                    val role  = data.role  ?: "EMPLOYEE"
                    val name  = data.name  ?: email
                    val id    = data.id    ?: data.userId ?: ""
                    SessionManager.save(token, role, name, id.toString())
                    val dest = if (role.contains("ADMIN")) "admin" else "employee"
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
                // First try rollNo directly as email (preferred)
                val res = ApiClient.service.login(LoginRequest(rollNo, rollNo))
                val body = res.body()

                if (res.isSuccessful && body != null) {
                    saveAndNavigate(body.data ?: body, rollNo)
                } else {
                    // Fallback: try email format
                    val res2  = ApiClient.service.login(LoginRequest("$rollNo@student.crm", rollNo))
                    val body2 = res2.body()
                    if (res2.isSuccessful && body2 != null) {
                        saveAndNavigate(body2.data ?: body2, rollNo)
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

    private fun saveAndNavigate(data: Any, fallbackName: String) {
        // data could be LoginResponse or nested data object — use reflection-safe approach
        val token = getField(data, "token") ?: ""
        val role  = getField(data, "role")  ?: "STUDENT"
        val name  = getField(data, "name")  ?: fallbackName
        val id    = getField(data, "id")    ?: getField(data, "userId") ?: ""
        SessionManager.save(token, role, name, id)
        _uiState.value = LoginUiState(navigateTo = "student")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getField(obj: Any, field: String): String? {
        return try {
            val f = obj.javaClass.getDeclaredField(field)
            f.isAccessible = true
            f.get(obj)?.toString()
        } catch (e: Exception) { null }
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
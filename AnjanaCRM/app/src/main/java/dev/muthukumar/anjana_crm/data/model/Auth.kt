package dev.muthukumar.anjana_crm.data.model

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val success: Boolean,
    val data: LoginData?
)

data class LoginData(
    val token: String,
    val id: Long,
    val userId: String,
    val name: String,
    val email: String,
    val role: String
)

data class ApiResponse<T>(val success: Boolean, val data: T?, val message: String? = null)
data class ApiListResponse<T>(val success: Boolean, val data: List<T>?)

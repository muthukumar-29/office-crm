package dev.muthukumar.anjana_crm.data.model

data class User(
    val id: Long,
    val userId: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val position: String?,
    val employmentType: String?
)

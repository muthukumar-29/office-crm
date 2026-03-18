package dev.muthukumar.anjana_crm.data.model

data class Student(
    val id: Long,
    val studentId: String,
    val name: String,
    val email: String,
    val phone: String?,
    val collegeName: String?,
    val department: String?,
    val rollNo: String?
)

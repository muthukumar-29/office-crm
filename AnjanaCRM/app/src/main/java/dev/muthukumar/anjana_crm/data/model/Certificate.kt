package dev.muthukumar.anjana_crm.data.model

data class Certificate(
    val id: Long,
    val certificateNumber: String,
    val studentName: String,
    val programTitle: String?,
    val domainName: String?,
    val category: String?,
    val issuedDate: String,
    val grade: String?
)

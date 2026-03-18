package dev.muthukumar.anjana_crm.data.model

data class Payment(
    val id: Long,
    val amount: Double,
    val paymentMode: String,
    val paymentDate: String,
    val receiptNumber: String,
    val remarks: String?
)

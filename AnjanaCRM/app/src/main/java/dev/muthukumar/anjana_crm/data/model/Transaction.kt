package dev.muthukumar.anjana_crm.data.model

data class Transaction(
    val id: Long = 0,
    val type: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String? = null,
    val paymentMode: String? = null,
    val transactionDate: String = "",
    val referenceNo: String? = null,
    val notes: String? = null
)


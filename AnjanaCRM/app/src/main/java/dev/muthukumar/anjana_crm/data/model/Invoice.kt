package dev.muthukumar.anjana_crm.data.model

data class Invoice(
    val id: Long,
    val invoiceNumber: String,
    val clientName: String,
    val invoiceDate: String,
    val totalAmount: Double,
    val status: String
)

data class FinanceSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double,
    val periodStart: String,
    val periodEnd: String
)

data class FinanceTransaction(
    val id: Long,
    val type: String,
    val amount: Double,
    val category: String,
    val description: String?,
    val transactionDate: String
)

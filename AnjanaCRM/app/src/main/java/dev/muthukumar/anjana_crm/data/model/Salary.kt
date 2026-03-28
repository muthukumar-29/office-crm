package dev.muthukumar.anjana_crm.data.model

data class Salary(
    val id: Long = 0,
    val employee: User? = null,
    val payMonth: String? = null,
    val basicSalary: Double? = null,
    val hra: Double? = null,
    val transportAllowance: Double? = null,
    val otherAllowance: Double? = null,
    val bonus: Double? = null,
    val grossSalary: Double? = null,
    val pfDeduction: Double? = null,
    val taxDeduction: Double? = null,
    val otherDeduction: Double? = null,
    val netSalary: Double? = null,
    val paymentMode: String? = null,
    val transactionRef: String? = null,
    val notes: String? = null,
    val status: String? = null,
    val paidAt: String? = null
)

data class SalaryRequest(
    val employeeId: Long,
    val payMonth: String,
    val basicSalary: Double,
    val hra: Double = 0.0,
    val transportAllowance: Double = 0.0,
    val otherAllowance: Double = 0.0,
    val bonus: Double = 0.0,
    val pfDeduction: Double = 0.0,
    val taxDeduction: Double = 0.0,
    val otherDeduction: Double = 0.0,
    val paymentMode: String = "BANK_TRANSFER"
)

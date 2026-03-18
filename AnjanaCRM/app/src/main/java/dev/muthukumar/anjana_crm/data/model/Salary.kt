package dev.muthukumar.anjana_crm.data.model

data class Salary(
    val id: Long,
    val payMonth: String,
    val basicSalary: Double,
    val grossSalary: Double,
    val netSalary: Double,
    val pfDeduction: Double,
    val taxDeduction: Double,
    val otherDeduction: Double,
    val status: String,
    val paymentMode: String?,
    val employee: User?
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

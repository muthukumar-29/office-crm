package dev.muthukumar.anjana_crm.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login              : Screen("login")

    // Admin / Super Admin
    object AdminDashboard     : Screen("admin_dashboard")
    object AdminAllocations   : Screen("admin_allocations")
    object AdminStudents      : Screen("admin_students")
    object AdminCertificate   : Screen("admin_certificate")
    object AdminSalary        : Screen("admin_salary")
    object AdminInvoice       : Screen("admin_invoice")
    object AdminFinance       : Screen("admin_finance")

    // Employee
    object EmployeeDashboard  : Screen("emp_dashboard")
    object EmployeeAllocations: Screen("emp_allocations")
    object EmployeeProfile    : Screen("emp_profile")
    object UpdateStatus       : Screen("update_status/{allocationId}") {
        fun buildRoute(id: Long) = "update_status/$id"
    }

    // Student
    object StudentDashboard   : Screen("student_dashboard")
    object StudentStatus      : Screen("student_status")
    object StudentPayments    : Screen("student_payments")
    object StudentCertificate : Screen("student_certificate")
    object StudentTicket      : Screen("student_ticket")
}

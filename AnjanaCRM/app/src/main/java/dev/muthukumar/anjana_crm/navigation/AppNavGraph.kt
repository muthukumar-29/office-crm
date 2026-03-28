package dev.muthukumar.anjana_crm.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.muthukumar.anjana_crm.data.store.TokenStore
import dev.muthukumar.anjana_crm.ui.admin.*
import dev.muthukumar.anjana_crm.ui.auth.LoginScreen
import dev.muthukumar.anjana_crm.ui.employee.*
import dev.muthukumar.anjana_crm.ui.student.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val store = remember { TokenStore(context) }
    val navController = rememberNavController()

    val startDest = remember {
        val token = runBlocking { store.token.first() }
        val role  = runBlocking { store.role.first() }
        if (token == null) Screen.Login.route else roleStartScreen(role)
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val dest = when (vm.uiState.value.navigateTo) {
                        "admin"    -> Screen.AdminDashboard.route
                        "employee" -> Screen.EmployeeDashboard.route
                        "student"  -> Screen.StudentDashboard.route
                        else       -> Screen.AdminDashboard.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Admin / Super Admin ───────────────────────────────
        composable(Screen.AdminDashboard.route)     { AdminDashboardScreen(navController) }
        composable(Screen.AdminAllocations.route)   { AdminAllocationsScreen(navController) }
        composable(Screen.AdminStudents.route)      { AdminStudentsScreen(navController) }
        composable(Screen.AdminCertificate.route)   { AdminCertificateScreen(navController) }
        composable(Screen.AdminSalary.route)        { AdminSalaryScreen(navController) }
        composable(Screen.AdminInvoice.route)       { AdminInvoiceScreen(navController) }
        composable(Screen.AdminFinance.route)       { AdminFinanceScreen(navController) }
        composable(Screen.AdminProfile.route)       { AdminProfileScreen(navController) }
        composable(Screen.AdminUsers.route)         { AdminUsersScreen(navController) }

        // ── Employee ──────────────────────────────────────────
        composable(Screen.EmployeeDashboard.route)   { EmployeeDashboardScreen(navController) }
        composable(Screen.EmployeeAllocations.route) { MyAllocationsScreen(navController) }
        composable(Screen.EmployeeProfile.route)     { EmployeeProfileScreen(navController) }
        composable(Screen.UpdateStatus.route) { back ->
            val id = back.arguments?.getString("allocationId")?.toLongOrNull() ?: 0L
            UpdateStatusScreen(navController = navController, allocationId = id)
        }

        // ── Student ───────────────────────────────────────────
        composable(Screen.StudentDashboard.route)   { StudentDashboardScreen(navController) }
        composable(Screen.StudentStatus.route)      { StudentStatusScreen(navController) }
        composable(Screen.StudentPayments.route)    { PaymentHistoryScreen(navController) }
        composable(Screen.StudentCertificate.route) { StudentCertificateScreen(navController) }
        composable(Screen.StudentTicket.route)      { TicketScreen(navController) }
    }
}

fun roleStartScreen(role: String?): String = when (role) {
    "SUPER_ADMIN", "ADMIN" -> Screen.AdminDashboard.route
    "EMPLOYEE", "SUB_ADMIN" -> Screen.EmployeeDashboard.route
    "STUDENT" -> Screen.StudentDashboard.route
    else -> Screen.Login.route
}

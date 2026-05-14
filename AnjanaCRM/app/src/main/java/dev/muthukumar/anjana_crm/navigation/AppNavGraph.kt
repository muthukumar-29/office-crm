package dev.muthukumar.anjana_crm.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.muthukumar.anjana_crm.ui.admin.*
import dev.muthukumar.anjana_crm.ui.auth.LoginScreen
import dev.muthukumar.anjana_crm.ui.auth.SplashScreen
import dev.muthukumar.anjana_crm.ui.employee.*
import dev.muthukumar.anjana_crm.ui.student.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // ── Splash ────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen { dest ->
                navController.navigate(dest) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }

        // ── Auth ──────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navigateTo ->
                    val route = when (navigateTo) {
                        "admin"    -> Screen.AdminDashboard.route
                        "employee" -> Screen.EmployeeDashboard.route
                        "student"  -> Screen.StudentDashboard.route
                        else       -> Screen.AdminDashboard.route
                    }
                    navController.navigate(route) {
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
    "SUPER_ADMIN", "ADMIN"        -> Screen.AdminDashboard.route
    "EMPLOYEE", "SUB_ADMIN"       -> Screen.EmployeeDashboard.route
    "STUDENT"                     -> Screen.StudentDashboard.route
    else                          -> Screen.Login.route
}

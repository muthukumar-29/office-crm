package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminSalaryScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val userName    by vm.userName.collectAsState(initial = "")
    val role        by vm.role.collectAsState(initial = "")
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = White, drawerTonalElevation = 0.dp) {
                AppDrawer(
                    userName = userName, userRole = role, currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onLogout = {
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    drawerItems = adminDrawerItems()
                )
            }
        }
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = { CrmTopBar("Salary & Payroll", onMenuClick = { scope.launch { drawerState.open() } }) }
        ) { padding ->
            if (state.loading) LoadingView()
            else LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val paid    = state.salaries.filter { it.status == "PAID" }.sumOf { it.netSalary }
                        val pending = state.salaries.filter { it.status == "PENDING" }.sumOf { it.netSalary }
                        Box(Modifier.weight(1f)) {
                            StatCard("Paid", "₹${"%,.0f".format(paid)}", SuccessGreen, "✅")
                        }
                        Box(Modifier.weight(1f)) {
                            StatCard("Pending", "₹${"%,.0f".format(pending)}", WarningAmber, "⏳")
                        }
                    }
                }
                items(state.salaries) { salary ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(salary.employee?.name ?: "Employee", fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold, color = OnSurface)
                                    Text(salary.payMonth, fontSize = 12.sp, color = OnSurfaceMuted)
                                }
                                StatusBadge(salary.status)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text("Gross", fontSize = 11.sp, color = OnSurfaceMuted)
                                    Text("₹${"%,.0f".format(salary.grossSalary)}",
                                        fontSize = 13.sp, color = OnSurfaceMuted)
                                }
                                Column {
                                    Text("Net Pay", fontSize = 11.sp, color = OnSurfaceMuted)
                                    Text("₹${"%,.0f".format(salary.netSalary)}", fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold, color = BrandMagenta)
                                }
                            }
                            if (salary.status == "PENDING") {
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = { vm.markSalaryPaid(salary.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) { Text("Mark as Paid", fontWeight = FontWeight.SemiBold) }
                            }
                        }
                    }
                }
            }
        }
    }
}
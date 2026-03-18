package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminFinanceScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val userName    by vm.userName.collectAsState(initial = "")
    val role        by vm.role.collectAsState(initial = "")
    val summary      = state.financeSummary
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
            topBar = { CrmTopBar("Finance", onMenuClick = { scope.launch { drawerState.open() } }) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Summary cards
                item {
                    // Net balance hero card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandMagenta),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Net Balance", fontSize = 13.sp, color = White.copy(alpha = 0.8f))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "₹${"%,.0f".format(summary?.netBalance ?: 0.0)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = White
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Income", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                    Text(
                                        "₹${"%,.0f".format(summary?.totalIncome ?: 0.0)}",
                                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFB9F6CA)
                                    )
                                }
                                Box(Modifier.width(1.dp).height(32.dp).background(White.copy(alpha = 0.3f)))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Expense", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                    Text(
                                        "₹${"%,.0f".format(summary?.totalExpense ?: 0.0)}",
                                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFFCDD2)
                                    )
                                }
                            }
                        }
                    }
                }

                // Salary summary
                item {
                    SectionCard("Salary Overview") {
                        val paid    = state.salaries.count { it.status == "PAID" }
                        val pending = state.salaries.count { it.status == "PENDING" }
                        val totalAmt = state.salaries.sumOf { it.netSalary }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FinanceMiniStat("Records", "${state.salaries.size}", OnSurface)
                            FinanceMiniStat("Paid", "$paid", SuccessGreen)
                            FinanceMiniStat("Pending", "$pending", WarningAmber)
                            FinanceMiniStat("Total", "₹${"%,.0f".format(totalAmt)}", BrandMagenta)
                        }
                    }
                }

                // Invoice summary
                item {
                    SectionCard("Invoice Overview") {
                        val paidAmt = state.invoices
                            .filter { it.status == "PAID" }
                            .sumOf { it.totalAmount }
                        val unpaidAmt = state.invoices
                            .filter { it.status != "PAID" }
                            .sumOf { it.totalAmount }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            FinanceMiniStat("Total", "${state.invoices.size}", OnSurface)
                            FinanceMiniStat("Paid", "₹${"%,.0f".format(paidAmt)}", SuccessGreen)
                            FinanceMiniStat("Pending", "₹${"%,.0f".format(unpaidAmt)}", WarningAmber)
                        }
                    }
                }

                // Recent transactions
                if (state.transactions.isNotEmpty()) {
                    item {
                        Text("Recent Transactions", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = OnSurface)
                    }
                    items(state.transactions.take(20)) { txn ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Icon dot
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (txn.type == "INCOME") Color(0xFFDCF5E8)
                                            else Color(0xFFFFE8E8)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (txn.type == "INCOME") "↑" else "↓",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (txn.type == "INCOME") SuccessGreen else ErrorRed
                                    )
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        txn.category,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurface
                                    )
                                    txn.description?.let {
                                        Text(it, fontSize = 11.sp, color = OnSurfaceMuted)
                                    }
                                    Text(txn.transactionDate, fontSize = 11.sp, color = OnSurfaceHint)
                                }
                                Text(
                                    "${if (txn.type == "INCOME") "+" else "-"}₹${"%,.0f".format(txn.amount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (txn.type == "INCOME") SuccessGreen else ErrorRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceMiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = OnSurfaceMuted)
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
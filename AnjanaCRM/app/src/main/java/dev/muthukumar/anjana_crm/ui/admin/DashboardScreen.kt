package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.data.model.Allocation
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

// ── Drawer items — only core Icons.Default used ───────────────
fun adminDrawerItems() = listOf(
    DrawerItem(Icons.Default.Home,     "Dashboard",    Screen.AdminDashboard.route),
    DrawerItem(Icons.Default.Person,   "Students",     Screen.AdminStudents.route),
    DrawerItem(Icons.Default.List,     "Allocations",  Screen.AdminAllocations.route),
    DrawerItem(Icons.Default.Star,     "Certificates", Screen.AdminCertificate.route),
    DrawerItem(Icons.Default.Settings, "Salary",       Screen.AdminSalary.route),
    DrawerItem(Icons.Default.Info,     "Invoices",     Screen.AdminInvoice.route),
    DrawerItem(Icons.Default.Build,    "Finance",      Screen.AdminFinance.route),
    DrawerItem(Icons.Default.Person,   "Users",        Screen.AdminUsers.route),
    DrawerItem(Icons.Default.Star,     "Profile",      Screen.AdminProfile.route),
)

@Composable
fun AdminDashboardScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val userName    by vm.userName.collectAsState(initial = "Admin")
    val role        by vm.role.collectAsState(initial = "ADMIN")
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = White, drawerTonalElevation = 0.dp) {
                AppDrawer(
                    userName = userName,
                    userRole = role,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    drawerItems = adminDrawerItems()
                )
            }
        }
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = {
                CrmTopBar(
                    title = "Dashboard",
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { padding ->
            when {
                state.loading -> LoadingView()
                state.error != null -> ErrorView(state.error!!) { vm.loadAll() }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "Hello, ${userName?.split(" ")?.first() ?: "Admin"} 👋",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface
                            )
                            Text(
                                "Here's what's happening today",
                                fontSize = 13.sp, color = OnSurfaceMuted
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.weight(1f)) {
                                StatCard("Students", "${state.students.size}", BrandMagenta, "🎓")
                            }
                            Box(Modifier.weight(1f)) {
                                StatCard("Allocations", "${state.allocations.size}", BrandPurple, "📋")
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.weight(1f)) {
                                StatCard("Certificates", "${state.certificates.size}", SuccessGreen, "🏆")
                            }
                            Box(Modifier.weight(1f)) {
                                state.financeSummary?.let {
                                    StatCard(
                                        "Net Balance",
                                        "₹${"%,.0f".format(it.netBalance)}",
                                        if (it.netBalance >= 0) SuccessGreen else ErrorRed,
                                        "💰"
                                    )
                                } ?: StatCard("Invoices", "${state.invoices.size}", InfoBlue, "🧾")
                            }
                        }
                    }
                    item {
                        SectionCard("Quick Actions") {
                            val actions = listOf(
                                Triple("📋", "Allocations",  Screen.AdminAllocations.route),
                                Triple("🏆", "Certificates", Screen.AdminCertificate.route),
                                Triple("💵", "Salary",       Screen.AdminSalary.route),
                                Triple("🧾", "Invoices",     Screen.AdminInvoice.route),
                                Triple("📊", "Finance",      Screen.AdminFinance.route),
                                Triple("👥", "Students",     Screen.AdminStudents.route),
                            )
                            actions.chunked(3).forEach { row ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { (icon, label, route) ->
                                        OutlinedButton(
                                            onClick = { navController.navigate(route) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, BrandMagentaLight),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = BrandMagenta,
                                                containerColor = BrandMagentaLight.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(icon, fontSize = 18.sp)
                                                Text(label, fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent Allocations", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, color = OnSurface)
                            TextButton(onClick = {
                                navController.navigate(Screen.AdminAllocations.route)
                            }) { Text("See all", color = BrandMagenta, fontSize = 12.sp) }
                        }
                    }
                    items(state.allocations.take(6)) { alloc ->
                        AllocationCard(alloc) {
                            navController.navigate(Screen.AdminAllocations.route)
                        }
                    }
                    if (state.salaries.isNotEmpty()) {
                        item {
                            SectionCard("Salary Overview") {
                                val paid    = state.salaries.count { it.status == "PAID" }
                                val pending = state.salaries.count { it.status == "PENDING" }
                                val total   = state.salaries.sumOf { it.netSalary }
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    Column {
                                        Text("Total", fontSize = 11.sp, color = OnSurfaceMuted)
                                        Text("${state.salaries.size}", fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold, color = OnSurface)
                                    }
                                    Column {
                                        Text("Paid", fontSize = 11.sp, color = OnSurfaceMuted)
                                        Text("$paid", fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    }
                                    Column {
                                        Text("Pending", fontSize = 11.sp, color = OnSurfaceMuted)
                                        Text("$pending", fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold, color = WarningAmber)
                                    }
                                    Column {
                                        Text("Amount", fontSize = 11.sp, color = OnSurfaceMuted)
                                        Text("₹${"%,.0f".format(total)}", fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold, color = BrandMagenta)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllocationCard(alloc: Allocation, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick  = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(alloc.student?.name ?: "Unknown", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = OnSurface)
                Text(
                    "${alloc.category}: ${
                        alloc.course?.name ?: alloc.intern?.title ?: alloc.project?.title ?: "—"
                    }",
                    fontSize = 12.sp, color = OnSurfaceMuted
                )
                alloc.assignedEmployee?.let {
                    Text("👤 ${it.name}", fontSize = 11.sp, color = BrandMagenta)
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusBadge(alloc.allocationStatus)
                StatusBadge(alloc.paymentStatus)
            }
        }
    }
}

@Composable
fun AdminBottomNav(navController: NavController) {
    val items = listOf(
        Triple("Home",     Icons.Default.Home,     Screen.AdminDashboard.route),
        Triple("Students", Icons.Default.Person,   Screen.AdminStudents.route),
        Triple("Salary",   Icons.Default.Settings, Screen.AdminSalary.route),
        Triple("Finance",  Icons.Default.Star,     Screen.AdminFinance.route),
    )
    NavigationBar(containerColor = White, tonalElevation = 4.dp) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = { navController.navigate(route) { launchSingleTop = true } },
                icon     = { Icon(icon, null) },
                label    = { Text(label, fontSize = 11.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = BrandMagenta,
                    selectedTextColor   = BrandMagenta,
                    unselectedIconColor = OnSurfaceHint,
                    unselectedTextColor = OnSurfaceHint,
                    indicatorColor      = BrandMagentaLight
                )
            )
        }
    }
}
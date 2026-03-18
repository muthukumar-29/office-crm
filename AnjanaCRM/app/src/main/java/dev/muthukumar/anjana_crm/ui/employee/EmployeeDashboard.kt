package dev.muthukumar.anjana_crm.ui.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

fun employeeDrawerItems() = listOf(
    DrawerItem(Icons.Default.Home,   "Dashboard",     Screen.EmployeeDashboard.route),
    DrawerItem(Icons.Default.Build,  "My Allocations",Screen.EmployeeAllocations.route),
    DrawerItem(Icons.Default.Person, "My Profile",    Screen.EmployeeProfile.route),
)

@Composable
fun EmployeeDashboardScreen(navController: NavController, vm: EmployeeViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val userName    by vm.userName.collectAsState(initial = "Employee")
    val role        by vm.role.collectAsState(initial = "EMPLOYEE")
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
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
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                    drawerItems = employeeDrawerItems()
                )
            }
        }
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = {
                CrmTopBar("My Dashboard", onMenuClick = { scope.launch { drawerState.open() } })
            },
            bottomBar = { EmployeeBottomNav(navController) }
        ) { padding ->
            when {
                state.loading -> LoadingView()
                state.error != null -> ErrorView(state.error!!) { vm.loadAll() }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Column {
                            Text("Hello, ${userName?.split(" ")?.first() ?: "Employee"} 👋",
                                fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("Here are your assigned tasks", fontSize = 13.sp, color = OnSurfaceMuted)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.weight(1f)) {
                                StatCard("My Tasks", "${state.allocations.size}", BrandMagenta, "📋")
                            }
                            Box(Modifier.weight(1f)) {
                                StatCard("Active",
                                    "${state.allocations.count { it.allocationStatus == "ACTIVE" }}",
                                    SuccessGreen, "✅")
                            }
                        }
                    }
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("My Allocations", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, color = OnSurface)
                            TextButton(onClick = { navController.navigate(Screen.EmployeeAllocations.route) }) {
                                Text("See all", color = BrandMagenta, fontSize = 12.sp)
                            }
                        }
                    }
                    items(state.allocations.take(8)) { alloc ->
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
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(alloc.student?.name ?: "Student", fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold, color = OnSurface)
                                        Text("${alloc.category}: ${
                                            alloc.course?.name ?: alloc.intern?.title
                                            ?: alloc.project?.title ?: "—"
                                        }", fontSize = 12.sp, color = OnSurfaceMuted)
                                        alloc.classStartTime?.let {
                                            Text("⏰ $it – ${alloc.classEndTime}",
                                                fontSize = 11.sp, color = OnSurfaceHint)
                                        }
                                    }
                                    StatusBadge(alloc.allocationStatus)
                                }
                                Spacer(Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { navController.navigate(Screen.UpdateStatus.buildRoute(alloc.id)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandMagenta),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandMagenta)
                                ) { Text("Update Status", fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeBottomNav(navController: NavController) {
    val items = listOf(
        Triple("Home",    Icons.Default.Home,   Screen.EmployeeDashboard.route),
        Triple("My Work", Icons.Default.Build,  Screen.EmployeeAllocations.route),
        Triple("Profile", Icons.Default.Person, Screen.EmployeeProfile.route),
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
                    selectedIconColor   = BrandMagenta, selectedTextColor = BrandMagenta,
                    unselectedIconColor = OnSurfaceHint, unselectedTextColor = OnSurfaceHint,
                    indicatorColor      = BrandMagentaLight
                )
            )
        }
    }
}
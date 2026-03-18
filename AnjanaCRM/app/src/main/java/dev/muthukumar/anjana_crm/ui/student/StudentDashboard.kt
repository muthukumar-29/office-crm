package dev.muthukumar.anjana_crm.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

fun studentDrawerItems() = listOf(
    DrawerItem(Icons.Default.Home,           "Dashboard",    Screen.StudentDashboard.route),
    DrawerItem(Icons.Default.List,           "My Programs",  Screen.StudentStatus.route),
    DrawerItem(Icons.Default.Check,          "Payments",     Screen.StudentPayments.route),
    DrawerItem(Icons.Default.Star,           "Certificates", Screen.StudentCertificate.route),
    DrawerItem(Icons.Default.Info,           "Help & Feedback", Screen.StudentTicket.route),
)

@Composable
fun StudentDashboardScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val userName    by vm.userName.collectAsState(initial = "Student")
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = White, drawerTonalElevation = 0.dp) {
                AppDrawer(
                    userName = userName, userRole = "STUDENT", currentRoute = currentRoute,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route) { launchSingleTop = true }
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                    drawerItems = studentDrawerItems()
                )
            }
        }
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = {
                CrmTopBar("My Dashboard", onMenuClick = { scope.launch { drawerState.open() } })
            },
            bottomBar = { StudentBottomNav(navController) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Column {
                        Text("Hello, ${userName?.split(" ")?.first() ?: "Student"} 👋",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Track your programs and certificates",
                            fontSize = 13.sp, color = OnSurfaceMuted)
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            StatCard("Enrolled", "${state.allocations.size}", BrandMagenta, "📚")
                        }
                        Box(Modifier.weight(1f)) {
                            StatCard("Certificates", "${state.certificates.size}", SuccessGreen, "🏆")
                        }
                    }
                }
                item {
                    SectionCard("My Programs") {
                        if (state.allocations.isEmpty()) {
                            Text("No programs enrolled yet", color = OnSurfaceMuted, fontSize = 13.sp)
                        } else {
                            state.allocations.forEach { alloc ->
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            alloc.course?.name ?: alloc.intern?.title
                                            ?: alloc.project?.title ?: "—",
                                            color = OnSurface, fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(alloc.category, color = OnSurfaceHint, fontSize = 11.sp)
                                    }
                                    StatusBadge(
                                        alloc.projectStatus ?: alloc.internStatus
                                        ?: alloc.courseStatus ?: alloc.allocationStatus
                                    )
                                }
                                Divider(color = Outline)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentBottomNav(navController: NavController) {
    val items = listOf(
        Triple("Home",    Icons.Default.Home,  Screen.StudentDashboard.route),
        Triple("Programs",Icons.Default.List,  Screen.StudentStatus.route),
        Triple("Payments",Icons.Default.Check, Screen.StudentPayments.route),
        Triple("Certs",   Icons.Default.Star,  Screen.StudentCertificate.route),
        Triple("Help",    Icons.Default.Info,  Screen.StudentTicket.route),
    )
    NavigationBar(containerColor = White, tonalElevation = 4.dp) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = { navController.navigate(route) { launchSingleTop = true } },
                icon     = { Icon(icon, null) },
                label    = { Text(label, fontSize = 10.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = BrandMagenta, selectedTextColor = BrandMagenta,
                    unselectedIconColor = OnSurfaceHint, unselectedTextColor = OnSurfaceHint,
                    indicatorColor      = BrandMagentaLight
                )
            )
        }
    }
}
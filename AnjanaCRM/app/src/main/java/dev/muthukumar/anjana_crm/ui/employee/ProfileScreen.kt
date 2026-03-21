package dev.muthukumar.anjana_crm.ui.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun EmployeeProfileScreen(navController: NavController, vm: EmployeeViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val userName    by vm.userName.collectAsState(initial = "—")
    val role        by vm.role.collectAsState(initial = "—")
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val active       = state.allocations.count { it.allocationStatus == "ACTIVE" }
    val completed    = state.allocations.count { it.allocationStatus == "COMPLETED" }

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
            topBar = { CrmTopBar("My Profile", onMenuClick = { scope.launch { drawerState.open() } }) },
            bottomBar = { EmployeeBottomNav(navController) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(4.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(
                            Brush.horizontalGradient(listOf(BrandPurple, BrandMagenta))
                        ))
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.offset(y = (-32).dp).size(64.dp).clip(CircleShape)
                                .background(White), contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(
                                    Brush.radialGradient(listOf(BrandMagenta, BrandPurple))
                                ), contentAlignment = Alignment.Center) {
                                    Text(userName?.firstOrNull()?.uppercase() ?: "E",
                                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = White)
                                }
                            }
                            Text(userName ?: "Employee", fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, color = OnSurface,
                                modifier = Modifier.offset(y = (-20).dp))
                            Surface(color = BrandMagentaLight, shape = RoundedCornerShape(99.dp),
                                modifier = Modifier.offset(y = (-16).dp)) {
                                Text(role?.replace("_", " ") ?: "EMPLOYEE",
                                    fontSize = 11.sp, color = BrandMagenta,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) { StatCard("Active Tasks", "$active", BrandMagenta, "📋") }
                        Box(Modifier.weight(1f)) { StatCard("Completed", "$completed", SuccessGreen, "✅") }
                    }
                }
                item {
                    SectionCard("Company Info") {
                        listOf(
                            "📍" to "372, Mudangiyar Road, Rajapalayam",
                            "📞" to "+91 97879 70633",
                            "✉️" to "info@anjanainfotech.in",
                            "🌐" to "www.anjanainfotech.in",
                            "🏅" to "ISO 9001:2015 Certified"
                        ).forEach { (icon, value) ->
                            Row(modifier = Modifier.padding(vertical = 5.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top) {
                                Text(icon, fontSize = 14.sp)
                                Text(value, color = OnSurfaceMuted, fontSize = 13.sp)
                            }
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                    ) { Text("Sign Out", fontWeight = FontWeight.SemiBold, fontSize = 15.sp) }
                }
            }
        }
    }
}
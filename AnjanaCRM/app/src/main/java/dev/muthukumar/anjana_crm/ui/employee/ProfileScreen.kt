package dev.muthukumar.anjana_crm.ui.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*

@Composable
fun EmployeeProfileScreen(navController: NavController, vm: EmployeeViewModel = viewModel()) {
    val state    by vm.state.collectAsState()
    val userName by vm.userName.collectAsState(initial = "—")
    val role     by vm.role.collectAsState(initial = "—")

    val active    = state.allocations.count { it.allocationStatus == "ACTIVE" }
    val completed = state.allocations.count { it.allocationStatus == "COMPLETED" }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("My Profile") },
        bottomBar = { EmployeeBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Avatar card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = BrandBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = userName?.firstOrNull()?.uppercase() ?: "E",
                                color = BrandBlue,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.size(72.dp).wrapContentSize(Alignment.Center)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(userName ?: "Employee", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Surface(color = Color(0x220A50B4), shape = RoundedCornerShape(99.dp)) {
                            Text(role ?: "EMPLOYEE", fontSize = 11.sp, color = Color(0xFF60A5FA),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Anjana Infotech", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
            }

            // Stats
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.weight(1f)) { StatCard("Active Tasks", "$active", BrandBlue) }
                    Box(Modifier.weight(1f)) { StatCard("Completed", "$completed", Color(0xFF10B981)) }
                }
            }

            // Company info
            item {
                SectionCard("Company Info") {
                    listOf(
                        "📍" to "372, Mudangiyar Road, Rajapalayam",
                        "📞" to "+91 97879 70633",
                        "✉️"  to "info@anjanainfotech.in",
                        "🌐" to "www.anjanainfotech.in",
                        "🏅" to "ISO 9001:2015 Certified"
                    ).forEach { (icon, value) ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(icon, fontSize = 14.sp)
                            Text(value, color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    }
                }
            }

            // Sign out
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44EF4444))
                ) { Text("Sign Out", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

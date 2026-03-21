package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dev.muthukumar.anjana_crm.data.model.Allocation
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminAllocationsScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val userName    by vm.userName.collectAsState(initial = "")
    val role        by vm.role.collectAsState(initial = "")
    var filterCat   by remember { mutableStateOf("ALL") }
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val categories = listOf("ALL", "COURSE", "INTERN", "PROJECT")
    val filtered   = if (filterCat == "ALL") state.allocations
    else state.allocations.filter { it.category == filterCat }

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
            topBar = { CrmTopBar("Allocations", onMenuClick = { scope.launch { drawerState.open() } }) }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = filterCat == cat,
                            onClick  = { filterCat = cat },
                            label    = { Text(cat, fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandMagenta,
                                selectedLabelColor     = White,
                                containerColor         = SurfaceVariant,
                                labelColor             = OnSurfaceMuted
                            )
                        )
                    }
                }
                if (state.loading) LoadingView()
                else LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text("${filtered.size} allocations", fontSize = 12.sp, color = OnSurfaceMuted)
                    }
                    items(filtered) { alloc ->
                        AdminAllocationDetailCard(alloc, vm)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAllocationDetailCard(alloc: Allocation, vm: AdminViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val allocOptions  = listOf("ACTIVE", "COMPLETED", "DROPPED", "ON_HOLD")
    val statusOptions = when (alloc.category.uppercase()) {
        "PROJECT" -> listOf("NOT_STARTED", "IN_PROGRESS", "REVIEW_PENDING", "DELIVERED", "COMPLETED")
        "INTERN"  -> listOf("ONGOING", "COMPLETED", "TERMINATED")
        else      -> listOf("ENROLLED", "IN_PROGRESS", "COMPLETED", "DROPPED")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick  = { expanded = !expanded }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(alloc.student?.name ?: "—", fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Text("${alloc.category}: ${
                        alloc.course?.name ?: alloc.intern?.title ?: alloc.project?.title ?: "—"
                    }", fontSize = 12.sp, color = OnSurfaceMuted)
                    alloc.assignedEmployee?.let {
                        Text("👤 ${it.name}", fontSize = 11.sp, color = BrandMagenta)
                    }
                    alloc.classStartTime?.let {
                        Text("⏰ $it – ${alloc.classEndTime}", fontSize = 11.sp, color = OnSurfaceHint)
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

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider(color = Outline)
                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column {
                        Text("Total Fee", fontSize = 11.sp, color = OnSurfaceMuted)
                        Text("₹${alloc.totalFee?.let { "%,.0f".format(it) } ?: "—"}",
                            fontSize = 13.sp, color = OnSurface)
                    }
                    Column {
                        Text("Paid", fontSize = 11.sp, color = OnSurfaceMuted)
                        Text("₹${alloc.amountPaid?.let { "%,.0f".format(it) } ?: "0"}",
                            fontSize = 13.sp, color = SuccessGreen)
                    }
                    Column {
                        Text("Balance", fontSize = 11.sp, color = OnSurfaceMuted)
                        Text("₹${alloc.balanceDue?.let { "%,.0f".format(it) } ?: "—"}",
                            fontSize = 13.sp,
                            color = if ((alloc.balanceDue ?: 0.0) > 0) ErrorRed else SuccessGreen)
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("Update Allocation Status", fontSize = 11.sp,
                    color = OnSurfaceMuted, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    allocOptions.forEach { opt ->
                        val isSelected = alloc.allocationStatus == opt
                        AssistChip(
                            onClick = {
                                vm.updateAllocationStatus(alloc.id, mapOf("allocationStatus" to opt))
                            },
                            label  = { Text(opt.replace("_", " "), fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) BrandMagenta else BrandMagentaLight,
                                labelColor     = if (isSelected) White else BrandMagenta
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Update Work Status", fontSize = 11.sp,
                    color = OnSurfaceMuted, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))

                val key = when (alloc.category.uppercase()) {
                    "PROJECT" -> "projectStatus"
                    "INTERN"  -> "internStatus"
                    else      -> "courseStatus"
                }
                val currentStatus = alloc.projectStatus ?: alloc.internStatus ?: alloc.courseStatus
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statusOptions.take(4).forEach { opt ->
                        val isSelected = currentStatus == opt
                        AssistChip(
                            onClick = {
                                vm.updateAllocationStatus(alloc.id, mapOf(key to opt))
                            },
                            label  = { Text(opt.replace("_", " "), fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) BrandPurple else BrandPurpleLight,
                                labelColor     = if (isSelected) White else BrandPurple
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
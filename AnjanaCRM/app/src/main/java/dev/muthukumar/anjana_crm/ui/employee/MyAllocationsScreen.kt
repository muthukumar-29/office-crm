package dev.muthukumar.anjana_crm.ui.employee

import androidx.compose.foundation.BorderStroke
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
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MyAllocationsScreen(navController: NavController, vm: EmployeeViewModel = viewModel()) {
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
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                    drawerItems = employeeDrawerItems()
                )
            }
        }
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = { CrmTopBar("My Allocations", onMenuClick = { scope.launch { drawerState.open() } }) },
            bottomBar = { EmployeeBottomNav(navController) }
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
                when {
                    state.loading -> LoadingView()
                    state.error != null -> ErrorView(state.error!!) { vm.loadAll() }
                    filtered.isEmpty() -> Box(
                        Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) { Text("No allocations found", color = OnSurfaceMuted, fontSize = 14.sp) }
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Text("${filtered.size} task${if (filtered.size != 1) "s" else ""}",
                                fontSize = 12.sp, color = OnSurfaceMuted)
                        }
                        items(filtered) { alloc ->
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
                                            alloc.startDate?.let {
                                                Text("📅 $it → ${alloc.endDate ?: "—"}",
                                                    fontSize = 11.sp, color = OnSurfaceHint)
                                            }
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            StatusBadge(alloc.allocationStatus)
                                            val ws = alloc.projectStatus ?: alloc.internStatus ?: alloc.courseStatus
                                            ws?.let { StatusBadge(it) }
                                        }
                                    }
                                    alloc.notes?.takeIf { it.isNotBlank() }?.let {
                                        Spacer(Modifier.height(8.dp))
                                        Surface(color = BrandPurpleLight, shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()) {
                                            Text("📝 $it", color = OnSurfaceMuted, fontSize = 12.sp,
                                                modifier = Modifier.padding(8.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    OutlinedButton(
                                        onClick = { navController.navigate(Screen.UpdateStatus.buildRoute(alloc.id)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, BrandMagenta),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandMagenta)
                                    ) { Text("Update Work Status", fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
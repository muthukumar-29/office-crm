package dev.muthukumar.anjana_crm.ui.employee

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
import dev.muthukumar.anjana_crm.navigation.Screen
import dev.muthukumar.anjana_crm.ui.common.*

@Composable
fun MyAllocationsScreen(navController: NavController, vm: EmployeeViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var filterCat by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "COURSE", "INTERN", "PROJECT")
    val filtered = if (filterCat == "ALL") state.allocations
    else state.allocations.filter { it.category == filterCat }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("My Allocations", onBack = { navController.popBackStack() }) },
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
                            selectedContainerColor = BrandBlue,
                            selectedLabelColor     = Color.White,
                            containerColor         = SurfaceDark,
                            labelColor             = Color(0xFF94A3B8)
                        )
                    )
                }
            }
            when {
                state.loading -> LoadingView()
                state.error != null -> ErrorView(state.error!!) { vm.loadAll() }
                filtered.isEmpty() -> Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) { Text("No allocations found", color = Color(0xFF64748B), fontSize = 14.sp) }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        Text("${filtered.size} task${if (filtered.size != 1) "s" else ""}",
                            fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    items(filtered) { alloc ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(alloc.student?.name ?: "Student", fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold, color = Color.White)
                                        Text("${alloc.category}: ${
                                            alloc.course?.name ?: alloc.intern?.title ?: alloc.project?.title ?: "—"
                                        }", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                        if (alloc.classStartTime != null)
                                            Text("⏰ ${alloc.classStartTime} – ${alloc.classEndTime}",
                                                fontSize = 11.sp, color = Color(0xFF64748B))
                                        alloc.startDate?.let {
                                            Text("📅 $it → ${alloc.endDate ?: "—"}",
                                                fontSize = 11.sp, color = Color(0xFF64748B))
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
                                    Surface(color = Color(0x0FFFFFFF), shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()) {
                                        Text("📝 $it", color = Color(0xFF94A3B8), fontSize = 12.sp,
                                            modifier = Modifier.padding(8.dp))
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = { navController.navigate(Screen.UpdateStatus.buildRoute(alloc.id)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A5F))
                                ) { Text("Update Work Status", fontSize = 12.sp, color = Color(0xFF60A5FA)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

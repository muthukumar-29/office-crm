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

@Composable
fun AdminAllocationsScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var filterCat by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "COURSE", "INTERN", "PROJECT")
    val filtered = if (filterCat == "ALL") state.allocations
    else state.allocations.filter { it.category == filterCat }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("Allocations", onBack = { navController.popBackStack() }) },
        bottomBar = { AdminBottomNav(navController) }
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
            if (state.loading) LoadingView()
            else LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item { Text("${filtered.size} allocations", fontSize = 12.sp, color = Color(0xFF64748B)) }
                items(filtered) { alloc -> AdminAllocationDetailCard(alloc, vm) }
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
        colors   = CardDefaults.cardColors(containerColor = SurfaceDark),
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
                        fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text("${alloc.category}: ${
                        alloc.course?.name ?: alloc.intern?.title ?: alloc.project?.title ?: "—"
                    }", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    alloc.assignedEmployee?.let {
                        Text("👤 ${it.name}", fontSize = 11.sp, color = Color(0xFF60A5FA))
                    }
                    if (alloc.classStartTime != null)
                        Text("⏰ ${alloc.classStartTime} – ${alloc.classEndTime}",
                            fontSize = 11.sp, color = Color(0xFF64748B))
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
                Divider(color = Color(0x1AFFFFFF))
                Spacer(Modifier.height(10.dp))

                // Fee info
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column {
                        Text("Total Fee", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("₹${alloc.totalFee?.let { "%,.0f".format(it) } ?: "—"}",
                            fontSize = 13.sp, color = Color.White)
                    }
                    Column {
                        Text("Paid", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("₹${alloc.amountPaid?.let { "%,.0f".format(it) } ?: "0"}",
                            fontSize = 13.sp, color = Color(0xFF10B981))
                    }
                    Column {
                        Text("Balance", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text("₹${alloc.balanceDue?.let { "%,.0f".format(it) } ?: "—"}",
                            fontSize = 13.sp,
                            color = if ((alloc.balanceDue ?: 0.0) > 0) Color(0xFFEF4444) else Color(0xFF10B981))
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("Update Allocation Status", fontSize = 11.sp,
                    color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))

                // Allocation status chips — fixed: use BorderStroke directly
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    allocOptions.forEach { opt ->
                        val isSelected = alloc.allocationStatus == opt
                        AssistChip(
                            onClick = {
                                vm.updateAllocationStatus(alloc.id, mapOf("allocationStatus" to opt))
                            },
                            label  = { Text(opt.replace("_", " "), fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) BrandBlue else Color(0xFF1E3A5F),
                                labelColor     = Color.White
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Update Work Status", fontSize = 11.sp,
                    color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))

                // Work status chips
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
                                containerColor = if (isSelected) BrandOrange else Color(0xFF1E293B),
                                labelColor     = Color.White
                            ),
                            border = BorderStroke(0.dp, Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
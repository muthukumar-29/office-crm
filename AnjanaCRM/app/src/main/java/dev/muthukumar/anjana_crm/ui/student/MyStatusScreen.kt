package dev.muthukumar.anjana_crm.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import dev.muthukumar.anjana_crm.ui.common.*

@Composable
fun StudentStatusScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("My Programs", onBack = { navController.popBackStack() }) },
        bottomBar = { StudentBottomNav(navController) }
    ) { padding ->
        when {
            state.loading -> LoadingView()
            state.allocations.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No programs enrolled yet", color = Color(0xFF64748B), fontSize = 14.sp)
                }
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.allocations) { alloc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    val programName = alloc.course?.name
                                        ?: alloc.intern?.title ?: alloc.project?.title ?: "Program"
                                    Text(programName, fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold, color = Color.White)
                                    Surface(
                                        color = when (alloc.category) {
                                            "COURSE"  -> Color(0x220A50B4)
                                            "INTERN"  -> Color(0x22E66414)
                                            else      -> Color(0x228B5CF6)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            alloc.category,
                                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                                            color = when (alloc.category) {
                                                "COURSE"  -> Color(0xFF60A5FA)
                                                "INTERN"  -> Color(0xFFE66414)
                                                else      -> Color(0xFF8B5CF6)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                StatusBadge(
                                    alloc.projectStatus ?: alloc.internStatus
                                    ?: alloc.courseStatus ?: alloc.allocationStatus
                                )
                            }

                            Spacer(Modifier.height(12.dp))
                            Divider(color = Color(0x1AFFFFFF))
                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                alloc.startDate?.let {
                                    Column {
                                        Text("Start", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text(it, fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                                alloc.endDate?.let {
                                    Column {
                                        Text("End", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text(it, fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                                alloc.classStartTime?.let {
                                    Column {
                                        Text("Timing", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("$it – ${alloc.classEndTime}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    }
                                }
                            }

                            alloc.assignedEmployee?.let { emp ->
                                Spacer(Modifier.height(10.dp))
                                Surface(color = Color(0x0F60A5FA), shape = RoundedCornerShape(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Person, null,
                                            tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                                        Text("Mentor: ${emp.name}", fontSize = 12.sp, color = Color(0xFF93C5FD))
                                    }
                                }
                            }

                            if (alloc.totalFee != null) {
                                Spacer(Modifier.height(10.dp))
                                Divider(color = Color(0x1AFFFFFF))
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                    Column {
                                        Text("Total Fee", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("₹${"%,.0f".format(alloc.totalFee)}", fontSize = 13.sp, color = Color.White)
                                    }
                                    Column {
                                        Text("Paid", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("₹${"%,.0f".format(alloc.amountPaid ?: 0.0)}", fontSize = 13.sp, color = Color(0xFF10B981))
                                    }
                                    Column {
                                        Text("Balance", fontSize = 10.sp, color = Color(0xFF64748B))
                                        Text("₹${"%,.0f".format(alloc.balanceDue ?: 0.0)}", fontSize = 13.sp,
                                            color = if ((alloc.balanceDue ?: 0.0) > 0) Color(0xFFF59E0B) else Color(0xFF10B981))
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

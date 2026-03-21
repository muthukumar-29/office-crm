// ─── MyStatusScreen.kt ──────────────────────────────────────
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*

@Composable
fun StudentStatusScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    Scaffold(
        containerColor = OffWhite,
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
                    Text("No programs enrolled yet", color = OnSurfaceMuted, fontSize = 14.sp)
                }
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.allocations) { alloc ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top) {
                                Column(Modifier.weight(1f)) {
                                    val programName = alloc.course?.name ?: alloc.intern?.title
                                    ?: alloc.project?.title ?: "Program"
                                    Text(programName, fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold, color = OnSurface)
                                    Surface(color = when (alloc.category) {
                                        "COURSE"  -> BrandMagentaLight
                                        "INTERN"  -> BrandPurpleLight
                                        else      -> SurfaceVariant
                                    }, shape = RoundedCornerShape(4.dp)) {
                                        Text(alloc.category, fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = when (alloc.category) {
                                                "COURSE"  -> BrandMagenta
                                                "INTERN"  -> BrandPurple
                                                else      -> OnSurfaceMuted
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                                StatusBadge(alloc.projectStatus ?: alloc.internStatus
                                ?: alloc.courseStatus ?: alloc.allocationStatus)
                            }
                            Spacer(Modifier.height(12.dp))
                            Divider(color = Outline)
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                alloc.startDate?.let {
                                    Column {
                                        Text("Start", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text(it, fontSize = 12.sp, color = OnSurfaceMuted)
                                    }
                                }
                                alloc.endDate?.let {
                                    Column {
                                        Text("End", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text(it, fontSize = 12.sp, color = OnSurfaceMuted)
                                    }
                                }
                                alloc.classStartTime?.let {
                                    Column {
                                        Text("Timing", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text("$it – ${alloc.classEndTime}", fontSize = 12.sp, color = OnSurfaceMuted)
                                    }
                                }
                            }
                            alloc.assignedEmployee?.let { emp ->
                                Spacer(Modifier.height(10.dp))
                                Surface(color = BrandMagentaLight, shape = RoundedCornerShape(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Person, null,
                                            tint = BrandMagenta, modifier = Modifier.size(16.dp))
                                        Text("Mentor: ${emp.name}", fontSize = 12.sp, color = BrandMagenta)
                                    }
                                }
                            }
                            if (alloc.totalFee != null) {
                                Spacer(Modifier.height(10.dp))
                                Divider(color = Outline)
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                    Column {
                                        Text("Total Fee", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text("₹${"%,.0f".format(alloc.totalFee)}", fontSize = 13.sp, color = OnSurface)
                                    }
                                    Column {
                                        Text("Paid", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text("₹${"%,.0f".format(alloc.amountPaid ?: 0.0)}", fontSize = 13.sp, color = SuccessGreen)
                                    }
                                    Column {
                                        Text("Balance", fontSize = 10.sp, color = OnSurfaceHint)
                                        Text("₹${"%,.0f".format(alloc.balanceDue ?: 0.0)}", fontSize = 13.sp,
                                            color = if ((alloc.balanceDue ?: 0.0) > 0) WarningAmber else SuccessGreen)
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
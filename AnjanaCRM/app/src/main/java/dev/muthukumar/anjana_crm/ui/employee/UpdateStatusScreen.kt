package dev.muthukumar.anjana_crm.ui.employee

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*

@Composable
fun UpdateStatusScreen(
    navController: NavController,
    allocationId: Long,
    vm: EmployeeViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val alloc = state.allocations.find { it.id == allocationId }
    var selected by remember { mutableStateOf("") }
    var saved    by remember { mutableStateOf(false) }

    val statusOptions = when (alloc?.category?.uppercase()) {
        "PROJECT" -> listOf("NOT_STARTED", "IN_PROGRESS", "REVIEW_PENDING", "DELIVERED", "COMPLETED")
        "INTERN"  -> listOf("ONGOING", "COMPLETED", "TERMINATED")
        else      -> listOf("ENROLLED", "IN_PROGRESS", "COMPLETED", "DROPPED")
    }

    LaunchedEffect(alloc) {
        selected = alloc?.projectStatus ?: alloc?.internStatus ?: alloc?.courseStatus ?: ""
    }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("Update Status", onBack = { navController.popBackStack() }) }
    ) { padding ->
        if (alloc == null) {
            LoadingView()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionCard("Allocation Info") {
                    Text(alloc.student?.name ?: "—", color = Color.White,
                        fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("${alloc.category}: ${
                        alloc.course?.name ?: alloc.intern?.title ?: alloc.project?.title ?: "—"
                    }", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    alloc.classStartTime?.let {
                        Text("⏰ $it – ${alloc.classEndTime}", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }

                SectionCard("Select New Status") {
                    statusOptions.forEach { opt ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selected == opt,
                                onClick  = { selected = opt; saved = false },
                                colors   = RadioButtonDefaults.colors(selectedColor = BrandBlue)
                            )
                            Text(
                                opt.replace("_", " "),
                                color = if (selected == opt) Color.White else Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (saved) {
                    Surface(
                        color = Color(0x2210B981),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "✓ Status updated successfully",
                            color = Color(0xFF10B981), fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        vm.updateWorkStatus(alloc.id, alloc.category, selected)
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selected.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) { Text("Save Status", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

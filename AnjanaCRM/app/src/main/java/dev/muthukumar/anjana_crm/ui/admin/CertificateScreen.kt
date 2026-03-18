package dev.muthukumar.anjana_crm.ui.admin

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
import dev.muthukumar.anjana_crm.ui.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCertificateScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var selectedAllocId by remember { mutableStateOf<Long?>(null) }
    var grade  by remember { mutableStateOf("") }
    var issued by remember { mutableStateOf(false) }

    val eligible = state.allocations.filter {
        it.allocationStatus == "COMPLETED" &&
                state.certificates.none { c -> c.studentName == it.student?.name }
    }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("Certificates", onBack = { navController.popBackStack() }) },
        bottomBar = { AdminBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                SectionCard("Issue Certificate") {
                    if (eligible.isEmpty()) {
                        Text("No completed allocations pending certificate",
                            color = Color(0xFF64748B), fontSize = 13.sp)
                    } else {
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedAllocId?.let { id ->
                                    eligible.find { it.id == id }
                                        ?.let { "${it.student?.name} — ${it.category}" }
                                } ?: "Select allocation",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Allocation") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            // ── FIX: removed containerColor param (not in Material3 1.2.x) ──
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                eligible.forEach { alloc ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "${alloc.student?.name} — ${alloc.category}",
                                                fontSize = 13.sp
                                            )
                                        },
                                        onClick = {
                                            selectedAllocId = alloc.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = grade,
                            onValueChange = { grade = it },
                            label = { Text("Grade (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("A, B, Distinction…") }
                        )

                        if (issued) {
                            Spacer(Modifier.height(6.dp))
                            Text("✓ Certificate issued!", color = Color(0xFF10B981), fontSize = 13.sp)
                        }

                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                selectedAllocId?.let {
                                    vm.issueCertificate(it, grade.ifBlank { null })
                                    issued = true
                                    grade = ""
                                    selectedAllocId = null
                                }
                            },
                            enabled = selectedAllocId != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) { Text("Issue Certificate", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }

            item {
                Text("Issued (${state.certificates.size})", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
            }

            items(state.certificates) { cert ->
                Card(
                    shape  = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(cert.studentName, color = Color.White,
                                fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(cert.programTitle ?: "—", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text(cert.issuedDate, color = Color(0xFF64748B), fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            cert.grade?.let { StatusBadge(it) }
                            Text(cert.certificateNumber, fontSize = 10.sp, color = Color(0xFF60A5FA))
                        }
                    }
                }
            }
        }
    }
}
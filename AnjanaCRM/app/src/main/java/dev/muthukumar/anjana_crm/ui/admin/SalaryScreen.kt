package dev.muthukumar.anjana_crm.ui.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.data.model.Salary
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import java.util.Calendar

private val MONTHS = listOf(
    "01" to "January", "02" to "February", "03" to "March",
    "04" to "April",   "05" to "May",      "06" to "June",
    "07" to "July",    "08" to "August",   "09" to "September",
    "10" to "October", "11" to "November", "12" to "December"
)
private val YEARS = (0..4).map { (Calendar.getInstance().get(Calendar.YEAR) - it).toString() }
private val PAY_MODES = listOf("BANK_TRANSFER","CASH","CHEQUE","UPI")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSalaryScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val now   = Calendar.getInstance()

    var showDialog  by remember { mutableStateOf(false) }
    var filterEmpId by remember { mutableStateOf("") }

    // New salary form state
    var empId        by remember { mutableStateOf("") }
    var selYear      by remember { mutableStateOf(now.get(Calendar.YEAR).toString()) }
    var selMonth     by remember { mutableStateOf((now.get(Calendar.MONTH)+1).toString().padStart(2,'0')) }
    var basic        by remember { mutableStateOf("") }
    var hra          by remember { mutableStateOf("") }
    var transport    by remember { mutableStateOf("") }
    var otherAllow   by remember { mutableStateOf("") }
    var bonus        by remember { mutableStateOf("") }
    var pfDed        by remember { mutableStateOf("") }
    var taxDed       by remember { mutableStateOf("") }
    var otherDed     by remember { mutableStateOf("") }
    var payMode      by remember { mutableStateOf("BANK_TRANSFER") }
    var txnRef       by remember { mutableStateOf("") }
    var notes        by remember { mutableStateOf("") }

    val payMonth = "$selYear-$selMonth"

    // Live totals
    val gross       = listOf(basic,hra,transport,otherAllow,bonus).sumOf { it.toDoubleOrNull() ?: 0.0 }
    val deductions  = listOf(pfDed,taxDed,otherDed).sumOf { it.toDoubleOrNull() ?: 0.0 }
    val net         = gross - deductions

    val filtered = if (filterEmpId.isBlank()) state.salaries
    else state.salaries.filter { it.employee?.id.toString() == filterEmpId }

    val totalPaid    = state.salaries.filter { it.status == "PAID"    }.sumOf { it.netSalary ?: 0.0 }
    val totalPending = state.salaries.filter { it.status == "PENDING" }.sumOf { it.netSalary ?: 0.0 }

    Scaffold(
        containerColor = PageBg,
        topBar    = { CrmTopBar("Salary & Payroll", onBack = { navController.popBackStack() }) },
        bottomBar = { AdminBottomNav(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = BrandMagenta,
                contentColor   = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, "Generate Salary") }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // ── Summary ────────────────────────────────────
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SalaryChip("Paid",    "₹${"%.0f".format(totalPaid)}",    Color(0xFF10B981), Modifier.weight(1f))
                SalaryChip("Pending", "₹${"%.0f".format(totalPending)}", Color(0xFFF59E0B), Modifier.weight(1f))
                SalaryChip("Records", "${state.salaries.size}",           Color(0xFF38BDF8), Modifier.weight(1f))
            }

            // ── Employee filter ────────────────────────────
            if (state.users.isNotEmpty()) {
                var empExpanded by remember { mutableStateOf(false) }
                val selEmpName = state.users.find { it.id.toString() == filterEmpId }?.name ?: "All Employees"
                ExposedDropdownMenuBox(
                    expanded = empExpanded,
                    onExpandedChange = { empExpanded = it },
                    modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selEmpName, onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(empExpanded) },
                        label = { Text("Filter by Employee") },
                        shape  = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = BrandMagenta,
                            unfocusedBorderColor    = Outline,
                            focusedTextColor        = OnSurface,
                            unfocusedTextColor      = OnSurface,
                            focusedContainerColor   = White,
                            unfocusedContainerColor = Color(0xFFFAF7FB),
                            focusedLabelColor       = BrandMagenta,
                            unfocusedLabelColor     = OnSurfaceMuted
                        )
                    )
                    ExposedDropdownMenu(expanded = empExpanded, onDismissRequest = { empExpanded = false }) {
                        DropdownMenuItem(text = { Text("All Employees", color = Color.White) }, onClick = { filterEmpId = ""; empExpanded = false })
                        state.users.forEach { u ->
                            DropdownMenuItem(text = { Text(u.name, color = Color.White) }, onClick = { filterEmpId = u.id.toString(); empExpanded = false })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── List ───────────────────────────────────────
            if (state.loading) LoadingView()
            else LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
            ) {
                item { Text("${filtered.size} records", fontSize = 12.sp, color = Color(0xFF64748B)) }
                items(filtered) { sal -> SalaryCard(sal, vm) }
            }
        }
    }

    // ── Generate Salary Dialog ─────────────────────────────────────────────────
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Generate Salary", fontWeight = FontWeight.Bold,
                        color = OnSurface, fontSize = 16.sp)
                    Spacer(Modifier.height(14.dp))

                    // Employee
                    var empExp by remember { mutableStateOf(false) }
                    SalDlgLabel("Employee *")
                    ExposedDropdownMenuBox(expanded = empExp, onExpandedChange = { empExp = it }) {
                        OutlinedTextField(
                            value = state.users.find { it.id.toString() == empId }?.name ?: "Select Employee",
                            onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(empExp) },
                            shape = RoundedCornerShape(10.dp), colors = salDlgColors()
                        )
                        ExposedDropdownMenu(expanded = empExp, onDismissRequest = { empExp = false }) {
                            state.users.forEach { u ->
                                DropdownMenuItem(text = { Text("${u.name} — ${u.role}", color = OnSurface) }, onClick = { empId = u.id.toString(); empExp = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    // Pay Month
                    SalDlgLabel("Pay Month * → $payMonth")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var yearExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = yearExp, onExpandedChange = { yearExp = it }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selYear, onValueChange = {}, readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(), label = { Text("Year") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(yearExp) },
                                shape = RoundedCornerShape(10.dp), colors = salDlgColors()
                            )
                            ExposedDropdownMenu(expanded = yearExp, onDismissRequest = { yearExp = false }) {
                                YEARS.forEach { y -> DropdownMenuItem(text = { Text(y, color = OnSurface) }, onClick = { selYear = y; yearExp = false }) }
                            }
                        }
                        var monExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = monExp, onExpandedChange = { monExp = it }, modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = MONTHS.find { it.first == selMonth }?.second ?: selMonth,
                                onValueChange = {}, readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(), label = { Text("Month") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(monExp) },
                                shape = RoundedCornerShape(10.dp), colors = salDlgColors()
                            )
                            ExposedDropdownMenu(expanded = monExp, onDismissRequest = { monExp = false }) {
                                MONTHS.forEach { (v, l) -> DropdownMenuItem(text = { Text(l, color = OnSurface) }, onClick = { selMonth = v; monExp = false }) }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Divider(color = Color(0x3310B981))
                    SalDlgLabel("── Earnings ──────────────────")

                    SalDlgField("Basic Salary *", basic) { basic = it }
                    SalDlgField("HRA", hra) { hra = it }
                    SalDlgField("Transport Allowance", transport) { transport = it }
                    SalDlgField("Other Allowance", otherAllow) { otherAllow = it }
                    SalDlgField("Bonus", bonus) { bonus = it }

                    Spacer(Modifier.height(8.dp))
                    Divider(color = Color(0x33EF4444))
                    SalDlgLabel("── Deductions ─────────────────")

                    SalDlgField("PF Deduction", pfDed) { pfDed = it }
                    SalDlgField("Tax (TDS)", taxDed) { taxDed = it }
                    SalDlgField("Other Deduction", otherDed) { otherDed = it }

                    // Live net pay
                    Spacer(Modifier.height(10.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                        Row(Modifier.padding(14.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            NetItem("Gross",      "₹${"%.0f".format(gross)}",      OnSurfaceMuted)
                            NetItem("Deductions", "₹${"%.0f".format(deductions)}", ErrorRed)
                            NetItem("Net Pay",    "₹${"%.0f".format(net)}",        BrandMagenta)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Payment mode
                    var modeExp by remember { mutableStateOf(false) }
                    SalDlgLabel("Payment Mode")
                    ExposedDropdownMenuBox(expanded = modeExp, onExpandedChange = { modeExp = it }) {
                        OutlinedTextField(
                            value = payMode, onValueChange = {}, readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(modeExp) },
                            shape = RoundedCornerShape(10.dp), colors = salDlgColors()
                        )
                        ExposedDropdownMenu(expanded = modeExp, onDismissRequest = { modeExp = false }) {
                            PAY_MODES.forEach { m -> DropdownMenuItem(text = { Text(m, color = OnSurface) }, onClick = { payMode = m; modeExp = false }) }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SalDlgLabel("Transaction Ref")
                    OutlinedTextField(value = txnRef, onValueChange = { txnRef = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Optional") }, shape = RoundedCornerShape(10.dp), colors = salDlgColors())
                    Spacer(Modifier.height(8.dp))
                    SalDlgLabel("Notes")
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Optional") }, shape = RoundedCornerShape(10.dp), colors = salDlgColors())

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showDialog = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                            Text("Cancel", color = Color(0xFF94A3B8))
                        }
                        Button(
                            onClick = {
                                if (empId.isBlank() || basic.toDoubleOrNull() == null) return@Button
                                vm.generateSalary(
                                    employeeId = empId.toLong(),
                                    payMonth   = payMonth,
                                    basicSalary = basic.toDouble(),
                                    hra = hra.toDoubleOrNull() ?: 0.0,
                                    transportAllowance = transport.toDoubleOrNull() ?: 0.0,
                                    otherAllowance = otherAllow.toDoubleOrNull() ?: 0.0,
                                    bonus = bonus.toDoubleOrNull() ?: 0.0,
                                    pfDeduction = pfDed.toDoubleOrNull() ?: 0.0,
                                    taxDeduction = taxDed.toDoubleOrNull() ?: 0.0,
                                    otherDeduction = otherDed.toDoubleOrNull() ?: 0.0,
                                    paymentMode = payMode,
                                    transactionRef = txnRef.ifBlank { null },
                                    notes = notes.ifBlank { null }
                                )
                                showDialog = false
                                empId=""; basic=""; hra=""; transport=""; otherAllow=""; bonus=""; pfDed=""; taxDed=""; otherDed=""; txnRef=""; notes=""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta),
                            enabled = empId.isNotBlank() && (basic.toDoubleOrNull() ?: 0.0) > 0
                        ) { Text("Generate", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalaryCard(sal: Salary, vm: AdminViewModel) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(sal.employee?.name ?: "—", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 14.sp)
                    Text("${sal.payMonth} • ${sal.employee?.position ?: sal.employee?.role ?: ""}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
                StatusBadge(sal.status ?: "PENDING")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AmtItem("Basic",  "₹${"%.0f".format(sal.basicSalary ?: 0.0)}", Color(0xFF94A3B8))
                AmtItem("Net Pay","₹${"%.0f".format(sal.netSalary ?: 0.0)}",   Color(0xFF38BDF8))
                AmtItem("Mode",   sal.paymentMode ?: "—",                       Color(0xFF64748B))
            }
            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider(color = Color(0x1AFFFFFF))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AmtItem("Gross",       "₹${"%.0f".format(sal.grossSalary ?: 0.0)}", Color(0xFF10B981))
                    AmtItem("PF",          "₹${"%.0f".format(sal.pfDeduction ?: 0.0)}", Color(0xFFEF4444))
                    AmtItem("Tax",         "₹${"%.0f".format(sal.taxDeduction ?: 0.0)}", Color(0xFFEF4444))
                }
                if (sal.status == "PENDING") {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.markSalaryPaid(sal.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) { Text("💳 Mark as Paid", fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable private fun SalaryChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 13.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun AmtItem(label: String, value: String, color: Color) {
    Column {
        Text(label, fontSize = 10.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun NetItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = OnSurfaceMuted)
        Text(value, fontSize = 14.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable private fun SalDlgLabel(text: String) {
    Text(text, fontSize = 11.sp, color = OnSurfaceMuted, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
}

@Composable private fun SalDlgField(label: String, value: String, onValue: (String) -> Unit) {
    SalDlgLabel(label)
    OutlinedTextField(
        value = value, onValueChange = { onValue(it.filter { c -> c.isDigit() || c == '.' }) },
        modifier = Modifier.fillMaxWidth(), placeholder = { Text("0") },
        shape = RoundedCornerShape(10.dp), colors = salDlgColors()
    )
}

@Composable private fun salDlgColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor        = BrandMagenta,
    unfocusedBorderColor      = Outline,
    focusedTextColor          = OnSurface,
    unfocusedTextColor        = OnSurface,
    cursorColor               = BrandMagenta,
    focusedContainerColor     = White,
    unfocusedContainerColor   = Color(0xFFFAF7FB),
    focusedPlaceholderColor   = OnSurfaceHint,
    unfocusedPlaceholderColor = OnSurfaceHint,
    focusedLabelColor         = BrandMagenta,
    unfocusedLabelColor       = OnSurfaceMuted
)
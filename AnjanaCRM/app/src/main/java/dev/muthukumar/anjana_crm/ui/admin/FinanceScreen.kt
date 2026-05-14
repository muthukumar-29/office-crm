package dev.muthukumar.anjana_crm.ui.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import java.util.Calendar

private val INC_CATS  = listOf("Student Fee","Workshop","Consultation","Office Project","Other Income")
private val EXP_CATS  = listOf("Salary","Rent","Utilities","Equipment","Marketing","Travel","Miscellaneous")
private val PAY_MODES = listOf("CASH","UPI","BANK_TRANSFER","CHEQUE","ONLINE")

private fun todayStr(): String {
    val c = Calendar.getInstance()
    return "${c.get(Calendar.YEAR)}-${(c.get(Calendar.MONTH)+1).toString().padStart(2,'0')}-${c.get(Calendar.DAY_OF_MONTH).toString().padStart(2,'0')}"
}

private fun monthStartStr(): String {
    val c = Calendar.getInstance()
    return "${c.get(Calendar.YEAR)}-${(c.get(Calendar.MONTH)+1).toString().padStart(2,'0')}-01"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFinanceScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state   by vm.state.collectAsState()
    val context = LocalContext.current

    var showDialog  by remember { mutableStateOf(false) }
    var txnType     by remember { mutableStateOf("EXPENSE") }
    var amount      by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payMode     by remember { mutableStateOf("CASH") }
    var txnDate     by remember { mutableStateOf(todayStr()) }
    var refNo       by remember { mutableStateOf("") }

    var filterStart by remember { mutableStateOf(monthStartStr()) }
    var filterEnd   by remember { mutableStateOf(todayStr()) }

    fun pickDate(current: String, onPick: (String) -> Unit) {
        val parts = current.split("-")
        val y = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
        val m = (parts.getOrNull(1)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.MONTH)+1) - 1
        val d = parts.getOrNull(2)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _, year, month, day ->
            onPick("$year-${(month+1).toString().padStart(2,'0')}-${day.toString().padStart(2,'0')}")
        }, y, m, d).show()
    }

    Scaffold(
        containerColor = PageBg,
        topBar   = { CrmTopBar("Finance", onBack = { navController.popBackStack() }) },
        bottomBar = { AdminBottomNav(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon    = { Icon(Icons.Default.Add, null) },
                text    = { Text("Add Transaction") },
                containerColor = BrandMagenta,
                contentColor   = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // ── Summary ────────────────────────────────────
            state.financeSummary?.let { s ->
                Row(
                    Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryChip("Income",  "₹${"%.0f".format(s.totalIncome)}",  Color(0xFF10B981), Modifier.weight(1f))
                    SummaryChip("Expense", "₹${"%.0f".format(s.totalExpense)}", Color(0xFFEF4444), Modifier.weight(1f))
                    SummaryChip("Net",     "₹${"%.0f".format(s.netBalance)}",
                        if (s.netBalance >= 0) Color(0xFF38BDF8) else Color(0xFFF59E0B), Modifier.weight(1f))
                }
            }

            // ── Date filter ────────────────────────────────
            Card(
                modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Row(
                    Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.DateRange, null, tint = BrandMagenta, modifier = Modifier.size(16.dp))
                    // Start date
                    OutlinedButton(
                        onClick = { pickDate(filterStart) { filterStart = it } },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) { Text(filterStart, fontSize = 11.sp, color = Color.White) }
                    Text("→", color = Color(0xFF64748B))
                    // End date
                    OutlinedButton(
                        onClick = { pickDate(filterEnd) { filterEnd = it } },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) { Text(filterEnd, fontSize = 11.sp, color = Color.White) }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { vm.loadFinance(filterStart, filterEnd) },
                        shape   = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        colors  = ButtonDefaults.buttonColors(containerColor = BrandMagenta),
                        modifier = Modifier.height(32.dp)
                    ) { Text("Go", fontSize = 12.sp) }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── List ───────────────────────────────────────
            if (state.loading) {
                LoadingView()
            } else if (state.transactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No transactions found", color = Color(0xFF64748B), fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
                ) {
                    item {
                        Text("${state.transactions.size} transactions",
                            fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                    items(state.transactions) { txn ->
                        Card(
                            shape  = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Row(
                                Modifier.padding(14.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(txn.category, fontWeight = FontWeight.SemiBold,
                                        color = Color.White, fontSize = 14.sp)
                                    txn.description?.let { Text(it, color = Color(0xFF94A3B8), fontSize = 12.sp) }
                                    Text(txn.transactionDate, color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "${if (txn.type=="INCOME") "+" else "-"}₹${"%.0f".format(txn.amount)}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (txn.type=="INCOME") Color(0xFF10B981) else Color(0xFFEF4444),
                                        fontSize = 15.sp
                                    )
                                    StatusBadge(txn.type)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add Transaction Dialog ─────────────────────────────────────────────────
    if (showDialog) {
        Dialog(onDismissRequest = {
            showDialog = false
            amount = ""; category = ""; description = ""; refNo = ""
            txnDate = todayStr(); txnType = "EXPENSE"; payMode = "CASH"
        }) {
            Card(
                shape  = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Add Transaction", fontWeight = FontWeight.Bold,
                        color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.height(14.dp))

                    // Type toggle
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("INCOME","EXPENSE").forEach { t ->
                            val sel = txnType == t
                            Button(
                                onClick  = { txnType = t; category = "" },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        sel && t=="INCOME"  -> Color(0xFF10B981)
                                        sel && t=="EXPENSE" -> Color(0xFFEF4444)
                                        else -> Color(0xFF1E293B)
                                    }
                                )
                            ) { Text(if (t=="INCOME") "⬆ Income" else "⬇ Expense", fontSize = 12.sp) }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Amount
                    DlgLabel("Amount (₹) *")
                    OutlinedTextField(
                        value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.00") },
                        shape = RoundedCornerShape(10.dp), colors = dlgColors()
                    )
                    Spacer(Modifier.height(10.dp))

                    // Category
                    var catExp by remember { mutableStateOf(false) }
                    DlgLabel("Category *")
                    ExposedDropdownMenuBox(expanded = catExp, onExpandedChange = { catExp = it }) {
                        OutlinedTextField(
                            value = category.ifBlank { "Select…" }, onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExp) },
                            shape = RoundedCornerShape(10.dp), colors = dlgColors()
                        )
                        ExposedDropdownMenu(expanded = catExp, onDismissRequest = { catExp = false }) {
                            (if (txnType=="INCOME") INC_CATS else EXP_CATS).forEach { c ->
                                DropdownMenuItem(text = { Text(c, color = Color.White) }, onClick = { category = c; catExp = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    // Date — tappable field opens DatePickerDialog
                    DlgLabel("Date *")
                    OutlinedTextField(
                        value = txnDate, onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { pickDate(txnDate) { txnDate = it } },
                        enabled  = false,
                        trailingIcon = {
                            IconButton(onClick = { pickDate(txnDate) { txnDate = it } }) {
                                Icon(Icons.Default.DateRange, null, tint = BrandMagenta)
                            }
                        },
                        shape  = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor   = Color(0xFF334155),
                            disabledTextColor     = Color.White,
                            disabledContainerColor = Color(0xFF1E293B)
                        )
                    )
                    Spacer(Modifier.height(10.dp))

                    // Payment mode
                    var modeExp by remember { mutableStateOf(false) }
                    DlgLabel("Payment Mode")
                    ExposedDropdownMenuBox(expanded = modeExp, onExpandedChange = { modeExp = it }) {
                        OutlinedTextField(
                            value = payMode, onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(modeExp) },
                            shape = RoundedCornerShape(10.dp), colors = dlgColors()
                        )
                        ExposedDropdownMenu(expanded = modeExp, onDismissRequest = { modeExp = false }) {
                            PAY_MODES.forEach { m ->
                                DropdownMenuItem(text = { Text(m, color = Color.White) }, onClick = { payMode = m; modeExp = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    DlgLabel("Description (optional)")
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("Optional") },
                        shape = RoundedCornerShape(10.dp), colors = dlgColors()
                    )
                    Spacer(Modifier.height(10.dp))

                    DlgLabel("Reference No (optional)")
                    OutlinedTextField(
                        value = refNo, onValueChange = { refNo = it },
                        modifier = Modifier.fillMaxWidth(), placeholder = { Text("Optional") },
                        shape = RoundedCornerShape(10.dp), colors = dlgColors()
                    )

                    Spacer(Modifier.height(20.dp))

                    // Buttons
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick  = { showDialog = false },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(10.dp)
                        ) { Text("Cancel", color = Color(0xFF94A3B8)) }

                        Button(
                            onClick = {
                                val amt = amount.toDoubleOrNull() ?: return@Button
                                if (category.isBlank()) return@Button
                                vm.recordTransaction(
                                    type = txnType, amount = amt, category = category,
                                    description = description.ifBlank { null },
                                    paymentMode = payMode, transactionDate = txnDate,
                                    referenceNo = refNo.ifBlank { null }, notes = null
                                )
                                showDialog = false
                                amount = ""; category = ""; description = ""; refNo = ""
                                txnDate = todayStr(); txnType = "EXPENSE"; payMode = "CASH"
                            },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = BrandMagenta),
                            enabled  = amount.toDoubleOrNull() != null && category.isNotBlank()
                        ) { Text("Save", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

// ── Small helpers ─────────────────────────────────────────────────────────────

@Composable
private fun DlgLabel(text: String) {
    Text(text, fontSize = 11.sp, color = Color(0xFF94A3B8),
        fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun dlgColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = BrandMagenta,
    unfocusedBorderColor    = Color(0xFF334155),
    focusedTextColor        = Color.White,
    unfocusedTextColor      = Color.White,
    cursorColor             = BrandMagenta,
    focusedContainerColor   = Color(0xFF1E293B),
    unfocusedContainerColor = Color(0xFF1E293B),
    focusedPlaceholderColor = Color(0xFF475569),
    unfocusedPlaceholderColor = Color(0xFF475569),
)

@Composable
private fun SummaryChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark), modifier = modifier) {
        Column(Modifier.padding(10.dp)) {
            Text(label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            Text(value,  fontSize = 14.sp, color = color,              fontWeight = FontWeight.Bold)
        }
    }
}
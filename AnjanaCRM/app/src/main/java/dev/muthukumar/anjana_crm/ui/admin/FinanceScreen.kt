package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.data.model.FinanceTransaction
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminFinanceScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state        by vm.state.collectAsState()
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val userName     by vm.userName.collectAsState(initial = "")
    val role         by vm.role.collectAsState(initial = "")
    val summary       = state.financeSummary
    val currentRoute  = navController.currentBackStackEntry?.destination?.route
    val context       = LocalContext.current

    // Sheet & dialog states
    var showAddSheet    by remember { mutableStateOf(false) }
    var editTarget      by remember { mutableStateOf<FinanceTransaction?>(null) }
    var deleteTarget    by remember { mutableStateOf<FinanceTransaction?>(null) }
    var showExportSheet by remember { mutableStateOf(false) }
    var markPaidInvId  by remember { mutableStateOf<Long?>(null) }

    // Filters
    var filterType      by remember { mutableStateOf("ALL") }
    var filterDateFrom  by remember { mutableStateOf("") }
    var filterDateTo    by remember { mutableStateOf("") }
    var showDateFilter  by remember { mutableStateOf(false) }

    // Filtered transactions
    val filtered = state.transactions
        .let { list ->
            when (filterType) {
                "INCOME"  -> list.filter { it.type == "INCOME" }
                "EXPENSE" -> list.filter { it.type == "EXPENSE" }
                else      -> list
            }
        }
        .let { list ->
            if (filterDateFrom.isNotBlank())
                list.filter { it.transactionDate >= filterDateFrom }
            else list
        }
        .let { list ->
            if (filterDateTo.isNotBlank())
                list.filter { it.transactionDate <= filterDateTo }
            else list
        }

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
            topBar = {
                CrmTopBar(
                    title = "Finance",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    actions = {
                        // Export button
                        TextButton(onClick = { showExportSheet = true }) {
                            Text("Export", color = BrandMagenta, fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    SmallFloatingActionButton(
                        onClick = { showAddSheet = true },
                        containerColor = BrandMagenta,
                        contentColor = White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp, start = 0.dp, end = 0.dp)
            ) {

                // ── Hero balance card ──────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandMagenta),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Net Balance", fontSize = 13.sp, color = White.copy(alpha = 0.8f))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "₹${"%,.0f".format(summary?.netBalance ?: 0.0)}",
                                fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = White
                            )
                            Spacer(Modifier.height(20.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("↑ Income", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                    Text(
                                        "₹${"%,.0f".format(summary?.totalIncome ?: 0.0)}",
                                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFB9F6CA)
                                    )
                                }
                                Box(Modifier.width(1.dp).height(36.dp).background(White.copy(alpha = 0.3f)))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("↓ Expense", fontSize = 11.sp, color = White.copy(alpha = 0.7f))
                                    Text(
                                        "₹${"%,.0f".format(summary?.totalExpense ?: 0.0)}",
                                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFFCDD2)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Salary + Invoice mini summary ──────────────────
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            SectionCard("Salary") {
                                val paid    = state.salaries.count { it.status == "PAID" }
                                val pending = state.salaries.count { it.status == "PENDING" }
                                Text("${state.salaries.size} records", fontSize = 11.sp, color = OnSurfaceMuted)
                                Text("Paid: $paid  Pending: $pending", fontSize = 11.sp, color = OnSurfaceHint)
                                Text("₹${"%,.0f".format(state.salaries.sumOf { it.netSalary })}",
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandMagenta)
                            }
                        }
                        Box(Modifier.weight(1f)) {
                            SectionCard("Invoices") {
                                val unpaidInvoices = state.invoices.filter { it.status != "PAID" }
                                Text("${state.invoices.size} total", fontSize = 11.sp, color = OnSurfaceMuted)
                                Text("Unpaid: ${unpaidInvoices.size}", fontSize = 11.sp,
                                    color = if (unpaidInvoices.isNotEmpty()) WarningAmber else SuccessGreen)
                                Text("₹${"%,.0f".format(unpaidInvoices.sumOf { it.totalAmount })} due",
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                    color = if (unpaidInvoices.isNotEmpty()) WarningAmber else SuccessGreen)
                            }
                        }
                    }
                }

                // ── Unpaid invoices — mark as paid ─────────────────
                val unpaidInvoices = state.invoices.filter { it.status != "PAID" }
                if (unpaidInvoices.isNotEmpty()) {
                    item {
                        SectionCard("Pending Invoices") {
                            unpaidInvoices.forEach { inv ->
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(inv.clientName, fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium, color = OnSurface)
                                        Text("${inv.invoiceNumber} · ${inv.invoiceDate}",
                                            fontSize = 11.sp, color = OnSurfaceHint)
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("₹${"%,.0f".format(inv.totalAmount)}",
                                            fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                            color = WarningAmber)
                                        Button(
                                            onClick = { markPaidInvId = inv.id },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Mark Paid", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                                if (inv != unpaidInvoices.last()) Divider(color = Outline)
                            }
                        }
                    }
                }

                // ── Filters row ────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Transactions", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, color = OnSurface,
                                modifier = Modifier.weight(1f))
                            Text("${filtered.size}", fontSize = 12.sp, color = OnSurfaceMuted)
                        }

                        // Type filter chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("ALL", "INCOME", "EXPENSE").forEach { type ->
                                FilterChip(
                                    selected = filterType == type,
                                    onClick  = { filterType = type },
                                    label    = {
                                        Text(
                                            when (type) {
                                                "INCOME"  -> "↑ Income"
                                                "EXPENSE" -> "↓ Expense"
                                                else      -> "All"
                                            },
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (type) {
                                            "INCOME"  -> SuccessGreen
                                            "EXPENSE" -> ErrorRed
                                            else      -> BrandMagenta
                                        },
                                        selectedLabelColor = White,
                                        containerColor     = SurfaceVariant,
                                        labelColor         = OnSurfaceMuted
                                    )
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            // Date filter toggle
                            FilterChip(
                                selected = filterDateFrom.isNotBlank() || filterDateTo.isNotBlank(),
                                onClick  = { showDateFilter = !showDateFilter },
                                label    = { Text("📅 Date", fontSize = 12.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPurple,
                                    selectedLabelColor     = White,
                                    containerColor         = SurfaceVariant,
                                    labelColor             = OnSurfaceMuted
                                )
                            )
                        }

                        // Date range inputs (shown when filter is open)
                        if (showDateFilter) {
                            Card(
                                shape  = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Date Range", fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold, color = OnSurfaceMuted)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = filterDateFrom,
                                            onValueChange = { filterDateFrom = it },
                                            label = { Text("From") },
                                            placeholder = { Text("YYYY-MM-DD") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = dateFieldColors(),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = filterDateTo,
                                            onValueChange = { filterDateTo = it },
                                            label = { Text("To") },
                                            placeholder = { Text("YYYY-MM-DD") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = dateFieldColors(),
                                            singleLine = true
                                        )
                                    }
                                    if (filterDateFrom.isNotBlank() || filterDateTo.isNotBlank()) {
                                        TextButton(
                                            onClick = {
                                                filterDateFrom = ""; filterDateTo = ""
                                                showDateFilter = false
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Clear filter", color = ErrorRed, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Empty state ────────────────────────────────────
                if (filtered.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(top = 32.dp),
                            contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📊", fontSize = 40.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("No transactions found", color = OnSurfaceMuted, fontSize = 14.sp)
                                Text("Tap + to add a transaction",
                                    color = OnSurfaceHint, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // ── Transaction list ───────────────────────────────
                items(filtered, key = { it.id }) { txn ->
                    TransactionCard(
                        txn = txn,
                        onEdit   = { editTarget = txn },
                        onDelete = { deleteTarget = txn }
                    )
                }
            }
        }
    }

    // ── Add transaction sheet ──────────────────────────────────
    if (showAddSheet) {
        TransactionSheet(
            title        = "Add Transaction",
            initial      = null,
            onDismiss    = { showAddSheet = false },
            onSubmit     = { body -> vm.createTransaction(body); showAddSheet = false }
        )
    }

    // ── Edit transaction sheet ─────────────────────────────────
    editTarget?.let { txn ->
        TransactionSheet(
            title     = "Edit Transaction",
            initial   = txn,
            onDismiss = { editTarget = null },
            onSubmit  = { body -> vm.updateTransaction(txn.id, body); editTarget = null }
        )
    }

    // ── Delete confirm dialog ──────────────────────────────────
    deleteTarget?.let { txn ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = White,
            title = {
                Text("Delete Transaction", fontWeight = FontWeight.Bold,
                    color = OnSurface, fontSize = 16.sp)
            },
            text = {
                Text(
                    "Delete \"${txn.category}\" " +
                            "(${if (txn.type == "INCOME") "+" else "-"}₹${"%,.0f".format(txn.amount)})?",
                    color = OnSurfaceMuted, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { vm.deleteTransaction(txn.id); deleteTarget = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Delete", fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { deleteTarget = null },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
                ) { Text("Cancel", color = OnSurfaceMuted) }
            }
        )
    }

    // ── Mark invoice paid confirm ──────────────────────────────
    markPaidInvId?.let { invId ->
        val inv = state.invoices.find { it.id == invId }
        AlertDialog(
            onDismissRequest = { markPaidInvId = null },
            containerColor = White,
            title = { Text("Mark Invoice Paid", fontWeight = FontWeight.Bold, color = OnSurface) },
            text = {
                Text(
                    "Mark \"${inv?.clientName}\" invoice of " +
                            "₹${"%,.0f".format(inv?.totalAmount ?: 0.0)} as paid?",
                    color = OnSurfaceMuted, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { vm.markInvoicePaid(invId); markPaidInvId = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Mark Paid", fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                OutlinedButton(onClick = { markPaidInvId = null },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
                ) { Text("Cancel", color = OnSurfaceMuted) }
            }
        )
    }

    // ── Export summary sheet ───────────────────────────────────
    if (showExportSheet) {
        ExportSummarySheet(
            summary      = summary,
            transactions = filtered,
            filterType   = filterType,
            dateFrom     = filterDateFrom,
            dateTo       = filterDateTo,
            onDismiss    = { showExportSheet = false }
        )
    }
}

// ── Individual transaction card ───────────────────────────────
@Composable
fun TransactionCard(
    txn: FinanceTransaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = txn.type == "INCOME"
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type icon
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(if (isIncome) Color(0xFFDCF5E8) else Color(0xFFFFE8E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isIncome) "↑" else "↓",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = if (isIncome) SuccessGreen else ErrorRed
                )
            }
            // Details
            Column(Modifier.weight(1f)) {
                Text(txn.category, fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = OnSurface)
                txn.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, fontSize = 11.sp, color = OnSurfaceMuted)
                }
                Text(txn.transactionDate, fontSize = 11.sp, color = OnSurfaceHint)
            }
            // Amount + actions
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (isIncome) "+" else "-"}₹${"%,.0f".format(txn.amount)}",
                    fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = if (isIncome) SuccessGreen else ErrorRed
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, null,
                            tint = BrandMagenta.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null,
                            tint = ErrorRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ── Add / Edit transaction sheet ──────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionSheet(
    title: String,
    initial: FinanceTransaction?,
    onDismiss: () -> Unit,
    onSubmit: (Map<String, Any>) -> Unit
) {
    var type        by remember { mutableStateOf(initial?.type ?: "INCOME") }
    var category    by remember { mutableStateOf(initial?.category ?: "") }
    var amount      by remember { mutableStateOf(initial?.amount?.toString() ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var date        by remember { mutableStateOf(
        initial?.transactionDate ?: java.time.LocalDate.now().toString()
    ) }

    val incomeCategories  = listOf("Course Fee", "Internship Fee", "Project Fee",
        "Certification Fee", "Consulting", "Other Income")
    val expenseCategories = listOf("Salary", "Rent", "Utilities", "Marketing",
        "Equipment", "Travel", "Maintenance", "Other Expense")
    val quickCategories   = if (type == "INCOME") incomeCategories else expenseCategories

    val accentColor = if (type == "INCOME") SuccessGreen else ErrorRed

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)

            // Income / Expense toggle
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("INCOME", "EXPENSE").forEach { t ->
                    val selected = type == t
                    Button(
                        onClick  = { type = t; if (initial == null) category = "" },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (selected) (if (t == "INCOME") SuccessGreen else ErrorRed)
                            else SurfaceVariant,
                            contentColor   = if (selected) White else OnSurfaceMuted
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(if (t == "INCOME") "↑ Income" else "↓ Expense",
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp)
                    }
                }
            }

            // Quick category chips
            Text("Category", fontSize = 12.sp, color = OnSurfaceMuted, fontWeight = FontWeight.Medium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                quickCategories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick  = { category = cat },
                        label    = { Text(cat, fontSize = 11.sp) },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor     = White,
                            containerColor         = SurfaceVariant,
                            labelColor             = OnSurfaceMuted
                        )
                    )
                }
            }

            // Custom category override
            OutlinedTextField(
                value = category, onValueChange = { category = it },
                label = { Text("Custom category") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = txFieldColors(accentColor), singleLine = true
            )

            // Amount + Date in one row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it },
                    label = { Text("Amount ₹ *") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = txFieldColors(accentColor), singleLine = true,
                    prefix = { Text("₹ ", color = OnSurfaceMuted) }
                )
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = txFieldColors(accentColor), singleLine = true
                )
            }

            // Description
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = txFieldColors(accentColor), maxLines = 2
            )

            // Live preview
            amount.toDoubleOrNull()?.let { amt ->
                Surface(
                    color = if (type == "INCOME") Color(0xFFDCF5E8) else Color(0xFFFFE8E8),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(category.ifBlank { "—" }, fontSize = 13.sp,
                                fontWeight = FontWeight.Medium, color = OnSurface)
                            Text(date, fontSize = 11.sp, color = OnSurfaceMuted)
                            description.takeIf { it.isNotBlank() }?.let {
                                Text(it, fontSize = 11.sp, color = OnSurfaceHint)
                            }
                        }
                        Text(
                            "${if (type == "INCOME") "+" else "-"}₹${"%,.2f".format(amt)}",
                            fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = accentColor
                        )
                    }
                }
            }

            // Submit
            Button(
                onClick = {
                    onSubmit(buildMap {
                        put("type",            type)
                        put("category",        category)
                        put("amount",          amount.toDoubleOrNull() ?: 0.0)
                        put("transactionDate", date)
                        put("description",     description)
                    })
                },
                enabled = category.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    if (initial == null) "Add Transaction" else "Save Changes",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            }
        }
    }
}

// ── Export Summary Sheet ──────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSummarySheet(
    summary: dev.muthukumar.anjana_crm.data.model.FinanceSummary?,
    transactions: List<FinanceTransaction>,
    filterType: String,
    dateFrom: String,
    dateTo: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val income  = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val expense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val net     = income - expense

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Export Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)

            // Summary preview card
            Card(shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandMagentaLight),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Anjana Infotech — Finance Summary", fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, color = BrandPurple)
                    if (dateFrom.isNotBlank() || dateTo.isNotBlank()) {
                        Text("Period: ${dateFrom.ifBlank { "—" }} to ${dateTo.ifBlank { "—" }}",
                            fontSize = 11.sp, color = OnSurfaceMuted)
                    }
                    Text("Filter: ${if (filterType == "ALL") "All Transactions" else filterType}",
                        fontSize = 11.sp, color = OnSurfaceMuted)
                    Divider(color = Outline, modifier = Modifier.padding(vertical = 4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Income", fontSize = 13.sp, color = OnSurface)
                        Text("₹${"%,.2f".format(income)}", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Expense", fontSize = 13.sp, color = OnSurface)
                        Text("₹${"%,.2f".format(expense)}", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = ErrorRed)
                    }
                    Divider(color = Outline)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Net Balance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("₹${"%,.2f".format(net)}", fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (net >= 0) SuccessGreen else ErrorRed)
                    }
                    Divider(color = Outline, modifier = Modifier.padding(vertical = 4.dp))
                    Text("Transactions: ${transactions.size}", fontSize = 12.sp, color = OnSurfaceMuted)
                }
            }

            // Transaction breakdown
            if (transactions.isNotEmpty()) {
                Text("Transaction Breakdown", fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, color = OnSurface)
                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                    modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        transactions.take(10).forEach { txn ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(Modifier.weight(1f)) {
                                    Text(txn.category, fontSize = 12.sp, color = OnSurface,
                                        fontWeight = FontWeight.Medium)
                                    Text(txn.transactionDate, fontSize = 10.sp, color = OnSurfaceHint)
                                }
                                Text(
                                    "${if (txn.type == "INCOME") "+" else "-"}₹${"%,.0f".format(txn.amount)}",
                                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = if (txn.type == "INCOME") SuccessGreen else ErrorRed
                                )
                            }
                        }
                        if (transactions.size > 10) {
                            Text("... and ${transactions.size - 10} more",
                                fontSize = 11.sp, color = OnSurfaceHint)
                        }
                    }
                }
            }

            // Share as text
            Button(
                onClick = {
                    val text = buildString {
                        appendLine("ANJANA INFOTECH — FINANCE SUMMARY")
                        appendLine("ISO 9001:2015 Certified")
                        appendLine("Generated: ${java.time.LocalDate.now()}")
                        if (dateFrom.isNotBlank() || dateTo.isNotBlank())
                            appendLine("Period: ${dateFrom.ifBlank{"—"}} to ${dateTo.ifBlank{"—"}}")
                        appendLine("Filter: ${if (filterType=="ALL") "All" else filterType}")
                        appendLine("─────────────────────────────")
                        appendLine("Total Income:  ₹${"%,.2f".format(income)}")
                        appendLine("Total Expense: ₹${"%,.2f".format(expense)}")
                        appendLine("Net Balance:   ₹${"%,.2f".format(net)}")
                        appendLine("─────────────────────────────")
                        appendLine("Transactions (${transactions.size}):")
                        transactions.forEach { txn ->
                            appendLine("${if (txn.type=="INCOME") "+" else "-"}₹${"%,.0f".format(txn.amount)}  ${txn.category}  ${txn.transactionDate}")
                        }
                    }
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Finance Summary — Anjana Infotech")
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, "Share Summary"))
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
            ) { Text("Share / Export Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp) }

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Outline)
            ) { Text("Close", color = OnSurfaceMuted) }
        }
    }
}

// ── Color helpers ─────────────────────────────────────────────
@Composable
private fun txFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = accent,
    unfocusedBorderColor    = Outline,
    focusedLabelColor       = accent,
    unfocusedLabelColor     = OnSurfaceMuted,
    cursorColor             = BrandMagenta,
    focusedTextColor        = OnSurface,
    unfocusedTextColor      = OnSurface,
    focusedContainerColor   = White,
    unfocusedContainerColor = White
)

@Composable
private fun dateFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = BrandPurple,
    unfocusedBorderColor    = Outline,
    focusedLabelColor       = BrandPurple,
    unfocusedLabelColor     = OnSurfaceMuted,
    cursorColor             = BrandMagenta,
    focusedTextColor        = OnSurface,
    unfocusedTextColor      = OnSurface,
    focusedContainerColor   = White,
    unfocusedContainerColor = White
)
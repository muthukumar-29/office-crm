package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInvoiceScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state        by vm.state.collectAsState()
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val userName     by vm.userName.collectAsState(initial = "")
    val role         by vm.role.collectAsState(initial = "")
    var showCreate   by remember { mutableStateOf(false) }
    val currentRoute  = navController.currentBackStackEntry?.destination?.route

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
            topBar = { CrmTopBar("Invoices", onMenuClick = { scope.launch { drawerState.open() } }) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreate = true },
                    containerColor = BrandMagenta,
                    contentColor = White,
                    shape = RoundedCornerShape(14.dp)
                ) { Icon(Icons.Default.Add, "New Invoice") }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) { StatCard("Total", "${state.invoices.size}", BrandMagenta, "🧾") }
                        Box(Modifier.weight(1f)) { StatCard("Paid", "${state.invoices.count { it.status == "PAID" }}", SuccessGreen, "✅") }
                    }
                }
                item { Text("${state.invoices.size} invoices", fontSize = 12.sp, color = OnSurfaceMuted) }
                items(state.invoices) { inv ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(inv.clientName, color = OnSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(inv.invoiceNumber, color = OnSurfaceHint, fontSize = 11.sp)
                                Text(inv.invoiceDate, color = OnSurfaceMuted, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${"%,.0f".format(inv.totalAmount)}", color = BrandMagenta,
                                    fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                StatusBadge(inv.status)
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCreate) CreateInvoiceSheet(vm = vm, onDismiss = { showCreate = false })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceSheet(vm: AdminViewModel, onDismiss: () -> Unit) {
    var clientName    by remember { mutableStateOf("") }
    var clientEmail   by remember { mutableStateOf("") }
    var clientPhone   by remember { mutableStateOf("") }
    var clientAddress by remember { mutableStateOf("") }
    var subtotal      by remember { mutableStateOf("") }
    var taxPercent    by remember { mutableStateOf("18") }
    var discount      by remember { mutableStateOf("0") }
    var submitted     by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("New Invoice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            if (submitted) Surface(color = Color(0xFFDCF5E8), shape = RoundedCornerShape(10.dp)) {
                Text("✓ Invoice created!", color = SuccessGreen, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
            }
            OutlinedTextField(value = clientName, onValueChange = { clientName = it },
                label = { Text("Client Name *") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp), colors = invoiceColors())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = clientEmail, onValueChange = { clientEmail = it },
                    label = { Text("Email") }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), colors = invoiceColors())
                OutlinedTextField(value = clientPhone, onValueChange = { clientPhone = it },
                    label = { Text("Phone") }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), colors = invoiceColors())
            }
            OutlinedTextField(value = clientAddress, onValueChange = { clientAddress = it },
                label = { Text("Address") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp), colors = invoiceColors(), maxLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subtotal, onValueChange = { subtotal = it },
                    label = { Text("Amount ₹ *") }, modifier = Modifier.weight(2f),
                    shape = RoundedCornerShape(10.dp), colors = invoiceColors())
                OutlinedTextField(value = taxPercent, onValueChange = { taxPercent = it },
                    label = { Text("Tax %") }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), colors = invoiceColors())
                OutlinedTextField(value = discount, onValueChange = { discount = it },
                    label = { Text("Disc ₹") }, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp), colors = invoiceColors())
            }
            subtotal.toDoubleOrNull()?.let { amt ->
                val taxAmt = amt * ((taxPercent.toDoubleOrNull() ?: 0.0) / 100.0)
                val disc   = discount.toDoubleOrNull() ?: 0.0
                Surface(color = BrandPurpleLight, shape = RoundedCornerShape(10.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Sub: ₹${"%,.2f".format(amt)}", fontSize = 11.sp, color = OnSurfaceMuted)
                            Text("Tax: +₹${"%,.2f".format(taxAmt)}", fontSize = 11.sp, color = OnSurfaceMuted)
                            Text("Disc: -₹${"%,.2f".format(disc)}", fontSize = 11.sp, color = OnSurfaceMuted)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total", fontSize = 11.sp, color = OnSurfaceMuted)
                            Text("₹${"%,.2f".format(amt + taxAmt - disc)}", fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold, color = BrandMagenta)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    vm.createInvoice(mapOf(
                        "clientName" to clientName, "clientEmail" to clientEmail,
                        "clientPhone" to clientPhone, "clientAddress" to clientAddress,
                        "subtotal" to (subtotal.toDoubleOrNull() ?: 0.0),
                        "taxPercent" to (taxPercent.toDoubleOrNull() ?: 0.0),
                        "discount" to (discount.toDoubleOrNull() ?: 0.0)
                    ))
                    submitted = true
                    clientName = ""; clientEmail = ""; clientPhone = ""
                    clientAddress = ""; subtotal = ""
                },
                enabled = clientName.isNotBlank() && subtotal.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
            ) { Text("Create Invoice", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
        }
    }
}

@Composable
private fun invoiceColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrandMagenta, unfocusedBorderColor = Outline,
    focusedLabelColor = BrandMagenta, unfocusedLabelColor = OnSurfaceMuted,
    cursorColor = BrandMagenta, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface,
    focusedContainerColor = White, unfocusedContainerColor = White
)
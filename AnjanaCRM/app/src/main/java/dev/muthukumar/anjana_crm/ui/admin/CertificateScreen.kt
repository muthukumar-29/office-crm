package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCertificateScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val userName    by vm.userName.collectAsState(initial = "")
    val role        by vm.role.collectAsState(initial = "")
    var selectedAllocId by remember { mutableStateOf<Long?>(null) }
    var grade       by remember { mutableStateOf("") }
    var issued      by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val eligible = state.allocations.filter {
        it.allocationStatus == "COMPLETED" &&
                state.certificates.none { c -> c.studentName == it.student?.name }
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
            topBar = { CrmTopBar("Certificates", onMenuClick = { scope.launch { drawerState.open() } }) }
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
                                color = OnSurfaceMuted, fontSize = 13.sp)
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
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrandMagenta,
                                        unfocusedBorderColor = Outline,
                                        focusedLabelColor = BrandMagenta,
                                        focusedContainerColor = White,
                                        unfocusedContainerColor = White
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    eligible.forEach { alloc ->
                                        DropdownMenuItem(
                                            text = {
                                                Text("${alloc.student?.name} — ${alloc.category}",
                                                    fontSize = 13.sp, color = OnSurface)
                                            },
                                            onClick = { selectedAllocId = alloc.id; expanded = false }
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = grade, onValueChange = { grade = it },
                                label = { Text("Grade (optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("A, B, Distinction…") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandMagenta,
                                    unfocusedBorderColor = Outline,
                                    focusedLabelColor = BrandMagenta,
                                    focusedContainerColor = White,
                                    unfocusedContainerColor = White
                                )
                            )
                            if (issued) {
                                Spacer(Modifier.height(6.dp))
                                Text("✓ Certificate issued!", color = SuccessGreen, fontSize = 13.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    selectedAllocId?.let {
                                        vm.issueCertificate(it, grade.ifBlank { null })
                                        issued = true; grade = ""; selectedAllocId = null
                                    }
                                },
                                enabled = selectedAllocId != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
                            ) { Text("Issue Certificate", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
                item {
                    Text("Issued (${state.certificates.size})", fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold, color = OnSurface)
                }
                items(state.certificates) { cert ->
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(cert.studentName, color = OnSurface,
                                    fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(cert.programTitle ?: "—", color = OnSurfaceMuted, fontSize = 12.sp)
                                Text(cert.issuedDate, color = OnSurfaceHint, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                cert.grade?.let { StatusBadge(it) }
                                Text(cert.certificateNumber, fontSize = 10.sp, color = BrandMagenta)
                            }
                        }
                    }
                }
            }
        }
    }
}
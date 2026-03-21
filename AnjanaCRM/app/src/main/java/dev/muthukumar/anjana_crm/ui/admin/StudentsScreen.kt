package dev.muthukumar.anjana_crm.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminStudentsScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state       by vm.state.collectAsState()
    val drawerState  = rememberDrawerState(DrawerValue.Closed)
    val scope        = rememberCoroutineScope()
    val userName    by vm.userName.collectAsState(initial = "")
    val role        by vm.role.collectAsState(initial = "")
    var search      by remember { mutableStateOf("") }
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val filtered = state.students.filter {
        search.isBlank() || it.name.contains(search, true)
                || it.email.contains(search, true)
                || (it.rollNo?.contains(search, true) == true)
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
            topBar = { CrmTopBar("Students", onMenuClick = { scope.launch { drawerState.open() } }) }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Search by name, email, roll no…") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandMagenta,
                        unfocusedBorderColor = Outline,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = BrandMagenta,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text("${filtered.size} students", fontSize = 12.sp, color = OnSurfaceMuted)
                    }
                    items(filtered) { student ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(44.dp).clip(CircleShape)
                                        .background(BrandMagentaLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(student.name.first().uppercase(), color = BrandMagenta,
                                        fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(student.name, color = OnSurface, fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(student.email, color = OnSurfaceMuted, fontSize = 12.sp)
                                    student.rollNo?.let {
                                        Text(it, color = OnSurfaceHint, fontSize = 11.sp)
                                    }
                                }
                                student.collegeName?.let {
                                    Text(it, fontSize = 10.sp, color = OnSurfaceHint,
                                        modifier = Modifier.width(80.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminUsersScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state        by vm.state.collectAsState()
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val userName     by vm.userName.collectAsState(initial = "")
    val role         by vm.role.collectAsState(initial = "")
    var search       by remember { mutableStateOf("") }
    val currentRoute  = navController.currentBackStackEntry?.destination?.route

    val filtered = state.users.filter {
        search.isBlank() || it.name.contains(search, true) || it.email.contains(search, true)
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
            topBar = { CrmTopBar("Users", onMenuClick = { scope.launch { drawerState.open() } }) }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Search by name or email…") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor     = BrandMagenta,
                        unfocusedBorderColor   = Outline,
                        focusedContainerColor  = White,
                        unfocusedContainerColor = White,
                        cursorColor            = BrandMagenta,
                        focusedLabelColor      = BrandMagenta,
                        focusedTextColor       = OnSurface,
                        unfocusedTextColor     = OnSurface
                    ),
                    singleLine = true
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text("${filtered.size} users", fontSize = 12.sp, color = OnSurfaceMuted)
                    }
                    items(filtered) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(BrandMagentaLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        user.name.first().uppercase(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandMagenta
                                    )
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(user.name, color = OnSurface, fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(user.email, color = OnSurfaceMuted, fontSize = 12.sp)
                                    user.position?.let {
                                        Text(it, color = OnSurfaceHint, fontSize = 11.sp)
                                    }
                                }
                                val (roleColor, roleText) = when (user.role) {
                                    "SUPER_ADMIN" -> BrandPurple to BrandPurpleLight
                                    "ADMIN"       -> BrandMagenta to BrandMagentaLight
                                    "EMPLOYEE"    -> InfoBlue to Color(0xFFE3F2FD)
                                    "SUB_ADMIN"   -> BrandMagenta to BrandMagentaLight
                                    else          -> OnSurfaceMuted to SurfaceVariant
                                }
                                Surface(color = roleText, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        user.role.replace("_", " "),
                                        fontSize = 10.sp,
                                        color = roleColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
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

@Composable
fun AdminStudentsScreen(navController: NavController, vm: AdminViewModel = viewModel()) {
    val state  by vm.state.collectAsState()
    var search by remember { mutableStateOf("") }

    val filtered = state.students.filter {
        search.isBlank()
                || it.name.contains(search, ignoreCase = true)
                || it.email.contains(search, ignoreCase = true)
                || (it.rollNo?.contains(search, ignoreCase = true) == true)
    }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("Students", onBack = { navController.popBackStack() }) },
        bottomBar = { AdminBottomNav(navController) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search by name, email, roll no…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text("${filtered.size} students", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                items(filtered) { student ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar circle
                            Surface(
                                color = BrandBlue.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = student.name.first().uppercase(),
                                    color = BrandBlue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .wrapContentSize(Alignment.Center)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(student.name, color = Color.White, fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold)
                                Text(student.email, color = Color(0xFF94A3B8), fontSize = 12.sp)
                                student.rollNo?.let {
                                    Text(it, color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                            }
                            student.collegeName?.let {
                                Text(it, fontSize = 10.sp, color = Color(0xFF64748B),
                                    modifier = Modifier.width(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

package dev.muthukumar.anjana_crm.ui.student

import androidx.compose.foundation.background
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
fun StudentCertificateScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("My Certificates", onBack = { navController.popBackStack() }) },
        bottomBar = { StudentBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (state.certificates.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎓", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No certificates yet", color = Color(0xFF64748B), fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Complete your program to receive one",
                                color = Color(0xFF475569), fontSize = 12.sp)
                        }
                    }
                }
            }
            items(state.certificates) { cert ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column {
                        // Gradient top accent bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(BrandBlue, BrandOrange)
                                    )
                                )
                        )
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text("Certificate of Completion", fontSize = 10.sp,
                                        color = Color(0xFF64748B), letterSpacing = 0.6.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(4.dp))
                                    Text(cert.programTitle ?: cert.domainName ?: "Program",
                                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(cert.studentName, fontSize = 13.sp, color = Color(0xFF94A3B8))
                                }
                                cert.grade?.let {
                                    Surface(color = BrandOrange.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                                        Text(it, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                                            color = BrandOrange,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Divider(color = Color(0x1AFFFFFF))
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Cert No.", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(cert.certificateNumber, fontSize = 12.sp,
                                        color = Color(0xFF60A5FA), fontWeight = FontWeight.Medium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Issued", fontSize = 10.sp, color = Color(0xFF64748B))
                                    Text(cert.issuedDate, fontSize = 12.sp, color = Color(0xFF94A3B8))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Anjana Infotech · ISO 9001:2015 Certified",
                                fontSize = 10.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }
        }
    }
}

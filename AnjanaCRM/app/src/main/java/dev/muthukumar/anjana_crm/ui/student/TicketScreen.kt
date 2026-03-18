package dev.muthukumar.anjana_crm.ui.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import dev.muthukumar.anjana_crm.ui.common.*

@Composable
fun TicketScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state   by vm.state.collectAsState()
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Scaffold(
        containerColor = PageBg,
        topBar = { CrmTopBar("Help & Feedback", onBack = { navController.popBackStack() }) },
        bottomBar = { StudentBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
        ) {
            item {
                SectionCard("Submit a Query / Feedback") {
                    if (state.ticketSubmitted) {
                        Surface(
                            color = Color(0x2210B981), shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("✓ Query submitted! We'll respond soon.",
                                color = Color(0xFF10B981), fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it; vm.resetTicketFlag() },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 5
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            vm.submitTicket(subject, message)
                            subject = ""
                            message = ""
                        },
                        enabled = subject.isNotBlank() && message.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) { Text("Submit", fontWeight = FontWeight.SemiBold) }
                }
            }

            item {
                Text("Previous Queries (${state.tickets.size})", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
            }

            items(state.tickets) { ticket ->
                Card(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ticket.subject, color = Color.White, fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            StatusBadge(ticket.status)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(ticket.message, color = Color(0xFF94A3B8), fontSize = 12.sp)
                        ticket.response?.let {
                            Spacer(Modifier.height(8.dp))
                            Surface(color = Color(0x1A60A5FA), shape = RoundedCornerShape(6.dp)) {
                                Text("Admin: $it", color = Color(0xFF93C5FD), fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(ticket.createdAt, fontSize = 10.sp, color = Color(0xFF475569))
                    }
                }
            }
        }
    }
}

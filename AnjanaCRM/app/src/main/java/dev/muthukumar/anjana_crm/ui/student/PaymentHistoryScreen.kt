// ─── PaymentHistoryScreen.kt ─────────────────────────────────
package dev.muthukumar.anjana_crm.ui.student

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

@Composable
fun PaymentHistoryScreen(navController: NavController, vm: StudentViewModel = viewModel()) {
    val state     by vm.state.collectAsState()
    val totalPaid  = state.payments.sumOf { it.amount }

    Scaffold(
        containerColor = OffWhite,
        topBar = { CrmTopBar("Payment History", onBack = { navController.popBackStack() }) },
        bottomBar = { StudentBottomNav(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { StatCard("Total Paid", "₹${"%,.0f".format(totalPaid)}", SuccessGreen, "💳") }
            item {
                Text("${state.payments.size} payment${if (state.payments.size != 1) "s" else ""}",
                    fontSize = 12.sp, color = OnSurfaceMuted)
            }
            if (state.payments.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💳", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No payments yet", color = OnSurfaceMuted, fontSize = 14.sp)
                        }
                    }
                }
            }
            items(state.payments) { payment ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(payment.paymentDate, fontSize = 13.sp,
                                fontWeight = FontWeight.Medium, color = OnSurface)
                            Text(payment.paymentMode.replace("_", " "),
                                fontSize = 11.sp, color = OnSurfaceMuted)
                            Text("Ref: ${payment.receiptNumber}",
                                fontSize = 10.sp, color = OnSurfaceHint)
                        }
                        Text("₹${"%,.0f".format(payment.amount)}", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            }
        }
    }
}
package dev.muthukumar.anjana_crm.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.muthukumar.anjana_crm.ui.theme.*

// ── Login screen — supports Staff login (email + password)
//                 and Student login (rollNo + rollNo as password)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    // Tab: 0 = Staff, 1 = Student
    var selectedTab by remember { mutableStateOf(0) }

    // Staff fields
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    // Student fields
    var rollNo by remember { mutableStateOf("") }

    LaunchedEffect(uiState.navigateTo) {
        if (uiState.navigateTo != null) onLoginSuccess(uiState.navigateTo!!)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Brand ─────────────────────────────────────
            Text(
                "Anjana Infotech",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandMagenta
            )
            Text(
                "ISO 9001:2015 Certified",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                "Office CRM",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // ── Tab selector ───────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.fillMaxWidth().padding(4.dp)) {
                    listOf("Staff / Admin", "Student").forEachIndexed { idx, label ->
                        val selected = selectedTab == idx
                        Button(
                            onClick = {
                                selectedTab = idx
                                vm.clearError()
                                email = ""; password = ""; rollNo = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) BrandMagenta else Color.Transparent,
                                contentColor   = if (selected) Color.White  else Color(0xFF94A3B8)
                            ),
                            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
                        ) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Form card ──────────────────────────────────
            Card(
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {

                    if (selectedTab == 0) {
                        // ── STAFF LOGIN ──────────────────────────
                        Text("Sign in to your workspace",
                            fontSize = 13.sp, color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 16.dp))

                        CrmField("Email") {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("you@anjanainfotech.in", color = Color(0xFF475569)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = crmTextFieldColors()
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        CrmField("Password") {
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("••••••••", color = Color(0xFF475569)) },
                                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = crmTextFieldColors(),
                                trailingIcon = {
                                    TextButton(onClick = { showPass = !showPass }) {
                                        Text(if (showPass) "Hide" else "Show",
                                            fontSize = 11.sp, color = BrandMagenta)
                                    }
                                }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) return@Button
                                vm.login(email.trim(), password)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.loading && email.isNotBlank() && password.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
                        ) {
                            if (uiState.loading)
                                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else
                                Text("Sign In", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }

                    } else {
                        // ── STUDENT LOGIN ────────────────────────
                        Text("Enter your Roll Number to continue",
                            fontSize = 13.sp, color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 16.dp))

                        CrmField("Roll Number") {
                            OutlinedTextField(
                                value = rollNo,
                                onValueChange = { rollNo = it.uppercase() },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. AI2024001", color = Color(0xFF475569)) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = crmTextFieldColors()
                            )
                        }

                        // Hint that rollNo is also the password
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0x1AC0297A), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ℹ", fontSize = 14.sp, color = BrandMagenta)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Your roll number is used as both username and password.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (rollNo.isBlank()) return@Button
                                // Student email format expected by backend: rollNo@student.crm
                                // Password = rollNo itself
                                vm.loginStudent(rollNo.trim())
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !uiState.loading && rollNo.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
                        ) {
                            if (uiState.loading)
                                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else
                                Text("Continue", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }

                    // ── Error message ──────────────────────────
                    uiState.error?.let { err ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            err,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x1AEF4444), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Contact footer ─────────────────────────────
            Text(
                "372, Mudangiyar Road, Rajapalayam • +91 97879 70633",
                fontSize = 11.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────

@Composable
private fun CrmField(label: String, content: @Composable () -> Unit) {
    Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        color = Color(0xFF94A3B8), letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 6.dp))
    content()
}

@Composable
private fun crmTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = BrandMagenta,
    unfocusedBorderColor = Color(0xFF334155),
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = BrandMagenta,
    focusedContainerColor   = Color(0xFF1E293B),
    unfocusedContainerColor = Color(0xFF1E293B),
)
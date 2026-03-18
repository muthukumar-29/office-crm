package dev.muthukumar.anjana_crm.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.muthukumar.anjana_crm.navigation.roleStartScreen

val BrandBlue   = Color(0xFF0A50B4)
val BrandOrange = Color(0xFFE66414)

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigate when login succeeds
    LaunchedEffect(ui.role) {
        ui.role?.let { onLoginSuccess(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brand header
        Text(
            text = "Anjana Infotech",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF60A5FA)
        )
        Text(
            text = "ISO 9001:2015 Certified",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Staff Portal",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 2.dp, bottom = 36.dp)
        )

        // Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sign in", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

                // Error
                ui.error?.let {
                    Surface(
                        color = Color(0x22EF4444),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                // Login button
                Button(
                    onClick = { vm.login(email, password) },
                    enabled = !ui.loading && email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    if (ui.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign in", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Contact info
        Spacer(Modifier.height(24.dp))
        Text("372, Mudangiyar Road, Rajapalayam", fontSize = 11.sp, color = Color(0xFF475569))
        Text("+91 97879 70633  |  info@anjanainfotech.in", fontSize = 11.sp, color = Color(0xFF475569))
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor  = BrandBlue,
    unfocusedBorderColor= Color(0xFF334155),
    focusedLabelColor   = BrandBlue,
    unfocusedLabelColor = Color(0xFF94A3B8),
    cursorColor         = BrandBlue,
    focusedTextColor    = Color.White,
    unfocusedTextColor  = Color(0xFFCBD5E1),
    focusedLeadingIconColor   = BrandBlue,
    unfocusedLeadingIconColor = Color(0xFF64748B),
    focusedContainerColor  = Color(0xFF0F172A),
    unfocusedContainerColor= Color(0xFF0F172A)
)

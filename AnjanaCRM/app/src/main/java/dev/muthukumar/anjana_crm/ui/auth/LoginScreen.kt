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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.muthukumar.anjana_crm.ui.theme.*

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val ui        by vm.ui.collectAsState()
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }

    LaunchedEffect(ui.role) {
        ui.role?.let { onLoginSuccess(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhite)
    ) {
        // Top gradient banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPurple, BrandMagenta)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo
            Text("AI", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = White)
            Spacer(Modifier.height(4.dp))
            Text("ANJANA INFOTECH", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = White, letterSpacing = 2.sp)
            Text("ISO 9001:2015 Certified", fontSize = 11.sp,
                color = White.copy(alpha = 0.75f), letterSpacing = 1.sp)

            Spacer(Modifier.height(40.dp))

            // Login card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Welcome Back", fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("Sign in to your CRM account", fontSize = 13.sp, color = OnSurfaceMuted)

                    // Error banner
                    ui.error?.let {
                        Surface(color = Color(0xFFFFE8E8), shape = RoundedCornerShape(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 14.sp)
                                Text(it, color = ErrorRed, fontSize = 13.sp)
                            }
                        }
                    }

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = BrandMagenta) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = loginFieldColors(),
                        singleLine = true
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = BrandMagenta) },
                        trailingIcon = {
                            TextButton(onClick = { showPass = !showPass }) {
                                Text(if (showPass) "Hide" else "Show",
                                    color = BrandMagenta, fontSize = 12.sp)
                            }
                        },
                        visualTransformation = if (showPass) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = loginFieldColors(),
                        singleLine = true
                    )

                    // Sign In button
                    Button(
                        onClick = { vm.login(email, password) },
                        enabled = !ui.loading && email.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandMagenta,
                            disabledContainerColor = BrandMagentaLight
                        )
                    ) {
                        if (ui.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text("372, Mudangiyar Road, Rajapalayam",
                    fontSize = 11.sp, color = OnSurfaceHint)
                Text("+91 97879 70633  |  info@anjanainfotech.in",
                    fontSize = 11.sp, color = OnSurfaceHint)
            }
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = BrandMagenta,
    unfocusedBorderColor    = Outline,
    focusedLabelColor       = BrandMagenta,
    unfocusedLabelColor     = OnSurfaceMuted,
    cursorColor             = BrandMagenta,
    focusedTextColor        = OnSurface,
    unfocusedTextColor      = OnSurface,
    focusedContainerColor   = White,
    unfocusedContainerColor = White
)
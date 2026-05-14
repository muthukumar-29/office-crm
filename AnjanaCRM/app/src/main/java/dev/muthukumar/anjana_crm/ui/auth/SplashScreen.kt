package dev.muthukumar.anjana_crm.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.muthukumar.anjana_crm.data.store.TokenStore
import dev.muthukumar.anjana_crm.navigation.roleStartScreen
import dev.muthukumar.anjana_crm.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(onFinished: (String) -> Unit) {
    val context = LocalContext.current
    val store   = remember { TokenStore(context) }

    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue    = if (visible) 1f else 0f,
        animationSpec  = tween(700),
        label          = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue    = if (visible) 1f else 0.75f,
        animationSpec  = tween(700, easing = EaseOutBack),
        label          = "scale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2400L)
        val token = store.token.first()
        val role  = store.role.first()
        val dest  = if (token == null) "login" else roleStartScreen(role)
        onFinished(dest)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A0060), BrandPurple, BrandMagenta)
                )
            )
    ) {
        // ── Centre content ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "A",
                        fontSize   = 46.sp,
                        fontWeight = FontWeight.Black,
                        color      = White
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text       = "Anjana Infotech",
                fontSize   = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = White,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(6.dp))

            Surface(
                color = White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text(
                    text     = "ISO 9001:2015 Certified",
                    fontSize = 11.sp,
                    color    = White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text       = "Office CRM",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = White.copy(alpha = 0.75f),
                letterSpacing = 2.sp
            )
        }

        // ── Bottom area ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .graphicsLayer(alpha = alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier     = Modifier.width(100.dp).clip(RoundedCornerShape(4.dp)),
                color        = White.copy(alpha = 0.85f),
                trackColor   = White.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text     = "Rajapalayam · Tamil Nadu",
                fontSize = 11.sp,
                color    = White.copy(alpha = 0.45f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "v1.0",
                fontSize = 10.sp,
                color    = White.copy(alpha = 0.3f)
            )
        }
    }
}

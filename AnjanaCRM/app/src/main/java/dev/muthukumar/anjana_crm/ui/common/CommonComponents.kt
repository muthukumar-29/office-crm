package dev.muthukumar.anjana_crm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.muthukumar.anjana_crm.ui.theme.*

// ── Status badge ─────────────────────────────────────────────
@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "ACTIVE", "PAID", "COMPLETED", "ENROLLED"  ->
            Color(0xFFDCF5E8) to SuccessGreen
        "PENDING", "IN_PROGRESS", "ONGOING",
        "NOT_STARTED", "REVIEW_PENDING"             ->
            Color(0xFFFFF3E0) to WarningAmber
        "DROPPED", "CANCELLED", "FAILED",
        "TERMINATED"                                ->
            Color(0xFFFFE8E8) to ErrorRed
        "ON_HOLD"                                   ->
            BrandPurpleLight to BrandPurple
        "DELIVERED"                                 ->
            Color(0xFFE3F2FD) to InfoBlue
        else                                        ->
            SurfaceVariant to OnSurfaceMuted
    }
    Surface(color = bg, shape = RoundedCornerShape(99.dp)) {
        Text(
            text = status.replace("_", " "),
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

// ── Stat card ────────────────────────────────────────────────
@Composable
fun StatCard(
    label: String,
    value: String,
    accentColor: Color = BrandMagenta,
    icon: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = OnSurfaceMuted,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.3.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(it, fontSize = 20.sp)
                }
            }
        }
    }
}

// ── Section card ─────────────────────────────────────────────
@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandPurple,
                letterSpacing = 0.4.sp
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ── Loading ──────────────────────────────────────────────────
@Composable
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = BrandMagenta, strokeWidth = 3.dp)
    }
}

// ── Error ────────────────────────────────────────────────────
@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️", fontSize = 40.sp)
        Spacer(Modifier.height(12.dp))
        Text(message, color = ErrorRed, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta),
            shape = RoundedCornerShape(10.dp)
        ) { Text("Retry") }
    }
}

// ── Top app bar with drawer toggle ───────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmTopBar(
    title: String,
    onMenuClick: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = OnSurface
            )
        },
        navigationIcon = {
            when {
                onBack != null -> IconButton(onClick = onBack) {
                    Text("←", fontSize = 20.sp, color = BrandMagenta)
                }
                onMenuClick != null -> IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, null, tint = BrandMagenta)
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White,
            titleContentColor = OnSurface
        )
    )
}

// ── Primary button ───────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandMagenta)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

// ── Divider with label ───────────────────────────────────────
@Composable
fun LabelDivider(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(Modifier.weight(1f), color = Outline)
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 11.sp,
            color = OnSurfaceHint
        )
        Divider(Modifier.weight(1f), color = Outline)
    }
}
package dev.muthukumar.anjana_crm.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.muthukumar.anjana_crm.ui.theme.*

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val route: String,
    val badge: String? = null
)

@Composable
fun AppDrawer(
    userName: String?,
    userRole: String?,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    drawerItems: List<DrawerItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // ── Header with gradient ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPurple, BrandMagenta)
                    )
                )
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (userName?.firstOrNull()?.uppercase()) ?: "A",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    userName ?: "User",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text(
                        userRole?.replace("_", " ") ?: "USER",
                        fontSize = 11.sp,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Anjana Infotech",
                    fontSize = 11.sp,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Nav items ────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            drawerItems.forEach { item ->
                val isSelected = currentRoute == item.route
                DrawerNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) }
                )
            }
        }

        // ── Divider + Logout ─────────────────────────────────
        Divider(color = Outline, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.ExitToApp, null, tint = ErrorRed, modifier = Modifier.size(22.dp))
            Text("Sign Out", fontSize = 14.sp, color = ErrorRed, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(16.dp))

        // Footer
        Text(
            "Anjana Infotech · ISO 9001:2015",
            fontSize = 10.sp,
            color = OnSurfaceHint,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun DrawerNavItem(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) BrandMagentaLight else Color.Transparent
    val contentColor = if (isSelected) BrandMagenta else OnSurfaceMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            item.icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            item.label,
            fontSize = 14.sp,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        item.badge?.let {
            Surface(
                color = BrandMagenta,
                shape = CircleShape
            ) {
                Text(
                    it,
                    fontSize = 10.sp,
                    color = White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandMagenta)
            )
        }
    }
}
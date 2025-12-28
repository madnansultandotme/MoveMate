package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.application.movemate.ui.theme.*

// Custom colors for Carrier Profile
private val SurfaceLight = Color(0xFFFFFFFF)
private val SurfaceDarkCarrier = Color(0xFF1C1F26)
private val GoldStar = Color(0xFFEAB308)
private val GreenVerified = Color(0xFF22C55E)
private val RedLogout = Color(0xFFEF4444)

// Data classes
data class SettingsItem(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val hasToggle: Boolean = false,
    val toggleState: Boolean = false,
    val statusIndicator: SettingsStatusIndicator? = null,
    val onClick: () -> Unit = {}
)

data class SettingsStatusIndicator(
    val text: String,
    val color: Color
)

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>
)

class CarrierProfileSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                CarrierProfileSettingsScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierProfileSettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    var notificationsEnabled by remember { mutableStateOf(true) }

    val personalDetailsSection = SettingsSection(
        title = "Personal Details",
        items = listOf(
            SettingsItem(
                title = "Edit Profile",
                subtitle = "Name, Phone, Email",
                icon = Icons.Default.Person,
                onClick = { }
            ),
            SettingsItem(
                title = "Company Info",
                subtitle = "Tax ID, Business Name",
                icon = Icons.Default.Business,
                onClick = { }
            )
        )
    )

    val fleetVehicleSection = SettingsSection(
        title = "Fleet & Vehicle",
        items = listOf(
            SettingsItem(
                title = "Primary Vehicle",
                subtitle = "Ford Transit - 2 Ton",
                icon = Icons.Default.LocalShipping,
                onClick = { }
            ),
            SettingsItem(
                title = "Documents",
                icon = Icons.Default.Description,
                statusIndicator = SettingsStatusIndicator(
                    text = "All Verified",
                    color = GreenVerified
                ),
                onClick = { }
            )
        )
    )

    val financialsSection = SettingsSection(
        title = "Financials",
        items = listOf(
            SettingsItem(
                title = "Payout Method",
                subtitle = "Chase Bank •••• 1234",
                icon = Icons.Default.AccountBalance,
                onClick = { }
            ),
            SettingsItem(
                title = "Payment History",
                icon = Icons.Default.ReceiptLong,
                onClick = { }
            )
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
    ) {
        Scaffold(
            topBar = {
                CarrierProfileTopBar(onBackClick = onBackClick)
            },
            bottomBar = {
                CarrierProfileBottomNav()
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Header
                ProfileHeader()

                // Personal Details Section
                SettingsSectionView(section = personalDetailsSection)

                // Fleet & Vehicle Section
                SettingsSectionView(section = fleetVehicleSection)

                // Financials Section
                SettingsSectionView(section = financialsSection)

                // Preferences Section (with toggle)
                PreferencesSection(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsToggle = { notificationsEnabled = it }
                )

                // Logout Button
                LogoutButton()

                // Version info
                Text(
                    text = "Version 2.4.0 (Build 342)",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFF4B5563) else Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun CarrierProfileTopBar(onBackClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color(0xFF111827),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title
            Text(
                text = "Profile Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF111827),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Spacer for symmetry
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun ProfileHeader() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with verification badge
        Box(
            modifier = Modifier.size(112.dp)
        ) {
            // Profile Image
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAMpPramoESsVEqRyNf-JDR2-LGyz0VB19VrA1aIfI_LFYN3jwEErCB9oRfYJjY2M-NVIRkV6WayFyyf6mniRcPvSdDhUgAVJkEEUemS206QVrF5iBva2mSlCtlgFN6mwFaykK76yszwWGyANZZuEHKJVT3hBTDZ8dkpshT6txrkIGsBQbpT_CoPcDeRcN373UbNN-9zzyJgW_jIMEkqu5W8ym_9TU7usLS0gl5Lu69wWyp7Ew259VUfHdgGWySVKemYx14uHH0p6k",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLight,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            // Verification Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .border(
                        width = 2.dp,
                        color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "John Doe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Stats Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = GoldStar,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "4.8",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GoldStar
                )
            }

            Text(
                text = "•",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            )

            // Carrier Status
            Text(
                text = "Gold Carrier",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )

            Text(
                text = "•",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            )

            // Verified Status
            Text(
                text = "Verified",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun SettingsSectionView(section: SettingsSection) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Title
        Text(
            text = section.title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // Settings Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLight,
            shadowElevation = 2.dp,
            border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFF3F4F6)
            )
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    SettingsListItem(
                        item = item,
                        showDivider = index < section.items.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsListItem(
    item: SettingsItem,
    showDivider: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !item.hasToggle) { item.onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDarkTheme) Primary.copy(alpha = 0.1f) else Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color(0xFF111827)
                )

                if (item.subtitle != null) {
                    Text(
                        text = item.subtitle,
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (item.statusIndicator != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(item.statusIndicator.color)
                        )
                        Text(
                            text = item.statusIndicator.text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = item.statusIndicator.color
                        )
                    }
                }
            }

            // Arrow or Toggle
            if (item.hasToggle) {
                // Toggle handled in PreferencesSection
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 72.dp),
                color = if (isDarkTheme) Color(0xFF374151) else Color(0xFFF3F4F6),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun PreferencesSection(
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section Title
        Text(
            text = "PREFERENCES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        // Settings Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLight,
            shadowElevation = 2.dp,
            border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFF3F4F6)
            )
        ) {
            Column {
                // Notifications Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) Primary.copy(alpha = 0.1f) else Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Push Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color.White else Color(0xFF111827),
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onNotificationsToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB)
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = if (isDarkTheme) Color(0xFF374151) else Color(0xFFF3F4F6),
                    thickness = 1.dp
                )

                // Security
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) Primary.copy(alpha = 0.1f) else Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Security",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color.White else Color(0xFF111827)
                        )
                        Text(
                            text = "2FA, Password",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutButton() {
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                RedLogout.copy(alpha = 0.3f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDarkTheme) Color(0xFFF87171) else RedLogout
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log Out",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CarrierProfileBottomNav() {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLight,
        shadowElevation = 8.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dashboard
                BottomNavItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    isSelected = false,
                    onClick = { }
                )

                // My Jobs
                BottomNavItem(
                    icon = Icons.Default.LocalShipping,
                    label = "My Jobs",
                    isSelected = false,
                    onClick = { }
                )

                // Profile (selected)
                Box {
                    BottomNavItem(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        isSelected = true,
                        onClick = { }
                    )
                    // Notification badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RedLogout)
                            .border(
                                width = 1.dp,
                                color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLight,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val color = if (isSelected) Primary else if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierProfileSettingsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierProfileSettingsScreen()
    }
}

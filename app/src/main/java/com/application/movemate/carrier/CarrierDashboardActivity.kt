package com.application.movemate.carrier

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.RoleSelectionActivity
import com.application.movemate.auth.CarrierAuthViewModel
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.CarrierViewModel

// Custom colors for Carrier Dashboard
private val SurfaceDarkCarrier = Color(0xFF1C2633)
private val SurfaceLightCarrier = Color(0xFFFFFFFF)

class CarrierDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                CarrierDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierDashboardScreen(
    viewModel: CarrierViewModel = viewModel(),
    authViewModel: CarrierAuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val carrier by viewModel.carrier.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCarrier("carrier_id_1")
    }

    Scaffold(
        bottomBar = {
            // Bottom Navigation Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8)
            ) {
                Column {
                    HorizontalDivider(
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(top = 8.dp, bottom = 16.dp)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CarrierBottomNavItem(
                            icon = Icons.Filled.Home,
                            label = "Home",
                            isSelected = true,
                            onClick = { }
                        )
                        CarrierBottomNavItem(
                            icon = Icons.Outlined.Search,
                            label = "Loads",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, AvailableLoadsActivity::class.java)) }
                        )
                        CarrierBottomNavItem(
                            icon = Icons.Outlined.LocalShipping,
                            label = "Trips",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, MyShipmentsActivity::class.java)) }
                        )
                        CarrierBottomNavItem(
                            icon = Icons.Outlined.Person,
                            label = "Profile",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, CarrierProfileSettingsActivity::class.java)) }
                        )
                    }
                    // Home indicator
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(128.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1))
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            CarrierDashboardHeader(
                userName = "John Doe",
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCaQf4lHwYsWKOVLpkOEfX5GnJTxhoMjCHutqXo36VstjieLnkhUicuGfYIFckYY89yns8nqkml0cpXzja_iAGujMowEsHUH_SS05TTo9ggTKKcR_UVGH7W1TmwQdiPf9ooXOWkWDmt0LKXbaJbDzWmWBjpa700YG3t03QvXPKChMlWSeHrqzEHPF0K8tUaz20uEAaFnWz5vr5lonqkRv_rm9lbthVCLReI1ZdPzmkKD3YNaWbrnqCBAg-DYlGlyv5LTWblzWyYCg4",
                onNotificationClick = { }
            )

            // Stats / Earnings Card
            EarningsCard(
                weeklyEarnings = "$1,240.50",
                previousWeek = "$1,107.00",
                percentChange = "12%"
            )

            // Vehicle Status
            VehicleStatusCard(
                vehicleName = "Mercedes Sprinter",
                plateNumber = "ABC-1234",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA4VJbUygHT3mfPoRBg_kuY8H56kcJnVmtKs9QQHAix8bUuPCMw17zNKJP0LgIi7TPgSYyd7oO_gDYcTtzZBv-VPA7zXKTATSTGKTPOuv8PC11uxIj4j6gw3P_BoxTwxe5UnIslgHuX_b8_0XsCp-QO0i8JPZvuylSMIgCTB_4AwHx3gpdjaIj-lBV6YD4gFT760-wO_ldzmBNfwWe17pLkpW270YGguuA2ED4C63m_-kMS2f31xKkd8lEEVvAzui3ODNEPdjRUVTU",
                isVerified = true,
                onChangeClick = { }
            )

            // Quick Actions Grid
            QuickActionsSection(
                onBrowseLoadsClick = { context.startActivity(Intent(context, AvailableLoadsActivity::class.java)) },
                onMyBidsClick = { context.startActivity(Intent(context, CarrierMyBidsActivity::class.java)) },
                onCurrentTripClick = { context.startActivity(Intent(context, MyShipmentsActivity::class.java)) },
                pendingBidsCount = 2,
                availableLoadsCount = 45
            )

            // Secondary Menu List
            MenuSection(
                onEarningsHistoryClick = { context.startActivity(Intent(context, EarningsActivity::class.java)) },
                onProfileSettingsClick = { context.startActivity(Intent(context, CarrierProfileSettingsActivity::class.java)) },
                onHelpSupportClick = { },
                onLogoutClick = {
                    authViewModel.logout()
                    val intent = Intent(context, RoleSelectionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CarrierDashboardHeader(
    userName: String,
    avatarUrl: String,
    onNotificationClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with online indicator
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape)
                    )
                    // Online indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .border(
                                2.dp,
                                if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
                                CircleShape
                            )
                    )
                }

                Column {
                    Text(
                        text = "Welcome back,",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                }
            }

            // Notification button
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNotificationClick() },
                shape = CircleShape,
                color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    // Notification badge
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .border(
                                1.dp,
                                if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun EarningsCard(
    weeklyEarnings: String,
    previousWeek: String,
    percentChange: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Total Earnings (Week)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
                // Percent change badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF22C55E).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = percentChange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = weeklyEarnings,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Previous week: $previousWeek",
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun VehicleStatusCard(
    vehicleName: String,
    plateNumber: String,
    imageUrl: String,
    isVerified: Boolean,
    onChangeClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    // Pulse animation for online indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active Vehicle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
            Text(
                text = "Change",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                modifier = Modifier.clickable { onChangeClick() }
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vehicle image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Vehicle",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(80.dp)
                        .height(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = vehicleName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(pulse)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                    }

                    Text(
                        text = "Plate: $plateNumber",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )

                    if (isVerified) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF22C55E).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "VERIFIED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp,
                                color = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF16A34A),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onBrowseLoadsClick: () -> Unit,
    onMyBidsClick: () -> Unit,
    onCurrentTripClick: () -> Unit,
    pendingBidsCount: Int,
    availableLoadsCount: Int
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Main Action: Browse Loads
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBrowseLoadsClick() },
            shape = RoundedCornerShape(16.dp),
            color = Primary,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Decorative background icon
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 24.dp, y = 32.dp)
                        .rotate(-15f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Browse Loads",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$availableLoadsCount new loads available",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(
                            modifier = Modifier.padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // My Bids
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onMyBidsClick() },
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF97316).copy(alpha = 0.1f)
                        ) {
                            Box(modifier = Modifier.padding(8.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.Gavel,
                                    contentDescription = null,
                                    tint = Color(0xFFF97316)
                                )
                            }
                        }
                        if (pendingBidsCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = pendingBidsCount.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "My Bids",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Pending approval",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            // Current Trip
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCurrentTripClick() },
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF8B5CF6).copy(alpha = 0.1f)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Map,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Current Trip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Track status",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuSection(
    onEarningsHistoryClick: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Menu",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) SurfaceDarkCarrier else SurfaceLightCarrier,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
            )
        ) {
            Column {
                MenuListItem(
                    icon = Icons.Outlined.History,
                    title = "Earnings History",
                    onClick = onEarningsHistoryClick,
                    showDivider = true
                )
                MenuListItem(
                    icon = Icons.Outlined.Settings,
                    title = "Profile Settings",
                    onClick = onProfileSettingsClick,
                    showDivider = true
                )
                MenuListItem(
                    icon = Icons.Outlined.Help,
                    title = "Help & Support",
                    onClick = onHelpSupportClick,
                    showDivider = true
                )
                MenuListItem(
                    icon = Icons.Outlined.Logout,
                    title = "Log Out",
                    onClick = onLogoutClick,
                    showDivider = false,
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun MenuListItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean,
    isDestructive: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) Color(0xFFEF4444)
                           else if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                )
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) Color(0xFFEF4444)
                            else if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }

            if (!isDestructive) {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun CarrierBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Primary else Color(0xFF94A3B8)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Primary else Color(0xFF94A3B8)
        )
    }
}

// Keep old SummaryCard for compatibility
@Composable
fun SummaryCard(title: String, value: String) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierDashboardScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierDashboardScreen()
    }
}

package com.application.movemate.admin

import android.content.Intent
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.ui.theme.*

// Custom colors for Admin Dashboard
private val SurfaceDark = Color(0xFF1C2632)
private val SurfaceDarkHighlight = Color(0xFF233348)
private val BorderDark = Color(0xFF334155)
private val TextSecondaryDarkAdmin = Color(0xFF94A3B8)

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                AdminDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: AdminAuthViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        if (user == null) {
            val intent = Intent(context, AdminLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    Scaffold(
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Column {
                    Divider(
                        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Filled.Dashboard,
                            label = "Home",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.LocalShipping,
                            label = "Fleets",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.Notifications,
                            label = "Alerts",
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.Settings,
                            label = "Settings",
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            AdminHeader(
                onLogout = { viewModel.logout() }
            )

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            // Stats Overview
            StatsOverview()

            // Management Modules
            ManagementModules(context)

            // Recent Activity
            RecentActivity()

            // Bottom spacer
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminHeader(onLogout: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile picture with online indicator
                    Box {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuCCS-OVk9PyxuUEfmPdnfA5zQWFqB3ECuTRAmxh8rPA8s3pO8E8CJWV_7m6Af_vtLPDqN-5YJdJ5T1Vgowe1ujKIdcUeGGDX8tObV6mex-TFOY1JyVpkGh1c00vRRMxsQOj7oGkbv7CiTiABYfBcJby6MKIgi53iXHOojpOlztfebv3YN-8Xk1_gRCp5RyhSLhkMmGfn6QvH2Mex3NJoGxncEsJxRSGky7tc-UcP6tWH6rfrscHyMP4VJav5WK6_C1AjgmRqXm4vxo")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
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
                                .background(Color(0xFF22C55E), CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                        )
                    }

                    Column {
                        Text(
                            text = "MoveMate Admin",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Hello, Alex",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF64748B)
                        )
                    }
                }

                // Logout button
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF64748B)
                    )
                }
            }

            Divider(
                color = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkHighlight else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF94A3B8),
                modifier = Modifier.padding(start = 16.dp)
            )
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "Search users, shipments, IDs...",
                        color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF94A3B8)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}

@Composable
fun StatsOverview() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkTheme) SurfaceDark else Color(0xFFF1F5F9)
            ) {
                Text(
                    text = "Updated 2m ago",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Active Shipments",
                    value = "142",
                    trend = "+5%",
                    trendUp = true,
                    icon = Icons.Outlined.LocalShipping,
                    iconColor = Color(0xFF3B82F6),
                    iconBgColor = if (isDarkTheme) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending Disputes",
                    value = "3",
                    trend = "+1 New",
                    trendUp = false,
                    icon = Icons.Outlined.Gavel,
                    iconColor = Color(0xFFF97316),
                    iconBgColor = if (isDarkTheme) Color(0xFFF97316).copy(alpha = 0.2f) else Color(0xFFF97316).copy(alpha = 0.1f),
                    hasAlert = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Daily Volume",
                    value = "$12.4k",
                    trend = "+12%",
                    trendUp = true,
                    icon = Icons.Outlined.Payments,
                    iconColor = Color(0xFF10B981),
                    iconBgColor = if (isDarkTheme) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Users",
                    value = "892",
                    trend = "+2%",
                    trendUp = true,
                    icon = Icons.Outlined.Group,
                    iconColor = Color(0xFF8B5CF6),
                    iconBgColor = if (isDarkTheme) Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFF8B5CF6).copy(alpha = 0.1f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    trend: String,
    trendUp: Boolean,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    hasAlert: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkHighlight else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 2.dp
    ) {
        Box {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (trendUp) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = "Trending",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = trend,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trendUp) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Alert indicator
            if (hasAlert) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-12).dp, y = 12.dp)
                        .background(Color(0xFFEF4444), CircleShape)
                )
            }
        }
    }
}

@Composable
fun ManagementModules(context: android.content.Context) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Management Modules",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "User\nManagement",
                    icon = Icons.Outlined.ManageAccounts,
                    iconBgColor = Primary.copy(alpha = 0.9f),
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC4Ly7VjyP-oIFE9P0PMJmEFY8seyzYh-R0b8Qpohh3IgCbdX0Mm-FcSKK__gOPK0ThNbBzoDm8E_5LnXqM1UjQE9XXYx-tf0Iz6Sx0SNElZbSPzal1nqWieIC5mbGvuKS7DaAJ77gB5TCc-h5ujKkcyaRhXabp4ia37qmQ0mWrR-8Q1qPaSMwqotZTBSHfsz7lIrW7NzR4a6mgzNZYRXOYL_dBRVTFWVglnSFg_e9FF-TSdxaLqeSmuVu8pGfH5hRyCsGCf_zZ0D8",
                    onClick = { context.startActivity(Intent(context, UserManagementActivity::class.java)) },
                    modifier = Modifier.weight(1f)
                )
                ModuleCard(
                    title = "Shipment\nMonitoring",
                    icon = Icons.Outlined.Radar,
                    iconBgColor = Color(0xFF2563EB).copy(alpha = 0.9f),
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuC0YmZZhFx-b2Sn-Zc758SOiIRyt8YkkME2RCZhcnezctyJ_Qhgv5FC0y1BlA0qpC_C0da_aqh02kG1xodxnmqIiaosyKmHNlZDmNVtjc2Y5dW583ghrgj-To6bEqH9euCEQ5M5TPKwBxJ5QSZ77jbFKD7_oKN-NyaVw6uWNBNnB3hFYw9p69AKf8DpokJhNsL8IzBfsWy3LCJfn1kAeLoDrYNVFN5lvAgYUwQr84zjyuhZuYjrL8YfxZ0kUNfokJS8Hd3aXmYUvj8",
                    onClick = { context.startActivity(Intent(context, ShipmentMonitoringActivity::class.java)) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleCard(
                    title = "Dispute\nResolution",
                    icon = Icons.Outlined.SupportAgent,
                    iconBgColor = Color(0xFFEA580C).copy(alpha = 0.9f),
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCdAtKxhs93kz9W6XbKfnU5VLb_26h-R26NOvfa_cp6OO5geaM_wEm-ezC1zkw2ITlxkTq61C0e0SVLMCn-K4saIFe2uFhde_tDbPXntrlhz0fbqNxv0WFttEkALgQLJIO_DfGDqm5L9D5p-ARXDjbNFiKJMH2BtlaeajD_C9iI_uBcdkO7U6tgPwWQcQqwUIOCmLKVZnmFJx9bVjGoCoOECGzXjMnui3y3w5ssU2ThvawHd17x20_iIqj6P8dLFrWZ2mRe725Dslk",
                    onClick = { context.startActivity(Intent(context, DisputeResolutionActivity::class.java)) },
                    alertCount = 3,
                    modifier = Modifier.weight(1f)
                )
                ModuleCard(
                    title = "Financial\nReports",
                    icon = Icons.Outlined.Analytics,
                    iconBgColor = Color(0xFF059669).copy(alpha = 0.9f),
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB1dp-8jz74oHfydDncx1bpmr2e4wB6UX_c1y0QUXSRVRF2KuzRsOqc8dsU8RKhEnFzthJsVY6zyFDvd5ikbCdgYSMjVaujpqmtwbOTORjO4jTMrHQqy2LF0M127MI8yiJUDSSqhC7qSmlRjR3rSVQx9MVvmGlWh9IVdr-8gQE2PVrKcTji8WchpZADki6EVo8EBa2e45bbW-NV-vW8C080qzBb5BvFjdLVH-TOoeZfYsNJuwSZK7GkZb6XLi5G8yxHctw6fDhKnaU",
                    onClick = { context.startActivity(Intent(context, FinancialDashboardActivity::class.java)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    icon: ImageVector,
    iconBgColor: Color,
    imageUrl: String,
    onClick: () -> Unit,
    alertCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box {
            // Background image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Alert badge
            if (alertCount > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = "$alertCount ALERT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun RecentActivity() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View All",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                modifier = Modifier.clickable { /* View all */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) SurfaceDarkHighlight else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
            )
        ) {
            Column {
                ActivityItem(
                    icon = Icons.Outlined.ReportProblem,
                    iconBgColor = if (isDarkTheme) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFFFEE2E2),
                    iconColor = if (isDarkTheme) Color(0xFFF87171) else Color(0xFFDC2626),
                    title = "New Dispute: Order #8921",
                    subtitle = "Damage reported by Receiver",
                    time = "10m",
                    showDivider = true
                )
                ActivityItem(
                    icon = Icons.Outlined.PersonAdd,
                    iconBgColor = if (isDarkTheme) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFFDBEAFE),
                    iconColor = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB),
                    title = "New Carrier Registration",
                    subtitle = "TransLogistics Ltd. pending approval",
                    time = "45m",
                    showDivider = true
                )
                ActivityItem(
                    icon = Icons.Outlined.AttachMoney,
                    iconBgColor = if (isDarkTheme) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFD1FAE5),
                    iconColor = if (isDarkTheme) Color(0xFF34D399) else Color(0xFF059669),
                    title = "Payout Processed",
                    subtitle = "Weekly batch settlement completed",
                    time = "2h",
                    showDivider = false
                )
            }
        }
    }
}

@Composable
fun ActivityItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    time: String,
    showDivider: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Handle click */ }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = time,
                fontSize = 12.sp,
                color = if (isDarkTheme) TextSecondaryDarkAdmin else Color(0xFF94A3B8)
            )
        }

        if (showDivider) {
            Divider(
                color = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Primary else Color(0xFF64748B),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Primary else Color(0xFF64748B)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    MoveMateTheme {
        AdminDashboardScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AdminDashboardScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        AdminDashboardScreen()
    }
}

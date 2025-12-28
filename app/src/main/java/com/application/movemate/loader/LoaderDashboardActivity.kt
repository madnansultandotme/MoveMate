package com.application.movemate.loader

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
import androidx.compose.ui.draw.alpha
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
import com.application.movemate.RoleSelectionActivity
import com.application.movemate.auth.LoaderAuthViewModel
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.LoaderViewModel

// Custom colors for Loader Dashboard
private val SurfaceDark = Color(0xFF1E293B)
private val SurfaceCard = Color(0xFF233348)
private val BorderDarkLoader = Color(0xFF334155)
private val TextSecondaryDarkLoader = Color(0xFF94A3B8)

class LoaderDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderDashboardScreen(
    viewModel: LoaderViewModel = viewModel(),
    authViewModel: LoaderAuthViewModel = viewModel()
) {
    val loader by viewModel.loader.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.fetchLoader("loader_id_1")
    }

    Scaffold(
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) MaterialTheme.colorScheme.background.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f)
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
                        LoaderBottomNavItem(
                            icon = Icons.Filled.Home,
                            label = "Home",
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        LoaderBottomNavItem(
                            icon = Icons.Outlined.Search,
                            label = "Search",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        LoaderBottomNavItem(
                            icon = Icons.Outlined.Chat,
                            label = "Messages",
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            hasBadge = true
                        )
                        LoaderBottomNavItem(
                            icon = Icons.Outlined.Person,
                            label = "Profile",
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                    }
                    // Safe area spacer
                    Spacer(modifier = Modifier.height(24.dp))
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
            // Top App Bar / Header
            LoaderHeader()

            // Main Content
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Stats Overview Grid
                StatsOverviewGrid()

                // CTA Section
                CTASection(context)

                // Quick Actions
                QuickActionsSection(context)

                // Recent Activity
                RecentActivitySection()

                // Logout Button
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            authViewModel.logout()
                            val intent = Intent(context, RoleSelectionActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign Out",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bottom spacer
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun LoaderHeader() {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) MaterialTheme.colorScheme.background.copy(alpha = 0.95f) else MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuAeTdNZrz0hC7OT6S44OsS31f55j1XAmuPk1cHhn4iQkm8uTtB6jb9UDY2qpPgeL-ll2KIqBFNM-gwDqKNrP9UyCs8cz1SWUAMSTHFfmErE0t9L5F43Z4Xuj6z-k9W8B4DXRii-b6e6Dp8dAyRGUN0JrrGzNeAsJq9LwHH-gCV7zx3sHqvMEC401q1chy9Y7rLzbyAA-YrTmheDqGatj8bj277pqsmlDwPTzTQnQEqx2TeZNzQAlhJGfkXUTmGcnDHsj6cibjfHEcI")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                        )
                        // Online indicator
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color(0xFF22C55E), CircleShape)
                                .border(
                                    2.dp,
                                    if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White,
                                    CircleShape
                                )
                        )
                    }

                    Column {
                        Text(
                            text = "Welcome back,",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B)
                        )
                        Text(
                            text = "Alex Johnson",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Notifications button
                Box {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = if (isDarkTheme) SurfaceCard else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = if (isDarkTheme) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                    // Notification badge
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                }
            }

            Divider(
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun StatsOverviewGrid() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Active Shipments
            LoaderStatCard(
                title = "Active",
                subtitle = "Shipments",
                value = "4",
                icon = Icons.Outlined.LocalShipping,
                iconColor = Primary,
                modifier = Modifier.weight(1f)
            )
            // Bids
            LoaderStatCard(
                title = "Bids",
                subtitle = "Action Needed",
                value = "12",
                icon = Icons.Outlined.Gavel,
                iconColor = Color(0xFFF97316),
                hasBadge = true,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Completed
            LoaderStatCardSmall(
                title = "Completed",
                value = "28",
                icon = Icons.Filled.CheckCircle,
                iconColor = Color(0xFF22C55E),
                modifier = Modifier.weight(1f)
            )
            // Month Spend
            LoaderStatCardSmall(
                title = "Month Spend",
                value = "$12k",
                icon = Icons.Outlined.Payments,
                iconColor = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF94A3B8),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LoaderStatCard(
    title: String,
    subtitle: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    hasBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceCard else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B)
                )
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = value,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (hasBadge) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFF97316), CircleShape)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun LoaderStatCardSmall(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceCard else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun CTASection(context: android.content.Context) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Color(0xFF2563EB))
                    ),
                    RoundedCornerShape(20.dp)
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-16).dp, y = 16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        text = "Move cargo today?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Post a new load to get competitive bids from top carriers instantly.",
                        fontSize = 14.sp,
                        color = Color(0xFFBFDBFE),
                        lineHeight = 20.sp
                    )
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, CreateShipmentActivity::class.java))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Create",
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create New Shipment",
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(context: android.content.Context) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Text(
            text = "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionItem(
                icon = Icons.Outlined.Inventory2,
                label = "My\nLoads",
                iconColor = Primary,
                onClick = { context.startActivity(Intent(context, LoaderMyShipmentsActivity::class.java)) }
            )
            QuickActionItem(
                icon = Icons.Outlined.RequestQuote,
                label = "Incoming\nBids",
                iconColor = Color(0xFFF97316),
                badgeCount = 12,
                onClick = { context.startActivity(Intent(context, LoaderManageBidsActivity::class.java)) }
            )
            QuickActionItem(
                icon = Icons.Outlined.Settings,
                label = "Profile\nSettings",
                iconColor = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B),
                onClick = { context.startActivity(Intent(context, LoaderProfileSettingsActivity::class.java)) }
            )
            QuickActionItem(
                icon = Icons.Outlined.SupportAgent,
                label = "Help &\nSupport",
                iconColor = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B),
                onClick = { /* Help */ }
            )
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceCard else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0)
                ),
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            if (badgeCount > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp),
                    shape = CircleShape,
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun RecentActivitySection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                modifier = Modifier.clickable { /* View all */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RecentActivityItem(
                icon = Icons.Outlined.Chair,
                iconBgColor = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFEFF6FF),
                iconColor = if (isDarkTheme) Color(0xFF60A5FA) else Primary,
                title = "Furniture to Seattle",
                status = "Bidding Open",
                statusColor = Color.White,
                statusBgColor = Color(0xFF22C55E).copy(alpha = 0.9f),
                orderId = "#4092"
            )
            RecentActivityItem(
                icon = Icons.Outlined.ViewInAr,
                iconBgColor = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFFFF7ED),
                iconColor = if (isDarkTheme) Color(0xFFFB923C) else Color(0xFFF97316),
                title = "Pallets to Austin",
                status = "In Transit",
                statusColor = if (isDarkTheme) Color(0xFFBFDBFE) else Color(0xFF2563EB),
                statusBgColor = if (isDarkTheme) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFDBEAFE),
                orderId = "#4088"
            )
            RecentActivityItem(
                icon = Icons.Outlined.CheckBox,
                iconBgColor = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                iconColor = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B),
                title = "Electronics to NY",
                status = "Delivered",
                statusColor = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                statusBgColor = if (isDarkTheme) BorderDarkLoader else Color(0xFFF1F5F9),
                orderId = "#4010",
                isCompleted = true
            )
        }
    }
}

@Composable
fun RecentActivityItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    status: String,
    statusColor: Color,
    statusBgColor: Color,
    orderId: String,
    isCompleted: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isCompleted) 0.75f else 1f),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceCard else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkLoader else Color(0xFFE2E8F0)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Navigate to details */ }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusBgColor
                    ) {
                        Text(
                            text = status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "• $orderId",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF64748B)
                    )
                }
            }

            // Arrow
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "View",
                tint = if (isDarkTheme) TextSecondaryDarkLoader else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun LoaderBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasBadge: Boolean = false
) {
    Box {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(2.dp)
                        .offset(y = (-8).dp)
                        .background(Primary, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Primary else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Primary else Color(0xFF94A3B8)
            )
        }
        if (hasBadge) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 12.dp)
                    .background(Color(0xFFEF4444), CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.background, CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoaderDashboardScreenPreview() {
    MoveMateTheme {
        LoaderDashboardScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoaderDashboardScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderDashboardScreen()
    }
}

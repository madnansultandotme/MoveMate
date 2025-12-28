package com.application.movemate.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.ShipmentViewModel

// Custom colors
private val SurfaceDark = Color(0xFF1E2530)
private val SurfaceDarkLighter = Color(0xFF2A3441)

// Shipment status enum
enum class ShipmentStatus {
    OPEN,
    ASSIGNED,
    IN_TRANSIT,
    DELAYED,
    DELIVERED
}

// Data class for shipment display
data class ShipmentDisplayItem(
    val id: String,
    val status: ShipmentStatus,
    val price: Double,
    val pickupLocation: String,
    val dropoffLocation: String,
    val updatedTime: String? = null,
    val eta: String? = null,
    val progress: Float? = null,
    val loaderAvatar: String? = null,
    val carrierAvatar: String? = null,
    val carrierName: String? = null,
    val delayReason: String? = null,
    val postedTime: String? = null
)

class ShipmentMonitoringActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                ShipmentMonitoringScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentMonitoringScreen(viewModel: ShipmentViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Shipments") }
    var selectedView by remember { mutableStateOf("List") }
    var selectedNavItem by remember { mutableStateOf("Shipments") }

    // Sample data
    val sampleShipments = remember {
        listOf(
            ShipmentDisplayItem(
                id = "MM-8392",
                status = ShipmentStatus.IN_TRANSIT,
                price = 450.00,
                pickupLocation = "123 Industrial Park, Brooklyn, NY",
                dropoffLocation = "456 Retail Way, Paramus, NJ",
                updatedTime = "Updated 12m ago",
                eta = "ETA 2h 15m",
                progress = 0.9f,
                loaderAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuBynX4_kJNvUowTXf881SJHsHhBwxj4bub8l_6xn659airc05jAHBSh_b8uEaqk0BQiZcjJal_8sdoLcZIl4rJTrdO_IJkh2_VccbhJrHlVwCNgwH0kslRNz3fRvoz4MEdJYe4ABqgyj9TIXCZlitRWr2ZrILUx-BpMGWLMnTi8n19U85ur73SSG1oVOzrz9skZx7FT-izPt6qsMYHgIpu6yYvkwQGio2KLV8CpP_45rZQCZYreXQz3qy0UqWD6s1Tl5IyG71TePtc",
                carrierAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDkPSc2nYu5Cai3TrD8TuX6VTeNyPk9qGecffCIrlqyrqGOOMhdJBKFZu65tum1_BwtZ9PGm8c5RemH_ZUvsfrsTS1subwiZUbl8MWF8VVksHAzMiDIosutFf_a-BtazAKzEe9vK6SjF2aEXPSL4lDCq3-bR6-sswr4MelaviyHad99xIseu5Att0AfQ6c93uV5SE7tQZLRZTeB_krLicVItmdrPNCLtGKOzxprAKXYxK2b5DC4APYtL7ZgzRaZTepAT92SjwaRRV8"
            ),
            ShipmentDisplayItem(
                id = "MM-9102",
                status = ShipmentStatus.DELAYED,
                price = 1200.00,
                pickupLocation = "Port of Los Angeles, CA",
                dropoffLocation = "Distribution Center 4, NV",
                delayReason = "Stopped for 45m near Exit 12",
                loaderAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuD9TQqM4FkQHc1hr0WlyGPJKdpB0uu6j7dpAW12dpNhuZ8uXqycYMmK5mmx5cWrt2Yptb5UXr8SgfrCmp0BewV6f6a9yLRUcjINUBHD4JDjWlzNKyvirFIrD5nceuWFRHrwJOqRF4pwejdFm-oR5Y5S2GZCmTphbRua2I_XLGlbnitDk-u6KDZ2ic-MaAUEHKltYzeNXo-sVKnosBHJ_UslaBRMUjlrGnUV-abmBtd1aZ1VyNHAG03e1uEFOy39sjiVUTPZ4JiA2yk",
                carrierName = "Global Logistics Co."
            ),
            ShipmentDisplayItem(
                id = "MM-8421",
                status = ShipmentStatus.OPEN,
                price = 320.00,
                pickupLocation = "Miami, FL",
                dropoffLocation = "Orlando, FL",
                postedTime = "Posted 2h ago"
            )
        )
    }

    val filterOptions = listOf("All Shipments", "Open", "Assigned", "In-Transit", "Delivered")

    Scaffold(
        bottomBar = {
            AdminBottomNavigation(
                selectedItem = selectedNavItem,
                onItemSelected = { selectedNavItem = it }
            )
        },
        floatingActionButton = {
            // Empty - FAB is part of bottom nav design
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                ShipmentMonitoringHeader(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 16.dp)
                ) {
                    // Stats Overview
                    item {
                        StatsOverview()
                    }

                    // Controls Section
                    item {
                        ControlsSection(
                            selectedView = selectedView,
                            onViewSelected = { selectedView = it },
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it },
                            filterOptions = filterOptions
                        )
                    }

                    // Shipment Cards
                    items(sampleShipments) { shipment ->
                        ShipmentCard(
                            shipment = shipment,
                            onDetailsClick = { },
                            onActionClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShipmentMonitoringHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f)
    ) {
        Column {
            // Title Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shipment Monitoring",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notifications
                    Box {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        // Red dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFEF4444), CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                                .border(2.dp, if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8), CircleShape)
                        )
                    }

                    // Profile Avatar
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuCzvQTa6n2WjHBmWwnmZN535x9jso0Fn6JzcjTm0WKUbitBYNdiRtU4Pro0YbenyoO0Qy05f5fZHOGUtvZZzx7pgv-WDIQrov5QBcyfBib3Wlfzif5yweT9Zr6_S8Vo-lHnby9PQVX_84L35SijH6N8Fq9VtF3CECFsLRCMPQXwRXZSXnVrF-ijagj6slogsG48_2vYFr0NcGpNt0E4vjnzlpfhVsl97-fPydbDafTEOWOSb5tMgvR0ErMWjMzQFhoPhnEA94hKCf0")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Admin Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                }
            }

            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkTheme) SurfaceDark else Color.White,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Search ID, Loader, or Carrier...",
                                fontSize = 14.sp,
                                color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = searchText,
                            onValueChange = onSearchTextChange,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            HorizontalDivider(
                color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun StatsOverview() {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Shipments Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "42",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+5%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }

        // Delayed Shipments Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DELAYED",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "3",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+10%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlsSection(
    selectedView: String,
    onViewSelected: (String) -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    filterOptions: List<String>
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // View Toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = if (isDarkTheme) SurfaceDark else Color(0xFFE2E8F0)
        ) {
            Row(
                modifier = Modifier.padding(4.dp)
            ) {
                listOf("List" to Icons.Filled.FormatListBulleted, "Map" to Icons.Filled.Map).forEach { (label, icon) ->
                    val isSelected = label == selectedView
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onViewSelected(label) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) {
                            if (isDarkTheme) SurfaceDarkLighter else Color.White
                        } else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) {
                                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) {
                                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                } else {
                                    if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { option ->
                val isSelected = option == selectedFilter
                Surface(
                    modifier = Modifier.clickable { onFilterSelected(option) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Primary else {
                        if (isDarkTheme) SurfaceDark else Color.White
                    },
                    shadowElevation = if (isSelected) 4.dp else 0.dp,
                    border = if (!isSelected) {
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                        )
                    } else null
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else {
                            if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ShipmentCard(
    shipment: ShipmentDisplayItem,
    onDetailsClick: () -> Unit,
    onActionClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val (statusColor, statusBgColor, statusText) = when (shipment.status) {
        ShipmentStatus.IN_TRANSIT -> Triple(
            Color(0xFF3B82F6),
            Color(0xFF3B82F6).copy(alpha = 0.1f),
            "In-Transit"
        )
        ShipmentStatus.DELAYED -> Triple(
            Color(0xFFEF4444),
            Color(0xFFEF4444).copy(alpha = 0.1f),
            "Delayed"
        )
        ShipmentStatus.OPEN -> Triple(
            Color(0xFFF59E0B),
            Color(0xFFF59E0B).copy(alpha = 0.1f),
            "Open"
        )
        ShipmentStatus.ASSIGNED -> Triple(
            Color(0xFF8B5CF6),
            Color(0xFF8B5CF6).copy(alpha = 0.1f),
            "Assigned"
        )
        ShipmentStatus.DELIVERED -> Triple(
            Color(0xFF10B981),
            Color(0xFF10B981).copy(alpha = 0.1f),
            "Delivered"
        )
    }

    val cardAlpha = if (shipment.status == ShipmentStatus.OPEN) 0.8f else 1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(cardAlpha),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDark else Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "#${shipment.id}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isDarkTheme) statusColor.copy(alpha = 0.2f) else statusBgColor
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (shipment.status == ShipmentStatus.DELAYED) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Text(
                                    text = statusText.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) {
                                        when (shipment.status) {
                                            ShipmentStatus.DELAYED -> Color(0xFFFCA5A5)
                                            ShipmentStatus.IN_TRANSIT -> Color(0xFF93C5FD)
                                            ShipmentStatus.OPEN -> Color(0xFFFCD34D)
                                            else -> statusColor
                                        }
                                    } else statusColor,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when {
                        shipment.delayReason != null -> {
                            Text(
                                text = shipment.delayReason,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) Color(0xFFFCA5A5) else Color(0xFFEF4444)
                            )
                        }
                        shipment.updatedTime != null && shipment.eta != null -> {
                            Text(
                                text = "${shipment.updatedTime} • ${shipment.eta}",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                        shipment.postedTime != null -> {
                            Text(
                                text = shipment.postedTime,
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Text(
                    text = "$${String.format("%.2f", shipment.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route Section
            if (shipment.status == ShipmentStatus.OPEN) {
                // Simplified route for Open status
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDarkTheme) Color.Black.copy(alpha = 0.2f) else Color(0xFFF8FAFC)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "PICKUP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = shipment.pickupLocation,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF374151)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "DROP-OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = shipment.dropoffLocation,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF374151)
                            )
                        }
                    }
                }
            } else {
                // Full route with timeline
                Box(modifier = Modifier.padding(start = 8.dp)) {
                    // Connector Line
                    Box(
                        modifier = Modifier
                            .padding(start = 3.dp, top = 8.dp, bottom = 8.dp)
                            .width(2.dp)
                            .height(48.dp)
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Pickup
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        4.dp,
                                        if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1),
                                        CircleShape
                                    )
                                    .background(if (isDarkTheme) SurfaceDark else Color.White)
                            )
                            Column {
                                Text(
                                    text = "PICKUP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = shipment.pickupLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                                )
                            }
                        }

                        // Dropoff
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Primary, CircleShape)
                                    .background(Primary)
                                    .shadow(8.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                            Column {
                                Text(
                                    text = "DROP-OFF",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = shipment.dropoffLocation,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }

            // Progress Bar (for In-Transit)
            if (shipment.progress != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                        Text(
                            text = "${(shipment.progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { shipment.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Primary,
                        trackColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            HorizontalDivider(
                color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (shipment.status == ShipmentStatus.OPEN) {
                // Open status actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDetailsClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Text(
                            text = "Edit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onActionClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                            contentColor = if (isDarkTheme) Color(0xFF0F172A) else Color.White
                        )
                    ) {
                        Text(
                            text = "Assign Carrier",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                // Other status actions with avatars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatars or carrier info
                    if (shipment.loaderAvatar != null && shipment.carrierAvatar != null) {
                        Row {
                            AsyncImage(
                                model = shipment.loaderAvatar,
                                contentDescription = "Loader",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isDarkTheme) SurfaceDark else Color.White, CircleShape)
                            )
                            AsyncImage(
                                model = shipment.carrierAvatar,
                                contentDescription = "Carrier",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .offset(x = (-12).dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (isDarkTheme) SurfaceDark else Color.White, CircleShape)
                            )
                        }
                    } else if (shipment.loaderAvatar != null && shipment.carrierName != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = shipment.loaderAvatar,
                                contentDescription = "Loader",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                            Column {
                                Text(
                                    text = shipment.carrierName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF374151)
                                )
                                Text(
                                    text = "Carrier",
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = onDetailsClick) {
                            Text(
                                text = if (shipment.status == ShipmentStatus.DELAYED) "Log" else "Details",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                            )
                        }
                        Button(
                            onClick = onActionClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (shipment.status == ShipmentStatus.DELAYED) {
                                    Color(0xFFEF4444)
                                } else Primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            if (shipment.status == ShipmentStatus.DELAYED) {
                                Icon(
                                    imageVector = Icons.Filled.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = if (shipment.status == ShipmentStatus.DELAYED) "Intervene" else "Track Live",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminBottomNavigation(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple("Home", Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
                Triple("Shipments", Icons.Outlined.LocalShipping, Icons.Filled.LocalShipping)
            )

            navItems.forEach { (label, outlinedIcon, filledIcon) ->
                val isSelected = label == selectedItem
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onItemSelected(label) }
                ) {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = label,
                        tint = if (isSelected) Primary else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Primary else Color(0xFF94A3B8)
                    )
                }
            }

            // FAB in center
            Box(
                modifier = Modifier.offset(y = (-24).dp)
            ) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = Primary,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            val navItems2 = listOf(
                Triple("Users", Icons.Outlined.Group, Icons.Filled.Group),
                Triple("Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
            )

            navItems2.forEach { (label, outlinedIcon, filledIcon) ->
                val isSelected = label == selectedItem
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onItemSelected(label) }
                ) {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = label,
                        tint = if (isSelected) Primary else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Primary else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

// Keep legacy composables for compatibility
@Composable
fun ShipmentListItem(shipment: Shipment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("ID: ${shipment.id}", style = MaterialTheme.typography.titleMedium)
            Text("Loader: ${shipment.loaderName} | Carrier: ${shipment.carrierName ?: "Not assigned"}")
            Text("Route: ${shipment.pickupAddress} -> ${shipment.deliveryAddress}")
            Text("Status: ${shipment.status.name}", style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(onClick = { /* View Shipment */ }) {
            Text("View")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShipmentMonitoringScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        ShipmentMonitoringScreen()
    }
}

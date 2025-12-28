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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.AvailableLoadsViewModel
import java.text.SimpleDateFormat
import java.util.*

// Custom colors
private val CardDark = Color(0xFF192433)
private val TextSecondary = Color(0xFF92A9C9)

// Load status enum
enum class LoadStatus {
    BIDDING_OPEN,
    URGENT,
    PLANNING
}

// Data class for load display
data class LoadDisplayItem(
    val id: String,
    val status: LoadStatus,
    val priceRange: String,
    val priceLabel: String,
    val pickupLocation: String,
    val pickupTime: String,
    val dropoffLocation: String,
    val dropoffTime: String,
    val cargoType: String,
    val cargoIcon: ImageVector,
    val weight: String,
    val distance: String
)

class AvailableLoadsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                AvailableLoadsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableLoadsScreen(viewModel: AvailableLoadsViewModel = viewModel()) {
    val loads by viewModel.loads.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedFilter by remember { mutableStateOf("All Loads") }

    // Sample data for display
    val sampleLoads = remember {
        listOf(
            LoadDisplayItem(
                id = "1",
                status = LoadStatus.BIDDING_OPEN,
                priceRange = "$2,400 - $2,600",
                priceLabel = "Est. Price",
                pickupLocation = "San Francisco, CA",
                pickupTime = "Sep 24 • 08:00 AM - 12:00 PM",
                dropoffLocation = "Austin, TX",
                dropoffTime = "Sep 26 • By 05:00 PM",
                cargoType = "Pallets (Dry)",
                cargoIcon = Icons.Outlined.Inventory2,
                weight = "12,000 lbs",
                distance = "1,750 mi"
            ),
            LoadDisplayItem(
                id = "2",
                status = LoadStatus.URGENT,
                priceRange = "$450 Fixed",
                priceLabel = "Buy it Now",
                pickupLocation = "Seattle, WA",
                pickupTime = "Today • ASAP",
                dropoffLocation = "Portland, OR",
                dropoffTime = "Tomorrow • By 10:00 AM",
                cargoType = "Furniture",
                cargoIcon = Icons.Outlined.Chair,
                weight = "2,500 lbs",
                distance = "173 mi"
            ),
            LoadDisplayItem(
                id = "3",
                status = LoadStatus.PLANNING,
                priceRange = "$3,200",
                priceLabel = "Starting Bid",
                pickupLocation = "Miami, FL",
                pickupTime = "Oct 02 • Flexible",
                dropoffLocation = "New York, NY",
                dropoffTime = "Oct 05 • Flexible",
                cargoType = "Refrigerated",
                cargoIcon = Icons.Outlined.AcUnit,
                weight = "18,000 lbs",
                distance = "1,280 mi"
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAvailableLoads()
    }

    Scaffold(
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) Color(0xFF101822) else Color.White
            ) {
                Column {
                    HorizontalDivider(
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE5E7EB),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoadsBottomNavItem(
                            icon = Icons.Filled.LocalShipping,
                            label = "Loads",
                            isSelected = true,
                            onClick = { }
                        )
                        LoadsBottomNavItem(
                            icon = Icons.Outlined.Gavel,
                            label = "My Bids",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, CarrierMyBidsActivity::class.java)) }
                        )
                        LoadsBottomNavItem(
                            icon = Icons.Outlined.Map,
                            label = "Active",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, MyShipmentsActivity::class.java)) }
                        )
                        LoadsBottomNavItem(
                            icon = Icons.Outlined.Person,
                            label = "Profile",
                            isSelected = false,
                            onClick = { context.startActivity(Intent(context, CarrierProfileSettingsActivity::class.java)) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to map view */ },
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = "View on Map",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
        ) {
            // Header
            AvailableLoadsHeader(
                onSearchClick = { },
                onNotificationClick = { }
            )

            // Filter Chips
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                if (isLoading) {
                    // Skeleton loading
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(3) {
                            LoadCardSkeleton()
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sampleLoads) { load ->
                            LoadCard(
                                load = load,
                                onClick = {
                                    val intent = Intent(context, LoadDetailsActivity::class.java)
                                    intent.putExtra("SHIPMENT_ID", load.id)
                                    context.startActivity(intent)
                                }
                            )
                        }
                        // Add skeleton for loading more
                        item {
                            LoadCardSkeleton()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableLoadsHeader(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Loads",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search button
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onSearchClick() },
                        shape = CircleShape,
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = if (isDarkTheme) Color.White else Color(0xFF4B5563),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Notifications button with badge
                    Box {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onNotificationClick() },
                            shape = CircleShape,
                            color = Color.Transparent
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (isDarkTheme) Color.White else Color(0xFF4B5563),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        // Red notification badge
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-6).dp, y = 8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .border(
                                    2.dp,
                                    if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
                                    CircleShape
                                )
                        )
                    }
                }
            }
            HorizontalDivider(
                color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE5E7EB),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val filters = listOf("All Loads", "Sort by Price", "Filter by Date", "Cargo Type")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { onFilterSelected(filter) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) Primary else if (isDarkTheme) CardDark else Color.White,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE5E7EB)
                        ) else null,
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White
                                        else if (isDarkTheme) Color.White else Color(0xFF374151)
                            )
                            if (filter != "All Loads") {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White
                                           else if (isDarkTheme) TextSecondary else Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(
                color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE5E7EB),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun LoadCard(
    load: LoadDisplayItem,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val (statusColor, statusBgColor, statusText) = when (load.status) {
        LoadStatus.BIDDING_OPEN -> Triple(
            Color(0xFF16A34A),
            Color(0xFF22C55E).copy(alpha = 0.1f),
            "Bidding Open"
        )
        LoadStatus.URGENT -> Triple(
            Color(0xFFEA580C),
            Color(0xFFF97316).copy(alpha = 0.1f),
            "Urgent"
        )
        LoadStatus.PLANNING -> Triple(
            Color(0xFF6B7280),
            Color(0xFF6B7280).copy(alpha = 0.1f),
            "Planning Phase"
        )
    }

    val cardAlpha = if (load.status == LoadStatus.PLANNING) 0.8f else 1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF3F4F6)
        )
    ) {
        Box {
            // Left color strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusBgColor,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            statusColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (load.status == LoadStatus.URGENT) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) statusColor.copy(alpha = 0.9f) else statusColor
                            )
                        }
                    }

                    // Price
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = load.priceRange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = load.priceLabel,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Route Timeline
                Box {
                    // Vertical line - positioned to align with center of circles
                    Box(
                        modifier = Modifier
                            .padding(start = 7.dp, top = 8.dp, bottom = 24.dp)
                            .width(2.dp)
                            .height(48.dp)
                            .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE5E7EB))
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        // Pickup
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Green pickup circle - 16dp with 3px border
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (isDarkTheme) CardDark else Color(0xFFF6F7F8))
                                    .border(3.dp, Color(0xFF22C55E), CircleShape)
                            )
                            Column {
                                Text(
                                    text = load.pickupLocation,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                    lineHeight = 20.sp
                                )
                                Text(
                                    text = load.pickupTime,
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Dropoff
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Red dropoff circle - 16dp with 3px border
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (isDarkTheme) CardDark else Color(0xFFF6F7F8))
                                    .border(3.dp, Color(0xFFEF4444), CircleShape)
                            )
                            Column {
                                Text(
                                    text = load.dropoffLocation,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                    lineHeight = 20.sp
                                )
                                Text(
                                    text = load.dropoffTime,
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Divider
                HorizontalDivider(
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF3F4F6),
                    thickness = 1.dp
                )

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cargo
                    LoadStatItem(
                        icon = load.cargoIcon,
                        label = "Cargo",
                        value = load.cargoType,
                        showBorder = false
                    )

                    // Weight
                    LoadStatItem(
                        icon = Icons.Outlined.Scale,
                        label = "Weight",
                        value = load.weight,
                        showBorder = true
                    )

                    // Distance
                    LoadStatItem(
                        icon = Icons.Outlined.Route,
                        label = "Distance",
                        value = load.distance,
                        showBorder = true
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    showBorder: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .then(
                if (showBorder) Modifier
                    .padding(start = 12.dp)
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(0.dp)
                    )
                else Modifier
            ),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        if (showBorder) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF3F4F6))
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun LoadCardSkeleton() {
    val isDarkTheme = isSystemInDarkTheme()

    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val skeletonColor = if (isDarkTheme) Color.White.copy(alpha = 0.05f * alpha * 3)
                        else Color(0xFFE5E7EB).copy(alpha = alpha)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF3F4F6)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(skeletonColor)
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(skeletonColor)
                )
            }

            // Route
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(skeletonColor)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(32.dp)
                            .background(skeletonColor)
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(skeletonColor)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(skeletonColor)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.66f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(skeletonColor)
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(skeletonColor)
            )

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(skeletonColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadsBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Primary else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Primary else Color(0xFF9CA3AF)
        )
    }
}

// Keep old LoadCard for compatibility
@Composable
fun LoadCard(shipment: Shipment) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, LoadDetailsActivity::class.java)
                intent.putExtra("SHIPMENT_ID", shipment.id)
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        shipment.goodsType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "Rs. ${shipment.estimatedPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pickup", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(shipment.pickupAddress, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Delivery", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(shipment.deliveryAddress, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Weight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("${shipment.weight} kg", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Vehicle", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(shipment.vehicleType, style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Posted", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(dateFormat.format(Date(shipment.createdAt)), style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Posted by: ${shipment.loaderName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val intent = Intent(context, LoadDetailsActivity::class.java)
                    intent.putExtra("SHIPMENT_ID", shipment.id)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Details & Place Bid")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AvailableLoadsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        AvailableLoadsScreen()
    }
}

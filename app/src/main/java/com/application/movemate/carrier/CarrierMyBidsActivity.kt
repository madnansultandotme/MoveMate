package com.application.movemate.carrier

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.BidStatus
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.CarrierMyBidsViewModel
import java.text.SimpleDateFormat
import java.util.*

// Custom colors
private val CardDark = Color(0xFF192433)
private val CardDarkHighlight = Color(0xFF233348)
private val TextSecondary = Color(0xFF94A3B8)

// Bid status enum for display
enum class BidDisplayStatus {
    ACCEPTED,
    OUTBID,
    PENDING,
    REJECTED
}

// Data class for bid display
data class BidDisplayItem(
    val id: String,
    val status: BidDisplayStatus,
    val originCity: String,
    val destinationCity: String,
    val loadId: String,
    val pickupDate: String,
    val bidAmount: String,
    val lowestBid: String? = null,
    val previousBid: String? = null,
    val equipment: String,
    val equipmentIcon: ImageVector,
    val submittedTime: String? = null
)

class CarrierMyBidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                CarrierMyBidsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierMyBidsScreen(viewModel: CarrierMyBidsViewModel = viewModel()) {
    val bids by viewModel.bids.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedFilter by remember { mutableStateOf("All") }

    // Sample data for display
    val sampleBids = remember {
        listOf(
            BidDisplayItem(
                id = "1",
                status = BidDisplayStatus.ACCEPTED,
                originCity = "Atlanta, GA",
                destinationCity = "Miami, FL",
                loadId = "#L-9382",
                pickupDate = "Pickup Sep 12",
                bidAmount = "$1,200",
                equipment = "53' Dry Van",
                equipmentIcon = Icons.Outlined.LocalShipping
            ),
            BidDisplayItem(
                id = "2",
                status = BidDisplayStatus.OUTBID,
                originCity = "Dallas, TX",
                destinationCity = "Houston, TX",
                loadId = "#L-4421",
                pickupDate = "Pickup Sep 14",
                bidAmount = "$650",
                previousBid = "$700",
                lowestBid = "$600",
                equipment = "Flatbed",
                equipmentIcon = Icons.Outlined.ViewAgenda
            ),
            BidDisplayItem(
                id = "3",
                status = BidDisplayStatus.PENDING,
                originCity = "Chicago, IL",
                destinationCity = "Detroit, MI",
                loadId = "#L-1102",
                pickupDate = "Pickup Sep 15",
                bidAmount = "$900",
                equipment = "Reefer",
                equipmentIcon = Icons.Outlined.AcUnit,
                submittedTime = "Submitted 2h ago"
            ),
            BidDisplayItem(
                id = "4",
                status = BidDisplayStatus.REJECTED,
                originCity = "Phoenix, AZ",
                destinationCity = "Denver, CO",
                loadId = "#L-5529",
                pickupDate = "Aug 30",
                bidAmount = "$1,150",
                equipment = "Dry Van",
                equipmentIcon = Icons.Outlined.LocalShipping
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMyBids()
    }

    Scaffold(
        bottomBar = {
            MyBidsBottomNavigation(
                onFindLoadsClick = {
                    context.startActivity(Intent(context, AvailableLoadsActivity::class.java))
                },
                onMyBidsClick = { },
                onProfileClick = {
                    context.startActivity(Intent(context, CarrierProfileSettingsActivity::class.java))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
        ) {
            // Header
            MyBidsHeader(
                onBackClick = { (context as? ComponentActivity)?.finish() }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Stats Overview
                item {
                    StatsOverviewSection()
                }

                // Filter Chips
                item {
                    FilterChipsSection(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }

                // Bids List
                items(sampleBids) { bid ->
                    BidCard(
                        bid = bid,
                        onRebidClick = { },
                        onViewDetailsClick = { }
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun MyBidsHeader(onBackClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f)
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
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .offset(x = (-8).dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                    )
                }

                Text(
                    text = "My Bids",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            HorizontalDivider(
                color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun StatsOverviewSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active Bids Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) CardDarkHighlight else Color.White,
            shadowElevation = 2.dp,
            border = if (!isDarkTheme) androidx.compose.foundation.BorderStroke(
                1.dp, Color(0xFFF3F4F6)
            ) else null
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ACTIVE BIDS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "5",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF22C55E).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "+2",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Won This Week Card
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) CardDarkHighlight else Color.White,
            shadowElevation = 2.dp,
            border = if (!isDarkTheme) androidx.compose.foundation.BorderStroke(
                1.dp, Color(0xFFF3F4F6)
            ) else null
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Payments,
                        contentDescription = null,
                        tint = Color(0xFF22C55E),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "WON THIS WEEK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$4.5k",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF22C55E).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "+$1.2k",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val filters = listOf("All", "Pending", "Accepted", "Outbid", "History")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                modifier = Modifier
                    .height(40.dp)
                    .clickable { onFilterSelected(filter) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Primary else if (isDarkTheme) CardDarkHighlight else Color.White,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) Color.Transparent else Color(0xFFE5E7EB)
                ) else null,
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White
                                else if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                    )
                }
            }
        }
    }
}

@Composable
private fun BidCard(
    bid: BidDisplayItem,
    onRebidClick: () -> Unit,
    onViewDetailsClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val (statusColor, statusBgColor, statusText, statusIcon) = when (bid.status) {
        BidDisplayStatus.ACCEPTED -> listOf(
            Color(0xFF16A34A),
            Color(0xFF22C55E).copy(alpha = 0.1f),
            "Accepted",
            Icons.Filled.CheckCircle
        )
        BidDisplayStatus.OUTBID -> listOf(
            Color(0xFFEA580C),
            Color(0xFFF97316).copy(alpha = 0.1f),
            "Outbid",
            Icons.Filled.Warning
        )
        BidDisplayStatus.PENDING -> listOf(
            if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF1F5F9),
            "Pending",
            Icons.Filled.HourglassEmpty
        )
        BidDisplayStatus.REJECTED -> listOf(
            Color(0xFFDC2626),
            Color(0xFFEF4444).copy(alpha = 0.1f),
            "Rejected",
            Icons.Filled.Cancel
        )
    }

    val cardAlpha = if (bid.status == BidDisplayStatus.REJECTED) 0.75f else 1f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(cardAlpha)
            .clickable { onViewDetailsClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1F2937).copy(alpha = 0.6f) else Color(0xFFF3F4F6)
        )
    ) {
        Box {
            // Right color strip for accepted and outbid
            if (bid.status == BidDisplayStatus.ACCEPTED || bid.status == BidDisplayStatus.OUTBID) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(statusColor as Color)
                        .align(Alignment.CenterEnd)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Equipment Icon
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = bid.equipmentIcon,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF6B7280),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            // Route
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = bid.originCity,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bid.status == BidDisplayStatus.REJECTED) {
                                        if (isDarkTheme) Color.White else Color(0xFF475569)
                                    } else {
                                        if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                    }
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = bid.destinationCity,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bid.status == BidDisplayStatus.REJECTED) {
                                        if (isDarkTheme) Color.White else Color(0xFF475569)
                                    } else {
                                        if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                    }
                                )
                            }
                            Text(
                                text = "ID: ${bid.loadId} • ${bid.pickupDate}",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF64748B)
                            )
                        }
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = statusBgColor as Color,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            (statusColor as Color).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = statusIcon as ImageVector,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = statusText as String,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bid Details Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 52.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Bid Amount Section
                    Column {
                        Text(
                            text = if (bid.status == BidDisplayStatus.REJECTED) "Final Bid" else "Your Bid",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF94A3B8)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = bid.bidAmount,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bid.status == BidDisplayStatus.REJECTED) {
                                    if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                                } else {
                                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                }
                            )
                            bid.previousBid?.let { prev ->
                                Text(
                                    text = prev,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFEF4444),
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                        bid.lowestBid?.let { lowest ->
                            Text(
                                text = "Lowest bid: $lowest",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFF97316)
                            )
                        }
                    }

                    // Equipment or Re-bid button
                    if (bid.status == BidDisplayStatus.OUTBID) {
                        Button(
                            onClick = onRebidClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = "Re-bid",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Equipment",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF94A3B8)
                            )
                            Text(
                                text = bid.equipment,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF0F172A)
                            )
                        }
                    }
                }

                // Pending footer with submitted time and view details
                if (bid.status == BidDisplayStatus.PENDING && bid.submittedTime != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6),
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bid.submittedTime,
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF94A3B8)
                        )
                        Text(
                            text = "View Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary,
                            modifier = Modifier.clickable { onViewDetailsClick() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyBidsBottomNavigation(
    onFindLoadsClick: () -> Unit,
    onMyBidsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) Color(0xFF111822) else Color.White
    ) {
        Column {
            HorizontalDivider(
                color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                thickness = 1.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Find Loads
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFindLoadsClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Find Loads",
                        tint = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Find Loads",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF94A3B8)
                    )
                }

                // My Bids (Selected)
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMyBidsClick() },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Gavel,
                            contentDescription = "My Bids",
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "My Bids",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary
                        )
                    }
                    // Red notification dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopCenter)
                            .offset(x = 20.dp, y = 0.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                }

                // Profile
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Profile",
                        tint = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Profile",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

// Keep old BidCard for compatibility
@Composable
fun BidCard(bid: CarrierBid) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val statusColor = when (bid.status) {
        BidStatus.ACCEPTED -> MaterialTheme.colorScheme.tertiary
        BidStatus.REJECTED -> MaterialTheme.colorScheme.error
        BidStatus.PENDING -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Bid Amount: Rs. ${bid.bidAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = statusColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        bid.status.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Shipment ID: ${bid.shipmentId}", style = MaterialTheme.typography.bodyMedium)
            Text("Submitted: ${dateFormat.format(Date(bid.createdAt))}", style = MaterialTheme.typography.bodySmall)

            if (bid.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Your message: ${bid.message}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierMyBidsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierMyBidsScreen()
    }
}

package com.application.movemate.loader

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.LoaderManageBidsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

// Custom colors for Manage Bids
private val CardDarkBids = Color(0xFF192433)
private val TextSecondaryBids = Color(0xFF92A9C9)
private val BorderDarkBids = Color(0xFF374151)
private val GreenBadge = Color(0xFF22C55E)
private val PurpleBadge = Color(0xFF8B5CF6)

class LoaderManageBidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderManageBidsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderManageBidsScreen(viewModel: LoaderManageBidsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val bids by viewModel.bids.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bidAccepted by viewModel.bidAccepted.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedSort by remember { mutableStateOf("Lowest Price") }
    val sortOptions = listOf("Lowest Price", "Highest Rating", "Earliest")

    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        currentUser?.let {
            viewModel.fetchShipmentsWithBids(it.uid)
        }
    }

    LaunchedEffect(bidAccepted) {
        if (bidAccepted) {
            viewModel.resetBidAccepted()
            currentUser?.let {
                viewModel.fetchShipmentsWithBids(it.uid)
            }
        }
    }

    Scaffold(
        topBar = {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) Color(0xFF111822) else Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { (context as? ComponentActivity)?.finish() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                    }

                    // Title
                    Text(
                        text = "Bids for Order #9928",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // Help button
                    TextButton(onClick = { /* Help */ }) {
                        Text(
                            text = "Help",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Shipment Summary Card
                item {
                    ShipmentSummaryCard(
                        requestId = "#1234",
                        title = "Furniture Move: 2 Bedroom",
                        route = "Chicago, IL → Austin, TX",
                        mapImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuAb4kqR31m827G8fLwes6FETn5XOsqlu2z-qiFLYg-o-qbHKD1Lm-HZoIHw50jy8-6LN96KeWbmUY82ll-te6rjYGEGvmvAZ2FWJlPNLRczJO2EB4cwHxX5tsXzQoTJ-3Dw-dtYx3eyPDckSetHMlcR7nXIM8lbn226kik0V-9WqEtlPE_y8pPSlLEVURjwxDZqYAlZ8k9rgtXxNSMiw7-PZ7MDz6GG0pvKe4iSabvRV_UFoXEqVu0k0V_b3iTFytN_-VpeSDE7eOE"
                    )
                }

                // Sort/Filter Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        sortOptions.forEachIndexed { index, option ->
                            SortChip(
                                label = option,
                                icon = when (index) {
                                    0 -> Icons.Outlined.Sort
                                    1 -> Icons.Filled.Star
                                    else -> Icons.Outlined.Schedule
                                },
                                iconTint = when (index) {
                                    1 -> Color(0xFFEAB308)
                                    else -> if (selectedSort == option) Color.White else TextSecondaryBids
                                },
                                isSelected = selectedSort == option,
                                onClick = { selectedSort = option }
                            )
                        }
                    }
                }

                // Section Headline
                item {
                    Text(
                        text = "Incoming Bids (3)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }

                // Bid Cards
                item {
                    BidCard(
                        carrierName = "City Haulers",
                        carrierImage = null,
                        carrierInitials = null,
                        rating = 4.2f,
                        reviewCount = 84,
                        price = 380.0,
                        deliveryDate = "Oct 25, 10:00 AM",
                        vehicleType = "Cargo Van",
                        isVerified = true,
                        badge = BidBadge.LOWEST_PRICE,
                        isHighlighted = false,
                        onProfileClick = { /* View profile */ },
                        onAcceptClick = { /* Accept bid */ }
                    )
                }

                item {
                    BidCard(
                        carrierName = "FastMove",
                        carrierImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuBS9QxP2VBcNKVsdlpFbl4_v2_ZNKZ71E460N96ziaxVH-64qkt7_UPh0ArULQjSnaSBgc0RTIe0qg4L7ltvrJcbkL1NPxBLhzsfPa2pTXdce_rOLNKDwon1pDYEbc4cWPz_i_-tPftgPoF3Tzc_UYceItjuvc52Nj9ThKNxJEU4KZZUjL2AwyWGHk7kY6oXMkNpAZDvnJ9Gej_kh2mzo3DaJMk4cvSPnuc46l-KbER2REpfHS0Z7NHbl62w7GyXHvUVaXrgcQ9EQ4",
                        carrierInitials = null,
                        rating = 4.9f,
                        reviewCount = 120,
                        price = 450.0,
                        deliveryDate = "Oct 24, 2:00 PM",
                        vehicleType = "Box Truck",
                        isVerified = true,
                        badge = BidBadge.TOP_RATED,
                        isHighlighted = true,
                        onProfileClick = { /* View profile */ },
                        onAcceptClick = { /* Accept bid */ }
                    )
                }

                item {
                    BidCard(
                        carrierName = "QuickShip",
                        carrierImage = null,
                        carrierInitials = "QS",
                        rating = 4.5f,
                        reviewCount = 45,
                        price = 410.0,
                        deliveryDate = "Oct 24, 6:00 PM",
                        vehicleType = "Sprinter Van",
                        isVerified = false,
                        badge = null,
                        isHighlighted = false,
                        onProfileClick = { /* View profile */ },
                        onAcceptClick = { /* Accept bid */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShipmentSummaryCard(
    requestId: String,
    title: String,
    route: String,
    mapImage: String
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) CardDarkBids else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // OPEN badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDarkTheme) GreenBadge.copy(alpha = 0.3f) else Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "OPEN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "Request $requestId",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) TextSecondaryBids else Color(0xFF64748B)
                    )
                }

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = if (isDarkTheme) TextSecondaryBids else Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = route,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryBids else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Map preview
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(mapImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (isDarkTheme) BorderDarkBids else Color(0xFFE5E7EB),
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Primary else if (isDarkTheme) CardDarkBids else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkBids else Color(0xFFE5E7EB)
        ) else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected && label != "Highest Rating") Color.White else iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White
                        else if (isDarkTheme) Color.White else Color(0xFF374151)
            )
        }
    }
}

enum class BidBadge {
    LOWEST_PRICE, TOP_RATED
}

@Composable
private fun BidCard(
    carrierName: String,
    carrierImage: String?,
    carrierInitials: String?,
    rating: Float,
    reviewCount: Int,
    price: Double,
    deliveryDate: String,
    vehicleType: String,
    isVerified: Boolean,
    badge: BidBadge?,
    isHighlighted: Boolean,
    onProfileClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) CardDarkBids else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isHighlighted) Primary.copy(alpha = 0.2f)
                else if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
            ),
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Badge (if any)
                if (badge != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                }

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
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (carrierInitials != null) {
                                        if (isDarkTheme) Color(0xFF312E81) else Color(0xFFE0E7FF)
                                    } else {
                                        if (isDarkTheme) BorderDarkBids else Color(0xFFE5E7EB)
                                    }
                                )
                                .border(
                                    2.dp,
                                    if (isDarkTheme) Color(0xFF4B5563) else Color(0xFFE5E7EB),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                carrierImage != null -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(carrierImage)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Carrier",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                carrierInitials != null -> {
                                    Text(
                                        text = carrierInitials,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color(0xFFA5B4FC) else Color(0xFF4F46E5)
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Filled.LocalShipping,
                                        contentDescription = "Carrier",
                                        tint = Color(0xFF9CA3AF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = carrierName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                if (isVerified) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = "Verified",
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = rating.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFEAB308)
                                )
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "($reviewCount reviews)",
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }

                    // Price
                    Text(
                        text = "$${String.format("%.2f", price)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        letterSpacing = (-0.5).sp
                    )
                }

                // Details Grid
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.5f) else Color(0xFFF9FAFB),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) BorderDarkBids.copy(alpha = 0.5f) else Color(0xFFF3F4F6)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Delivery
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = "Delivery",
                                tint = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "DELIVERY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = deliveryDate,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
                                )
                            }
                        }

                        // Vehicle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DirectionsCar,
                                contentDescription = "Vehicle",
                                tint = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "VEHICLE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = vehicleType,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Button
                    OutlinedButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme) Color.White else Color(0xFF374151)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) Color(0xFF4B5563) else Color(0xFFE5E7EB)
                        )
                    ) {
                        Text(
                            text = "Profile",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Accept Button
                    Button(
                        onClick = onAcceptClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Accept ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${price.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Badge overlay
        if (badge != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 16.dp, y = (-4).dp),
                shape = RoundedCornerShape(12.dp),
                color = when (badge) {
                    BidBadge.LOWEST_PRICE -> GreenBadge
                    BidBadge.TOP_RATED -> PurpleBadge
                },
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (badge) {
                            BidBadge.LOWEST_PRICE -> Icons.Outlined.Savings
                            BidBadge.TOP_RATED -> Icons.Filled.Star
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = when (badge) {
                            BidBadge.LOWEST_PRICE -> "LOWEST PRICE"
                            BidBadge.TOP_RATED -> "TOP RATED"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Keep old composables for compatibility
@Composable
fun ShipmentWithBidsCard(shipment: Shipment, onViewBids: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                shipment.goodsType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${shipment.pickupAddress} → ${shipment.deliveryAddress}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Budget: Rs. ${shipment.estimatedPrice}", style = MaterialTheme.typography.bodyMedium)
            Text("Weight: ${shipment.weight} kg", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onViewBids, modifier = Modifier.fillMaxWidth()) {
                Text("View & Manage Bids")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidsBottomSheet(
    shipment: Shipment,
    viewModel: LoaderManageBidsViewModel,
    onDismiss: () -> Unit
) {
    val bids by viewModel.bids.collectAsState()
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                "Bids for ${shipment.goodsType}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your budget: Rs. ${shipment.estimatedPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (bids.isEmpty()) {
                Text(
                    "No bids yet. Carriers will submit bids soon.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(bids) { bid ->
                        BidItemCard(
                            bid = bid,
                            onAccept = {
                                viewModel.acceptBid(bid.id, shipment.id, bid.carrierId, bid.carrierName)
                                onDismiss()
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BidItemCard(bid: CarrierBid, onAccept: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val deliveryDays = ((bid.estimatedDeliveryTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(bid.carrierName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rating: ", style = MaterialTheme.typography.bodySmall)
                        Text("${bid.carrierRating} ⭐", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(
                    "Rs. ${bid.bidAmount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Delivery in: $deliveryDays days", style = MaterialTheme.typography.bodySmall)
            Text("Bid placed: ${dateFormat.format(Date(bid.createdAt))}", style = MaterialTheme.typography.bodySmall)

            if (bid.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Message: ${bid.message}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Accept This Bid")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoaderManageBidsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderManageBidsScreen()
    }
}

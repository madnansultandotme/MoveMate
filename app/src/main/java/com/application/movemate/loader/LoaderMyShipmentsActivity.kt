package com.application.movemate.loader

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
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.LoaderMyShipmentsViewModel
import java.text.SimpleDateFormat
import java.util.*

// Custom colors for My Shipments
private val CardDark = Color(0xFF1C2530)
private val TextSecondaryShipments = Color(0xFF94A3B8)
private val BorderDarkShipments = Color(0xFF334155)

class LoaderMyShipmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderMyShipmentsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderMyShipmentsScreen(viewModel: LoaderMyShipmentsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(1) } // Shipments tab selected

    val filters = listOf(
        FilterItem("All", null),
        FilterItem("Open for Bids", 3),
        FilterItem("In Transit", 1),
        FilterItem("Delivered", null),
        FilterItem("Drafts", null)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchMyShipments()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, CreateShipmentActivity::class.java))
                },
                containerColor = Primary,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .size(56.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Shipment",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) CardDark else Color.White
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
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShipmentsBottomNavItem(
                            icon = Icons.Outlined.Home,
                            label = "Home",
                            isSelected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                (context as? ComponentActivity)?.finish()
                            }
                        )
                        ShipmentsBottomNavItem(
                            icon = Icons.Filled.LocalShipping,
                            label = "Shipments",
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        ShipmentsBottomNavItem(
                            icon = Icons.Outlined.ChatBubble,
                            label = "Messages",
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            hasBadge = true
                        )
                        ShipmentsBottomNavItem(
                            icon = Icons.Outlined.Person,
                            label = "Profile",
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
        ) {
            // Sticky Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Profile avatar
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuBZbuOq6BheEX1YAlIALkAJ70f4RzwCttxwcUpiraAYSVSOjAexkA102YMrpmb7u2qObvNqDNQ87RpTMyTgcra_tq1-L3xhBE0T_OgjdP589-emaoDULuXqJev9SLGwDX62QqizP7G7lYcKPABNlokQ7J5MfUQfGEikmrN2odFqKOZZyfEWiF--WHOoDRxfwnlBa-4z5ZAgN4HHeyXw-cfXarXDjAaIe6aGwhNtJfL2Vieq7O-8c4PCSpL7AHTOcYSEvnO3kKUKmpc")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (isDarkTheme) BorderDarkShipments else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                        )
                        Text(
                            text = "My Shipments",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Add button
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, CreateShipmentActivity::class.java))
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Add",
                            tint = Primary
                        )
                    }
                }

                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkTheme) CardDark else Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = TextSecondaryShipments,
                            modifier = Modifier.size(20.dp)
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search by ID, City or Carrier...",
                                    color = TextSecondaryShipments,
                                    fontSize = 14.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = Primary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = { /* Filter */ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = "Filter",
                                tint = TextSecondaryShipments,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChipItem(
                            label = filter.label,
                            count = filter.count,
                            isSelected = selectedFilter == filter.label,
                            onClick = { selectedFilter = filter.label }
                        )
                    }
                }
            }

            // Scrollable Content
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sample shipment cards - in real app, filter based on selectedFilter
                    item {
                        OpenForBidsCard(
                            id = "#SH-4921",
                            date = "Posted Sep 24",
                            origin = "Seattle, WA",
                            destination = "Portland, OR",
                            cargoType = "Pallets (200kg)",
                            bidsCount = 3
                        )
                    }
                    item {
                        InTransitCard(
                            id = "#SH-3302",
                            date = "Sep 20 • Furniture",
                            origin = "Austin, TX",
                            destination = "Dallas, TX",
                            carrierName = "SpeedyTrans Inc.",
                            carrierImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuDPrmLE9jgnzpH7LjDNuxmDpN0SMFjQTm8lL8SvSCEB1Qf88WREV1gS_Gqz3i6g-MvLwYPPCUrTU94x-e4Ku-ZrE9Cavt-hTGFI1-NWInIxPmy8YX7iMyf6e4XuWJjPjuu-LsRHQFgLuXNt8skh73jkgOQaeW3smaIdSuyH9S9orUcgNhkRKIdZwlrnI8uFGdwqeo35VbqHeZBN66ea12MgLOG7WVn_XC__3BPP8RQ1a48rXjOI1gFRNGsNFDLqQey7que226F-ocY",
                            progress = 0.66f,
                            eta = "Today, 4:00 PM"
                        )
                    }
                    item {
                        DeliveredCard(
                            id = "#SH-1029",
                            date = "Aug 12 • Electronics",
                            route = "Miami, FL → Orlando, FL",
                            deliveredDate = "Delivered on Aug 14 at 10:30 AM",
                            price = "$450.00"
                        )
                    }
                    item {
                        DraftCard(
                            route = "Chicago, IL → Detroit, MI",
                            lastEdited = "Last edited 2h ago"
                        )
                    }
                }
            }
        }
    }
}

data class FilterItem(val label: String, val count: Int?)

@Composable
fun FilterChipItem(
    label: String,
    count: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Primary else if (isDarkTheme) CardDark else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkShipments else Color(0xFFE2E8F0)
        ) else null,
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White
                        else if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (label == "In Transit") Color(0xFFF59E0B).copy(alpha = 0.2f)
                            else Primary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (label == "In Transit") Color(0xFFF59E0B) else Primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OpenForBidsCard(
    id: String,
    date: String,
    origin: String,
    destination: String,
    cargoType: String,
    bidsCount: Int
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = "Package",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ID: $id",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondaryShipments
                        )
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Primary, CircleShape)
                        )
                        Text(
                            text = "Open",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Route line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(
                                2.dp,
                                if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(32.dp)
                            .background(if (isDarkTheme) BorderDarkShipments else Color(0xFFE2E8F0))
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Primary, CircleShape)
                            .border(2.dp, Primary, CircleShape)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Text(
                            text = origin,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Origin",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B)
                        )
                    }
                    Column {
                        Text(
                            text = destination,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Destination",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B)
                        )
                    }
                }
            }

            Divider(
                color = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ViewInAr,
                        contentDescription = "Cargo",
                        tint = TextSecondaryShipments,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = cargoType,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                    )
                }

                Button(
                    onClick = { /* View bids */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "$bidsCount Bids Received",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InTransitCard(
    id: String,
    date: String,
    origin: String,
    destination: String,
    carrierName: String,
    carrierImage: String,
    progress: Float,
    eta: String
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF59E0B).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalShipping,
                            contentDescription = "Shipping",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ID: $id",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondaryShipments
                        )
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "In Transit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route
            Text(
                text = "$origin → $destination",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF59E0B))
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 5.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, Color(0xFFF59E0B), CircleShape)
                    )
                }
            }

            Text(
                text = "Est. Arrival: $eta",
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Carrier info
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) Color(0xFF151C24) else Color(0xFFF8FAFC)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(carrierImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Carrier",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) BorderDarkShipments else Color(0xFFE2E8F0))
                        )
                        Column {
                            Text(
                                text = "CARRIER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = carrierName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { /* Chat */ },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (isDarkTheme) BorderDarkShipments else Color.White,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (isDarkTheme) Color(0xFF475569) else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Chat",
                                tint = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* Call */ },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (isDarkTheme) BorderDarkShipments else Color.White,
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    if (isDarkTheme) Color(0xFF475569) else Color(0xFFE2E8F0),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Call",
                                tint = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveredCard(
    id: String,
    date: String,
    route: String,
    deliveredDate: String,
    price: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.8f),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        ),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF22C55E).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Delivered",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ID: $id",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondaryShipments
                        )
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF22C55E).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Delivered",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF22C55E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = route,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = deliveredDate,
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )

            Divider(
                color = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "View Receipt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary,
                    modifier = Modifier.clickable { /* View receipt */ }
                )
            }
        }
    }
}

@Composable
fun DraftCard(
    route: String,
    lastEdited: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkShipments else Color(0xFFD1D5DB)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF64748B).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = "Draft",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Draft",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextSecondaryShipments
                    )
                    Text(
                        text = lastEdited,
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = route,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* Continue editing */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) BorderDarkShipments else Color(0xFFF1F5F9)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Continue Editing",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF374151)
                    )
                }
            }
        }
    }
}

@Composable
fun ShipmentsBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasBadge: Boolean = false
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Primary else TextSecondaryShipments,
                modifier = Modifier.size(24.dp)
            )
            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .background(Color(0xFFEF4444), CircleShape)
                        .border(2.dp, CardDark, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Primary else TextSecondaryShipments
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoaderMyShipmentsScreenPreview() {
    MoveMateTheme {
        LoaderMyShipmentsScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoaderMyShipmentsScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderMyShipmentsScreen()
    }
}

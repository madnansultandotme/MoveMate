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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.ActiveShipmentsViewModel

// Custom colors for Loader Active Shipments
private val CardDarkActive = Color(0xFF1E2229)
private val SecondaryTextActive = Color(0xFF92A9C9)
private val SurfaceDarkActive = Color(0xFF233348)
private val BorderDarkActive = Color(0xFF324867)

class ActiveShipmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                ActiveShipmentsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveShipmentsScreen(viewModel: ActiveShipmentsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(1) } // Active tab selected

    val filters = listOf("All", "Picked Up", "In Transit", "Arriving Soon")

    LaunchedEffect(Unit) {
        viewModel.fetchActiveShipments("loader_id_1")
    }

    Scaffold(
        bottomBar = {
            // Bottom Navigation with centered FAB
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color.White
            ) {
                Column {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.05f),
                        thickness = 1.dp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ActiveNavItem(
                                icon = Icons.Outlined.Home,
                                label = "Home",
                                isSelected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                    (context as? ComponentActivity)?.finish()
                                }
                            )
                            ActiveNavItem(
                                icon = Icons.Filled.LocalShipping,
                                label = "Active",
                                isSelected = selectedTab == 1,
                                onClick = { selectedTab = 1 }
                            )
                            // Spacer for FAB
                            Spacer(modifier = Modifier.width(64.dp))
                            ActiveNavItem(
                                icon = Icons.Outlined.History,
                                label = "History",
                                isSelected = selectedTab == 2,
                                onClick = { selectedTab = 2 }
                            )
                            ActiveNavItem(
                                icon = Icons.Outlined.Person,
                                label = "Profile",
                                isSelected = selectedTab == 3,
                                onClick = { selectedTab = 3 }
                            )
                        }

                        // Centered FAB
                        FloatingActionButton(
                            onClick = {
                                context.startActivity(Intent(context, CreateShipmentActivity::class.java))
                            },
                            containerColor = Primary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-28).dp)
                                .size(56.dp)
                                .border(4.dp, BackgroundDark, CircleShape),
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    // Safe area spacer
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Sticky Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f)
                        else MaterialTheme.colorScheme.background
                    )
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Shipments",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "3 shipments in progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryTextActive
                        )
                    }

                    IconButton(
                        onClick = { /* Filter */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceDarkActive)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = CardDarkActive,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = SecondaryTextActive,
                            modifier = Modifier.size(20.dp)
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search by ID (e.g. #MM-8492)",
                                    color = Color(0xFF5D7290),
                                    fontSize = 14.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = Primary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filters.forEach { filter ->
                        ActiveFilterChip(
                            label = filter,
                            isSelected = selectedFilter == filter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.05f),
                    thickness = 1.dp
                )
            }

            // Shipment List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ActiveShipmentCard(
                        id = "#MM-8492",
                        status = "In Transit",
                        statusColor = Primary,
                        origin = "Chicago, IL",
                        destination = "Austin, TX",
                        eta = "Today, 4:00 PM",
                        etaHighlight = false,
                        progress = 0.65f,
                        progressColor = Primary,
                        carrierInitials = "SL",
                        carrierName = "Speedy Logistics",
                        carrierRating = "4.8",
                        carrierReviews = "120",
                        mapImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuB5wvcJ6Fwqhyf6Z8wVDxuk-xvTF45M7-ixl_gJXxXMBi8V2tObu1rzUhi0rMOaZoyNg8i73E-294K1Z3_oeTY-btWsJXAbHeb-ahzYUHzfJftufY48hxf3WtyIEfGrmdX1VfcLW_AzTIpNQ2Py31qoBwJupz2tjCtL86hjNjrZAKMqMIMjo1pbN6uc4wez_2zLSjx9IuQslNsDoTAgpfOjZ6VIP_yxGNmviFrXClgtkZ-U9x7azcnFGGtKeTTbRtw9aI8sKOi2fB0"
                    )
                }
                item {
                    ActiveShipmentCard(
                        id = "#MM-9102",
                        status = "Picked Up",
                        statusColor = Color(0xFF6366F1), // Indigo
                        origin = "Seattle, WA",
                        destination = "Portland, OR",
                        eta = "Tomorrow, 10:00 AM",
                        etaHighlight = false,
                        progress = 0.25f,
                        progressColor = Color(0xFF6366F1),
                        carrierInitials = "NT",
                        carrierName = "North Trucking",
                        carrierRating = "4.9",
                        carrierReviews = "56",
                        mapImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuB5wvcJ6Fwqhyf6Z8wVDxuk-xvTF45M7-ixl_gJXxXMBi8V2tObu1rzUhi0rMOaZoyNg8i73E-294K1Z3_oeTY-btWsJXAbHeb-ahzYUHzfJftufY48hxf3WtyIEfGrmdX1VfcLW_AzTIpNQ2Py31qoBwJupz2tjCtL86hjNjrZAKMqMIMjo1pbN6uc4wez_2zLSjx9IuQslNsDoTAgpfOjZ6VIP_yxGNmviFrXClgtkZ-U9x7azcnFGGtKeTTbRtw9aI8sKOi2fB0"
                    )
                }
                item {
                    ActiveShipmentCard(
                        id = "#MM-3201",
                        status = "Arriving Soon",
                        statusColor = Color(0xFF10B981), // Emerald
                        origin = "Miami, FL",
                        destination = "Orlando, FL",
                        eta = "In 30 mins",
                        etaHighlight = true,
                        progress = 0.92f,
                        progressColor = Color(0xFF10B981),
                        carrierInitials = "GX",
                        carrierName = "Global Xpress",
                        carrierRating = "4.5",
                        carrierReviews = "82",
                        mapImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuB5wvcJ6Fwqhyf6Z8wVDxuk-xvTF45M7-ixl_gJXxXMBi8V2tObu1rzUhi0rMOaZoyNg8i73E-294K1Z3_oeTY-btWsJXAbHeb-ahzYUHzfJftufY48hxf3WtyIEfGrmdX1VfcLW_AzTIpNQ2Py31qoBwJupz2tjCtL86hjNjrZAKMqMIMjo1pbN6uc4wez_2zLSjx9IuQslNsDoTAgpfOjZ6VIP_yxGNmviFrXClgtkZ-U9x7azcnFGGtKeTTbRtw9aI8sKOi2fB0"
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ActiveFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Primary else SurfaceDarkActive,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.05f)
        ) else null,
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) Color.White else SecondaryTextActive,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ActiveShipmentCard(
    id: String,
    status: String,
    statusColor: Color,
    origin: String,
    destination: String,
    eta: String,
    etaHighlight: Boolean,
    progress: Float,
    progressColor: Color,
    carrierInitials: String,
    carrierName: String,
    carrierRating: String,
    carrierReviews: String,
    mapImage: String
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardDarkActive,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.05f)
        ),
        shadowElevation = 8.dp
    ) {
        Column {
            // Map Preview Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(Color(0xFF374151))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(mapImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Map",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // ID Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Status Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(24.dp),
                    color = statusColor.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Card Body
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Route Info
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ORIGIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            color = SecondaryTextActive
                        )
                        Text(
                            text = "DESTINATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            color = SecondaryTextActive
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = origin,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(BorderDarkActive)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Arrow",
                                tint = BorderDarkActive,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(16.dp)
                                    .offset(x = 8.dp)
                            )
                        }

                        Text(
                            text = destination,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text(
                            text = "Est. Arrival: ",
                            fontSize = 12.sp,
                            color = SecondaryTextActive
                        )
                        Text(
                            text = eta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (etaHighlight) Color(0xFF34D399) else Color.White
                        )
                    }
                }

                // Progress Bar
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "PICKED UP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = SecondaryTextActive
                        )
                        Text(
                            text = "ARRIVING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = SecondaryTextActive
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BorderDarkActive)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(progressColor)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.05f),
                    thickness = 1.dp
                )

                // Carrier & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Carrier info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BorderDarkActive)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = carrierInitials,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = carrierName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "$carrierRating ($carrierReviews)",
                                    fontSize = 10.sp,
                                    color = SecondaryTextActive
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { /* Chat */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BorderDarkActive)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Chat,
                                contentDescription = "Chat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Button(
                            onClick = { /* Track */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(36.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Track Map",
                                fontSize = 14.sp,
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
private fun ActiveNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Primary else SecondaryTextActive,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Primary else SecondaryTextActive
        )
    }
}

// Keep the old composable for compatibility
@Composable
fun ActiveShipmentListItem(shipment: Shipment) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Shipment ID: ${shipment.id}", style = MaterialTheme.typography.titleMedium)
        Text("Status: ${shipment.status}")
    }
}

@Preview(showBackground = true)
@Composable
fun ActiveShipmentsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        ActiveShipmentsScreen()
    }
}

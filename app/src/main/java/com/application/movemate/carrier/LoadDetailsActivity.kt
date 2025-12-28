package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.CarrierLoadDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_SHIPMENT_ID = "SHIPMENT_ID"

// Custom colors
private val SurfaceDark = Color(0xFF1C2530)
private val SurfaceLight = Color(0xFFFFFFFF)

class LoadDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val shipmentId = intent.getStringExtra(EXTRA_SHIPMENT_ID) ?: ""

        setContent {
            MoveMateTheme {
                CarrierLoadDetailsScreen(shipmentId = shipmentId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierLoadDetailsScreen(
    shipmentId: String,
    viewModel: CarrierLoadDetailsViewModel = viewModel()
) {
    val shipment by viewModel.shipment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bidSubmitted by viewModel.bidSubmitted.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var bidAmount by remember { mutableStateOf("") }
    var estimatedArrival by remember { mutableStateOf("") }
    var instructionsExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(shipmentId) {
        if (shipmentId.isNotEmpty()) {
            viewModel.fetchShipment(shipmentId)
        }
    }

    Scaffold(
        bottomBar = {
            // Sticky Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) Color(0xFF151F2B) else Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-16).dp),
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Bid Amount Input
                        OutlinedTextField(
                            value = bidAmount,
                            onValueChange = { bidAmount = it },
                            placeholder = { Text("Offer Amount", fontSize = 14.sp) },
                            leadingIcon = {
                                Text(
                                    text = "$",
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = if (isDarkTheme) SurfaceDark else Color(0xFFF8FAFC),
                                focusedContainerColor = if (isDarkTheme) SurfaceDark else Color(0xFFF8FAFC),
                                unfocusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0),
                                focusedBorderColor = Primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold)
                        )

                        // Estimated Arrival Input
                        OutlinedTextField(
                            value = estimatedArrival,
                            onValueChange = { estimatedArrival = it },
                            placeholder = { Text("Est. Arrival", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = if (isDarkTheme) SurfaceDark else Color(0xFFF8FAFC),
                                focusedContainerColor = if (isDarkTheme) SurfaceDark else Color(0xFFF8FAFC),
                                unfocusedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0),
                                focusedBorderColor = Primary
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            viewModel.submitBid(
                                shipmentId = shipmentId,
                                bidAmount = bidAmount.toDoubleOrNull() ?: 0.0,
                                estimatedDays = 2,
                                message = ""
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        enabled = bidAmount.isNotEmpty() && !bidSubmitted
                    ) {
                        Text(
                            text = if (bidSubmitted) "Bid Submitted!" else "Submit Bid",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (bidSubmitted) Icons.Filled.Check else Icons.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    // Top App Bar
                    LoadDetailsTopBar(
                        loadId = "#83921",
                        status = "Open for Bidding",
                        onBackClick = { (context as? ComponentActivity)?.finish() },
                        onReportClick = { }
                    )

                    // Map Header
                    LoadMapHeader(
                        distance = "924 mi",
                        duration = "~14h 20m"
                    )

                    // Target Rate Section
                    TargetRateSection(
                        rate = "$1,200",
                        marketRange = "$1,150 - $1,300"
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
                    )

                    // Route Timeline
                    RouteTimeline(
                        pickupLocation = "Chicago, IL",
                        pickupAddress = "123 Industrial Park, Dock 4",
                        pickupDate = "Oct 12, 08:00 AM",
                        dropoffLocation = "Dallas, TX",
                        dropoffAddress = "456 Warehouse Blvd",
                        dropoffDate = "Oct 14, 02:00 PM"
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
                    )

                    // Load Specifications
                    LoadSpecificationsSection()

                    // Special Instructions
                    SpecialInstructionsSection(
                        instructions = "Driver must have PPE (vest and boots) for pickup. Liftgate required at delivery location. Please call receiver 1 hour prior to arrival.",
                        expanded = instructionsExpanded,
                        onExpandClick = { instructionsExpanded = !instructionsExpanded }
                    )

                    // Shipper Info
                    ShipperInfoSection(
                        name = "Acme Logistics",
                        initials = "AL",
                        rating = 4.8f,
                        loadCount = "150+",
                        isVerified = true,
                        onChatClick = { }
                    )

                    // Bottom spacing
                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message
                    error?.let {
                        Text(
                            text = it,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadDetailsTopBar(
    loadId: String,
    status: String,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit
) {
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
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Load $loadId",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        letterSpacing = (-0.5).sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                TextButton(onClick = onReportClick) {
                    Text(
                        text = "Report",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.5.sp
                    )
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
private fun LoadMapHeader(
    distance: String,
    duration: String
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        // Map Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("https://lh3.googleusercontent.com/aida-public/AB6AXuAtK__Jv5SAnGpql5kUc8NMDPEa7aOvGz5iT8_Fif7_CAU4mUgKvsA4ikGsUd026tcx4dOI9YvoXGF33e4vQuj_yK1zi8snAG3FxVZ8vxV0LS2tzLPx2U6Kax0Rm59gKefPt7n3VyWsTbqcwmAXye1sWf6bjaOIq6g4CDZWhsd8CBHxjS8m2s-QoLtfGK4MtEw1ZhYUMhTRczZ_QA8rUHRZ_DzKru3KCy8JsnKnLwvj4IpFdj_SY_Stqz0ifFcsWo_HmMiMaA26Agg")
                .crossfade(true)
                .build(),
            contentDescription = "Route Map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BackgroundDark.copy(alpha = 0.8f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Route Summary Chips
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapInfoChip(
                icon = Icons.Outlined.Route,
                text = distance
            )
            MapInfoChip(
                icon = Icons.Outlined.Schedule,
                text = duration
            )
        }
    }
}

@Composable
private fun MapInfoChip(
    icon: ImageVector,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BackgroundDark.copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TargetRateSection(
    rate: String,
    marketRange: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "TARGET RATE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Baseline,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = rate,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                letterSpacing = (-1).sp
            )
            Text(
                text = "USD",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Market Avg: $marketRange",
            fontSize = 14.sp,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
    }
}

@Composable
private fun RouteTimeline(
    pickupLocation: String,
    pickupAddress: String,
    pickupDate: String,
    dropoffLocation: String,
    dropoffAddress: String,
    dropoffDate: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Pickup Row
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Timeline column
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pickup node
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.2f))
                        .border(1.dp, Primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonChecked,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                // Connecting line
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                )
            }

            // Pickup details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PICKUP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = pickupDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pickupLocation,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = pickupAddress,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        // Dropoff Row
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Timeline column
            Column(
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Short line at top
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                )
                // Dropoff node
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF97316).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFFF97316).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Dropoff details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DELIVERY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF97316),
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = dropoffDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dropoffLocation,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = dropoffAddress,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun LoadSpecificationsSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Load Specifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Specs Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SpecCard(
                    icon = Icons.Outlined.Scale,
                    label = "WEIGHT",
                    value = "4,500 lbs"
                )
                SpecCard(
                    icon = Icons.Outlined.LocalShipping,
                    label = "EQUIPMENT",
                    value = "Dry Van, 53'"
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SpecCard(
                    icon = Icons.Outlined.AspectRatio,
                    label = "DIMS",
                    value = "48x40x96 in"
                )
                SpecCard(
                    icon = Icons.Outlined.Inventory2,
                    label = "TYPE",
                    value = "Palletized (4)"
                )
            }
        }
    }
}

@Composable
private fun SpecCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun SpecialInstructionsSection(
    instructions: String,
    expanded: Boolean,
    onExpandClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF97316)
                    )
                    Text(
                        text = "Special Instructions",
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }

            if (expanded) {
                Text(
                    text = instructions,
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ShipperInfoSection(
    name: String,
    initials: String,
    rating: Float,
    loadCount: String,
    isVerified: Boolean,
    onChatClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Shipper",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else SurfaceLight,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFE2E8F0)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                            )
                            if (isVerified) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFACC15),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = rating.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "• $loadCount Loads",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Chat button
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onChatClick() },
                    shape = CircleShape,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F5F9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Chat,
                            contentDescription = "Chat",
                            tint = Primary
                        )
                    }
                }
            }
        }
    }
}

// Keep old DetailRow for compatibility
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun LoadDetailsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierLoadDetailsScreen(shipmentId = "test123")
    }
}

package com.application.movemate.carrier

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.UpdateShipmentStatusViewModel

// Color definitions
private val CardDark = Color(0xFF1C2530)
private val CardLight = Color(0xFFFFFFFF)
private val SurfaceDarkBg = Color(0xFF151E29)
private val BorderLight = Color(0xFFE2E8F0)
private val TextSecondaryDark = Color(0xFF94A3B8)
private val TextSecondaryLight = Color(0xFF64748B)
private val OrangeAccent = Color(0xFFF97316)
private val GreenAccent = Color(0xFF22C55E)

// Data class for shipment status step
data class StatusStep(
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val icon: ImageVector
)

class UpdateShipmentStatusActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val shipmentId = intent.getStringExtra(EXTRA_SHIPMENT_ID)
        setContent {
            MoveMateTheme {
                UpdateShipmentStatusScreen(
                    shipmentId = shipmentId,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShipmentStatusScreen(
    shipmentId: String?,
    viewModel: UpdateShipmentStatusViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    // Sample status steps
    val statusSteps = remember {
        listOf(
            StatusStep("Assigned", "Oct 23, 09:30 AM", isCompleted = true, isCurrent = false, Icons.Filled.Check),
            StatusStep("Picked Up", "Oct 24, 10:15 AM", isCompleted = true, isCurrent = false, Icons.Filled.Check),
            StatusStep("In Transit", "Updating location...", isCompleted = false, isCurrent = true, Icons.Filled.LocalShipping),
            StatusStep("Delivered", "Pending arrival", isCompleted = false, isCurrent = false, Icons.Filled.Inventory2)
        )
    }

    Scaffold(
        topBar = {
            UpdateStatusTopBar(
                shipmentId = shipmentId ?: "#MM-8492",
                onBackClick = onBackClick,
                onCancelClick = onBackClick
            )
        },
        containerColor = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(bottom = 100.dp)
            ) {
                // Map & Summary Card
                MapSummaryCard(
                    originCity = "San Francisco, CA",
                    originLocation = "Warehouse A • Oct 24, 10:00 AM",
                    destinationCity = "Austin, TX",
                    destinationLocation = "Tech Hub • Est. Oct 26, 2:00 PM",
                    remainingDistance = "245 mi remaining"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Timeline
                ShipmentProgressSection(steps = statusSteps)

                Spacer(modifier = Modifier.height(16.dp))

                // Proof of Delivery Section
                ProofOfDeliverySection()
            }

            // Bottom Action Bar
            BottomActionBar(
                onActionClick = { /* Handle arrived at delivery */ },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateStatusTopBar(
    shipmentId: String,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else BorderLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update Status",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = shipmentId,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                )
            }

            // Cancel Button
            Text(
                text = "Cancel",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCancelClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun MapSummaryCard(
    originCity: String,
    originLocation: String,
    destinationCity: String,
    destinationLocation: String,
    remainingDistance: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else CardLight,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else BorderLight
        )
    ) {
        Column {
            // Map Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            ) {
                // Placeholder for map - would use Google Maps in real implementation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.1f),
                                    Primary.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Primary.copy(alpha = 0.5f)
                    )
                }

                // Distance chip
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = remainingDistance,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Route Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Dashed line connector using Box overlay
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Dashed vertical line
                    Box(
                        modifier = Modifier
                            .padding(start = 19.dp, top = 32.dp, bottom = 32.dp)
                            .width(2.dp)
                            .height(48.dp)
                            .background(
                                if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Origin
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RadioButtonChecked,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = originCity,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = originLocation,
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }

                        // Destination
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(OrangeAccent.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = OrangeAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = destinationCity,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = destinationLocation,
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShipmentProgressSection(steps: List<StatusStep>) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Shipment Progress",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) CardDark else CardLight,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1E293B) else BorderLight
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                steps.forEachIndexed { index, step ->
                    StatusStepItem(
                        step = step,
                        isLast = index == steps.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusStepItem(
    step: StatusStep,
    isLast: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        // Step indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            step.isCompleted -> GreenAccent
                            step.isCurrent -> Primary
                            else -> if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB)
                        }
                    )
                    .then(
                        if (step.isCurrent) {
                            Modifier.border(4.dp, Primary.copy(alpha = 0.2f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (step.isCompleted) Icons.Filled.Check else step.icon,
                    contentDescription = null,
                    tint = when {
                        step.isCompleted || step.isCurrent -> Color.White
                        else -> if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            // Connector line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Step content
        Column(
            modifier = Modifier.padding(top = 4.dp, bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Text(
                text = step.title,
                fontSize = 14.sp,
                fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                color = when {
                    step.isCurrent -> Primary
                    step.isCompleted -> if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    else -> if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                }
            )
            Text(
                text = step.subtitle,
                fontSize = 12.sp,
                color = when {
                    step.isCurrent -> Primary.copy(alpha = 0.8f)
                    else -> if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                }
            )
        }
    }
}

@Composable
private fun ProofOfDeliverySection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Proof of Delivery",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFFEF3C7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D).copy(alpha = 0.2f))
            ) {
                Text(
                    text = "Required upon arrival",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD97706),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) CardDark else CardLight,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1E293B) else BorderLight
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo Upload
                PhotoUploadSection()

                // Signature Pad
                SignaturePadSection()

                // Driver Notes
                DriverNotesSection()
            }
        }
    }
}

@Composable
private fun PhotoUploadSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Cargo Photos",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Existing photo placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                    .border(
                        1.dp,
                        if (isDarkTheme) Color(0xFF374151) else Color(0xFFE2E8F0),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(32.dp)
                )

                // Close button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Add photo button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        2.dp,
                        if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { /* Handle add photo */ },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddAPhoto,
                        contentDescription = "Add Photo",
                        tint = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Add Photo",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                    )
                }
            }
        }
    }
}

@Composable
private fun SignaturePadSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Recipient Signature",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDarkTheme) SurfaceDarkBg else Color(0xFFF9FAFB))
                .border(
                    2.dp,
                    if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = "Sign here",
                fontSize = 14.sp,
                color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = "Clear",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable { /* Handle clear */ }
            )
        }
    }
}

@Composable
private fun DriverNotesSection() {
    val isDarkTheme = isSystemInDarkTheme()
    var notes by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Driver Notes",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            placeholder = {
                Text(
                    text = "Any issues with delivery?",
                    color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB),
                focusedContainerColor = if (isDarkTheme) SurfaceDarkBg else Color(0xFFF9FAFB),
                unfocusedContainerColor = if (isDarkTheme) SurfaceDarkBg else Color(0xFFF9FAFB),
                cursorColor = Primary,
                focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun BottomActionBar(
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isDarkTheme) CardDark else CardLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else BorderLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Arrived at Delivery",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateShipmentStatusScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        UpdateShipmentStatusScreen(shipmentId = "#MM-8492")
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateShipmentStatusScreenLightPreview() {
    MoveMateTheme(darkTheme = false) {
        UpdateShipmentStatusScreen(shipmentId = "#MM-8492")
    }
}

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.movemate.ui.theme.*

// Custom colors for Analytics
private val SurfaceDarkAnalytics = Color(0xFF1C2632)
private val EmeraldAccent = Color(0xFF10B981)
private val RoseAccent = Color(0xFFF43F5E)
private val PurpleAccentAnalytics = Color(0xFF9333EA)
private val OrangeAccentAnalytics = Color(0xFFF97316)

// Data classes
data class KpiCardData(
    val title: String,
    val value: String,
    val changePercentage: String,
    val isPositive: Boolean,
    val icon: ImageVector,
    val iconBackgroundLight: Color,
    val iconBackgroundDark: Color,
    val iconColor: Color
)

data class TimeFrame(
    val label: String,
    val value: String
)

class AnalyticsReportingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                AnalyticsReportingScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsReportingScreen(
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()

    var selectedTimeFrame by remember { mutableIntStateOf(1) } // Default: "This Month"

    val timeFrames = listOf(
        TimeFrame("Last 7 Days", "7days"),
        TimeFrame("This Month", "month"),
        TimeFrame("This Year", "year")
    )

    val kpiCards = listOf(
        KpiCardData(
            title = "Shipments",
            value = "1,240",
            changePercentage = "+12%",
            isPositive = true,
            icon = Icons.Default.LocalShipping,
            iconBackgroundLight = Color(0xFFDBEAFE),
            iconBackgroundDark = Color(0xFF1E3A5F),
            iconColor = Primary
        ),
        KpiCardData(
            title = "New Shippers",
            value = "45",
            changePercentage = "+5%",
            isPositive = true,
            icon = Icons.Default.GroupAdd,
            iconBackgroundLight = Color(0xFFF3E8FF),
            iconBackgroundDark = Color(0xFF3B1E54),
            iconColor = PurpleAccentAnalytics
        ),
        KpiCardData(
            title = "New Carriers",
            value = "12",
            changePercentage = "-2%",
            isPositive = false,
            icon = Icons.Default.Badge,
            iconBackgroundLight = Color(0xFFFFEDD5),
            iconBackgroundDark = Color(0xFF4A2C1A),
            iconColor = OrangeAccentAnalytics
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
    ) {
        Scaffold(
            topBar = {
                AnalyticsTopBar(onBackClick = onBackClick)
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Time Frame Selector
                item {
                    TimeFrameSelector(
                        timeFrames = timeFrames,
                        selectedIndex = selectedTimeFrame,
                        onSelected = { selectedTimeFrame = it }
                    )
                }

                // At a Glance Section
                item {
                    AtAGlanceSection(kpiCards = kpiCards)
                }

                // Volume Trends Chart
                item {
                    VolumeTrendsSection()
                }

                // Logistics Insights
                item {
                    LogisticsInsightsSection()
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            DownloadReportButton()
        }
    }
}

@Composable
private fun AnalyticsTopBar(onBackClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.9f) else Color(0xFFF6F7F8).copy(alpha = 0.9f)
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

            // Title
            Text(
                text = "Platform Analytics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )

            // Settings Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeFrameSelector(
    timeFrames: List<TimeFrame>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isDarkTheme) SurfaceDarkAnalytics else Color(0xFFE2E8F0)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            timeFrames.forEachIndexed { index, timeFrame ->
                val isSelected = index == selectedIndex

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelected(index) },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) {
                        if (isDarkTheme) Color(0xFF2C3B4E) else Color.White
                    } else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = timeFrame.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) {
                                if (isDarkTheme) Color.White else Primary
                            } else {
                                if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AtAGlanceSection(kpiCards: List<KpiCardData>) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Text(
            text = "At a Glance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            kpiCards.forEach { card ->
                KpiCard(data = card)
            }
        }
    }
}

@Composable
private fun KpiCard(data: KpiCardData) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDarkAnalytics else Color.White,
        shadowElevation = 2.dp,
        border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDarkTheme) data.iconBackgroundDark else data.iconBackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        tint = data.iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = data.title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
            }

            // Value
            Text(
                text = data.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            // Change percentage
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                @Suppress("DEPRECATION")
                Icon(
                    imageVector = if (data.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (data.isPositive) EmeraldAccent else RoseAccent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = data.changePercentage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (data.isPositive) EmeraldAccent else RoseAccent
                )
            }
        }
    }
}

@Composable
private fun VolumeTrendsSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Volume Trends",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem(color = Primary, label = "Loads")
                LegendItem(
                    color = if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1),
                    label = "Carriers"
                )
            }
        }

        // Chart Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDarkAnalytics else Color.White,
            shadowElevation = 2.dp,
            border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFF1F5F9)
            )
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                VolumeTrendsChart()
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
    }
}

@Composable
private fun VolumeTrendsChart() {
    val isDarkTheme = isSystemInDarkTheme()

    val barData = listOf(
        Triple("M", 0.40f, 0.30f),
        Triple("T", 0.65f, 0.45f),
        Triple("W", 0.50f, 0.48f),
        Triple("T", 0.85f, 0.60f),
        Triple("F", 0.75f, 0.50f),
        Triple("S", 0.30f, 0.20f),
        Triple("S", 0.25f, 0.15f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
    ) {
        // Grid lines
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(4) {
                HorizontalDivider(
                    color = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.3f)
                           else Color(0xFF94A3B8).copy(alpha = 0.1f),
                    thickness = 1.dp
                )
            }
        }

        // Bars
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            barData.forEach { (day, primaryHeight, secondaryHeight) ->
                BarGroupItem(
                    day = day,
                    primaryHeightFraction = primaryHeight,
                    secondaryHeightFraction = secondaryHeight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BarGroupItem(
    day: String,
    primaryHeightFraction: Float,
    secondaryHeightFraction: Float,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            // Primary bar (Loads)
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight(primaryHeightFraction)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(Primary)
            )

            Spacer(modifier = Modifier.width(2.dp))

            // Secondary bar (Carriers)
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight(secondaryHeightFraction)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(if (isDarkTheme) Color(0xFF475569) else Color(0xFFCBD5E1))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = day,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF94A3B8)
        )
    }
}

@Composable
private fun LogisticsInsightsSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Logistics Insights",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Route Card
            TopRouteCard()

            // Avg Delivery Time Card
            AvgDeliveryTimeCard()
        }
    }
}

@Composable
private fun TopRouteCard() {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDarkAnalytics else Color.White,
        shadowElevation = 2.dp,
        border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Route Timeline Visual
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .border(2.dp, Primary, CircleShape)
                            .background(Color.Transparent, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(24.dp)
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Primary, CircleShape)
                    )
                }

                // Route Info
                Column {
                    Text(
                        text = "Top Performing Route",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "New York, NY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Chicago, IL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                }
            }

            // Stats
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "150",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = "Loads this month",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun AvgDeliveryTimeCard() {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDarkAnalytics else Color.White,
        shadowElevation = 2.dp,
        border = if (isDarkTheme) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFF1F5F9)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timer Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Avg Delivery Time",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "2.4 Days",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "-4hrs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldAccent
                    )
                }

                // Progress Bar
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(Primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadReportButton() {
    Button(
        onClick = { },
        modifier = Modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Download Report",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsReportingScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        AnalyticsReportingScreen()
    }
}


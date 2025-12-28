package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.EarningsViewModel

// Custom colors for Earnings
private val SurfaceDark = Color(0xFF1E2936)
private val SurfaceLight = Color(0xFFFFFFFF)
private val EmeraldAccent = Color(0xFF10B981)
private val AmberAccent = Color(0xFFF59E0B)

// Data class for transaction display
data class EarningsTransaction(
    val id: String,
    val route: String,
    val date: String,
    val amount: String,
    val status: TransactionStatus
)

enum class TransactionStatus {
    PAID, PROCESSING, PENDING
}

class EarningsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                EarningsScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    viewModel: EarningsViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    var selectedPeriod by remember { mutableStateOf("monthly") }

    // Sample transactions
    val transactions = remember {
        listOf(
            EarningsTransaction(
                id = "#TR-8832",
                route = "Seattle, WA → Portland, OR",
                date = "Oct 24",
                amount = "+$450.00",
                status = TransactionStatus.PAID
            ),
            EarningsTransaction(
                id = "#TR-8821",
                route = "San Fran, CA → San Jose, CA",
                date = "Oct 22",
                amount = "+$120.00",
                status = TransactionStatus.PROCESSING
            ),
            EarningsTransaction(
                id = "#TR-8815",
                route = "Austin, TX → Dallas, TX",
                date = "Oct 20",
                amount = "+$340.00",
                status = TransactionStatus.PAID
            )
        )
    }

    Scaffold(
        topBar = {
            EarningsTopBar(
                onBackClick = onBackClick,
                onFilterClick = { }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Period Selector
                    PeriodSelector(
                        selectedPeriod = selectedPeriod,
                        onPeriodSelected = { selectedPeriod = it }
                    )

                    // Hero Card: Total Earnings
                    TotalEarningsCard(
                        totalEarnings = "$4,250.00",
                        percentageChange = "+8.5%",
                        comparisonText = "vs last month"
                    )

                    // Performance Chart
                    IncomeAnalysisSection()

                    // Quick Stats Grid
                    QuickStatsGrid(
                        deliveries = 15,
                        avgPerTrip = "$283",
                        pending = "$120"
                    )

                    // Recent Transactions
                    RecentTransactionsSection(transactions = transactions)
                }
            }

            // Bottom Floating Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
                            )
                        )
                    )
                    .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Withdraw Funds",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EarningsTopBar(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
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
                    .clickable { onBackClick() }
                    .background(
                        if (isDarkTheme) Color.Transparent else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title
            Text(
                text = "Earnings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            // Filter Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Filter",
                    tint = if (isDarkTheme) Color.White else Color(0xFF334155),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val periods = listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) Color(0xFF1E2936) else Color(0xFFE2E8F0)
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            periods.forEach { (value, label) ->
                val isSelected = selectedPeriod == value
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPeriodSelected(value) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) {
                        if (isDarkTheme) Color(0xFF2C3B4E) else Color.White
                    } else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) {
                                Primary
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
private fun TotalEarningsCard(
    totalEarnings: String,
    percentageChange: String,
    comparisonText: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Primary,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Background decorations
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 80.dp, y = (-40).dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .offset(x = (-40).dp, y = 40.dp)
                    .align(Alignment.BottomStart)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.1f))
            )

            Column {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Earnings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Visibility,
                        contentDescription = "Toggle visibility",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Amount
                Text(
                    text = totalEarnings,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Percentage badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = percentageChange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = comparisonText,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun IncomeAnalysisSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Income Analysis",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) SurfaceDark else SurfaceLight,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Oct 1 - Oct 31",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            tint = EmeraldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "12%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart
                EarningsChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // X-axis labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("1", "5", "10", "15", "20", "25", "30").forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EarningsChart(
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Chart data points (normalized 0-1)
    val dataPoints = listOf(0.3f, 0.65f, 0.5f, 0.85f, 0.75f, 0.3f, 0.25f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val segmentWidth = width / (dataPoints.size - 1)

        // Create smooth curve path
        val linePath = Path()
        val fillPath = Path()

        // Control point offset for smooth curves
        val controlOffset = segmentWidth * 0.5f

        // Start point
        val startY = height - (dataPoints[0] * height * 0.8f)
        linePath.moveTo(0f, startY)
        fillPath.moveTo(0f, height)
        fillPath.lineTo(0f, startY)

        // Draw curved line through all points
        dataPoints.forEachIndexed { index, point ->
            val x = index * segmentWidth
            val y = height - (point * height * 0.8f)

            if (index > 0) {
                val prevX = (index - 1) * segmentWidth
                val prevY = height - (dataPoints[index - 1] * height * 0.8f)

                linePath.cubicTo(
                    prevX + controlOffset, prevY,
                    x - controlOffset, y,
                    x, y
                )
                fillPath.cubicTo(
                    prevX + controlOffset, prevY,
                    x - controlOffset, y,
                    x, y
                )
            }
        }

        // Complete fill path
        fillPath.lineTo(width, height)
        fillPath.close()

        // Draw gradient fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Primary.copy(alpha = 0.3f),
                    Primary.copy(alpha = 0f)
                )
            )
        )

        // Draw line
        drawPath(
            path = linePath,
            color = Primary,
            style = Stroke(
                width = 6f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw indicator dot at highest point (index 3)
        val highlightIndex = 3
        val highlightX = highlightIndex * segmentWidth
        val highlightY = height - (dataPoints[highlightIndex] * height * 0.8f)

        // Outer circle (white stroke)
        drawCircle(
            color = if (isDarkTheme) Color(0xFF1E2936) else Color.White,
            radius = 12f,
            center = Offset(highlightX, highlightY)
        )
        // Inner circle (primary color)
        drawCircle(
            color = Primary,
            radius = 8f,
            center = Offset(highlightX, highlightY)
        )
    }
}

@Composable
private fun QuickStatsGrid(
    deliveries: Int,
    avgPerTrip: String,
    pending: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Deliveries
        QuickStatCard(
            icon = Icons.Outlined.LocalShipping,
            iconColor = Primary,
            iconBgColor = if (isDarkTheme) Primary.copy(alpha = 0.2f) else Color(0xFFEFF6FF),
            label = "Deliveries",
            value = deliveries.toString(),
            modifier = Modifier.weight(1f)
        )

        // Avg/Trip
        QuickStatCard(
            icon = Icons.AutoMirrored.Outlined.TrendingUp,
            iconColor = EmeraldAccent,
            iconBgColor = if (isDarkTheme) EmeraldAccent.copy(alpha = 0.2f) else Color(0xFFECFDF5),
            label = "Avg/Trip",
            value = avgPerTrip,
            modifier = Modifier.weight(1f)
        )

        // Pending
        QuickStatCard(
            icon = Icons.Outlined.Pending,
            iconColor = AmberAccent,
            iconBgColor = if (isDarkTheme) AmberAccent.copy(alpha = 0.2f) else Color(0xFFFFFBEB),
            label = "Pending",
            value = pending,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Label
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            // Value
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun RecentTransactionsSection(
    transactions: List<EarningsTransaction>
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
            Text(
                text = "See All",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                modifier = Modifier.clickable { }
            )
        }

        // Transaction Items
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: EarningsTransaction
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
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
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NorthEast,
                        contentDescription = null,
                        tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Route and date
                Column {
                    Text(
                        text = transaction.route,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaction.date} • ID: ${transaction.id}",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }

            // Amount and status
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = transaction.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                StatusBadge(status = transaction.status)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TransactionStatus) {
    val isDarkTheme = isSystemInDarkTheme()

    val (text, textColor, bgColor) = when (status) {
        TransactionStatus.PAID -> Triple(
            "Paid",
            if (isDarkTheme) Color(0xFF34D399) else Color(0xFF047857),
            if (isDarkTheme) EmeraldAccent.copy(alpha = 0.2f) else Color(0xFFECFDF5)
        )
        TransactionStatus.PROCESSING -> Triple(
            "Processing",
            if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFFB45309),
            if (isDarkTheme) AmberAccent.copy(alpha = 0.2f) else Color(0xFFFFFBEB)
        )
        TransactionStatus.PENDING -> Triple(
            "Pending",
            if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            if (isDarkTheme) Color(0xFF334155) else Color(0xFFF1F5F9)
        )
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EarningsScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        EarningsScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun EarningsScreenLightPreview() {
    MoveMateTheme(darkTheme = false) {
        EarningsScreen()
    }
}

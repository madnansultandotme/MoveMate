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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.application.movemate.ui.theme.*

// Custom colors for Financial Dashboard
private val CardDark = Color(0xFF233348)
private val BorderDark = Color(0xFF324867)
private val TextSecondary = Color(0xFF92A9C9)
private val GreenAccent = Color(0xFF0BDA5E)
private val OrangeAccent = Color(0xFFF97316)
private val PurpleAccent = Color(0xFF8B5CF6)

// Data classes
data class TransactionItem(
    val title: String,
    val subtitle: String,
    val amount: String,
    val status: String,
    val statusColor: Color,
    val iconColor: Color,
    val icon: ImageVector,
    val isPositive: Boolean = false
)

data class DateFilter(
    val label: String,
    val isSelected: Boolean = false
)

class FinancialDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                FinancialDashboardScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialDashboardScreen(
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()

    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    val dateFilters = listOf(
        DateFilter("This Month", true),
        DateFilter("Last 7 Days"),
        DateFilter("Last 30 Days")
    )

    val transactions = listOf(
        TransactionItem(
            title = "Truck #402 - Carrier Logistics",
            subtitle = "Today, 10:45 AM • Payout",
            amount = "-\$400.00",
            status = "Completed",
            statusColor = GreenAccent,
            iconColor = Primary,
            icon = Icons.Default.LocalShipping
        ),
        TransactionItem(
            title = "Truck #891 - FastMove Inc.",
            subtitle = "Today, 09:12 AM • Payout",
            amount = "-\$1,250.00",
            status = "Pending",
            statusColor = OrangeAccent,
            iconColor = OrangeAccent,
            icon = Icons.Default.LocalShipping
        ),
        TransactionItem(
            title = "Shipper A - Load ID #992",
            subtitle = "Yesterday, 4:20 PM • Deposit",
            amount = "+\$2,400.00",
            status = "Received",
            statusColor = Color(0xFF64748B),
            iconColor = GreenAccent,
            icon = Icons.Default.Person,
            isPositive = true
        ),
        TransactionItem(
            title = "Refund - Shipper B",
            subtitle = "Yesterday, 1:15 PM • Refund",
            amount = "-\$50.00",
            status = "Processed",
            statusColor = Color(0xFF64748B),
            iconColor = Color(0xFFEF4444),
            icon = Icons.Default.Undo
        )
    )

    Scaffold(
        topBar = {
            FinancialTopBar(
                dateFilters = dateFilters,
                selectedFilterIndex = selectedFilterIndex,
                onFilterSelected = { selectedFilterIndex = it },
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            FinancialBottomNav()
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Stats
            item {
                HeroStatsSection()
            }

            // Secondary Stats
            item {
                SecondaryStatsSection()
            }

            // Revenue Trend Chart
            item {
                RevenueTrendChart()
            }

            // Recent Transactions
            item {
                RecentTransactionsSection(transactions = transactions)
            }

            // Bottom spacing for navigation
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FinancialTopBar(
    dateFilters: List<DateFilter>,
    selectedFilterIndex: Int,
    onFilterSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        tint = if (isDarkTheme) Color.White else Color(0xFF111827)
                    )
                }

                // Title
                Text(
                    text = "Financial Overview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF111827),
                    letterSpacing = (-0.5).sp
                )

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = "Hide Values",
                        tint = if (isDarkTheme) TextSecondary else Color(0xFF6B7280),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Export",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            // Date Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                dateFilters.forEachIndexed { index, filter ->
                    DateFilterChip(
                        label = filter.label,
                        isSelected = index == selectedFilterIndex,
                        onClick = { onFilterSelected(index) }
                    )
                }
            }

            // Bottom border
            HorizontalDivider(
                color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun DateFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Primary else {
            if (isDarkTheme) CardDark else Color(0xFFE5E7EB)
        },
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else {
                    if (isDarkTheme) Color.White else Color(0xFF374151)
                }
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (isSelected) Color.White else {
                    if (isDarkTheme) Color.White else Color(0xFF374151)
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun HeroStatsSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Volume Card
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total Volume",
            value = "\$1,240,500",
            percentageChange = "+5.0%",
            icon = Icons.Default.Payment,
            iconBackgroundColor = if (isDarkTheme) Color(0xFF1E3A5F) else Color(0xFFDBEAFE),
            iconColor = Primary,
            showBorder = false
        )

        // Net Revenue Card
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Net Revenue",
            value = "\$120,400",
            percentageChange = "+12.4%",
            icon = Icons.Default.AccountBalanceWallet,
            iconBackgroundColor = if (isDarkTheme) Color(0xFF3B1E54) else Color(0xFFF3E8FF),
            iconColor = PurpleAccent,
            showBorder = true
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    percentageChange: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconColor: Color,
    showBorder: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 4.dp,
                        color = Primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = GreenAccent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = percentageChange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF111827),
                letterSpacing = (-0.5).sp
            )
        }
    }
}

@Composable
private fun SecondaryStatsSection() {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pending Payouts
        SecondaryStatCard(
            modifier = Modifier.weight(1f),
            title = "Pending Payouts",
            value = "\$45,200",
            subtitle = "32 Carriers",
            icon = Icons.Default.Pending,
            iconColor = OrangeAccent
        )

        // Refunds Processed
        SecondaryStatCard(
            modifier = Modifier.weight(1f),
            title = "Refunds Processed",
            value = "\$1,250",
            subtitle = "4 Loaders",
            icon = Icons.Default.Replay,
            iconColor = Color(0xFFEF4444)
        )
    }
}

@Composable
private fun SecondaryStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isDarkTheme) BorderDark else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) TextSecondary else Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF111827),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) TextSecondary else Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun RevenueTrendChart() {
    val isDarkTheme = isSystemInDarkTheme()

    val barData = listOf(
        "Mon" to 0.40f,
        "Tue" to 0.65f,
        "Wed" to 0.35f,
        "Thu" to 0.85f,
        "Fri" to 0.60f,
        "Sat" to 0.45f,
        "Sun" to 0.30f
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Revenue Trend",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF111827)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        text = "See details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Primary
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Chart
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
                            color = if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB),
                            thickness = 1.dp
                        )
                    }
                }

                // Bars
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    barData.forEachIndexed { index, (day, height) ->
                        val isHighlighted = index == 4 // Friday
                        BarChartItem(
                            day = day,
                            heightFraction = height,
                            isHighlighted = isHighlighted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarChartItem(
    day: String,
    heightFraction: Float,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(heightFraction)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(
                    if (isHighlighted) Primary else Primary.copy(alpha = 0.2f)
                )
                .then(
                    if (isHighlighted) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            spotColor = Primary.copy(alpha = 0.3f)
                        )
                    } else Modifier
                )
        )

        // Day label
        Text(
            text = day,
            fontSize = 10.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) {
                if (isDarkTheme) Color.White else Color(0xFF111827)
            } else {
                if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
            }
        )
    }
}

@Composable
private fun RecentTransactionsSection(transactions: List<TransactionItem>) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Recent Transactions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF111827),
            letterSpacing = (-0.5).sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            transactions.forEach { transaction ->
                TransactionCard(transaction = transaction)
            }
        }

        // View All Button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDarkTheme) TextSecondary else Color(0xFF6B7280)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) BorderDark else Color(0xFFE5E7EB)
            )
        ) {
            Text(
                text = "View All Transactions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TransactionCard(transaction: TransactionItem) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) CardDark else Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(transaction.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = transaction.icon,
                    contentDescription = null,
                    tint = transaction.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Title and subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.subtitle,
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                )
            }

            // Amount and status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = transaction.amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isPositive) GreenAccent else {
                        if (isDarkTheme) Color.White else Color(0xFF111827)
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = transaction.statusColor
                )
            }
        }
    }
}

@Composable
private fun FinancialBottomNav() {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) Color(0xFF111822) else Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(80.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Dashboard,
                label = "Home",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.Monitoring,
                label = "Financials",
                isSelected = true
            )
            BottomNavItem(
                icon = Icons.Default.LocalShipping,
                label = "Loads",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.Group,
                label = "Users",
                isSelected = false
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = false
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean
) {
    val color = if (isSelected) Primary else Color(0xFF9CA3AF)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialDashboardScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        FinancialDashboardScreen()
    }
}


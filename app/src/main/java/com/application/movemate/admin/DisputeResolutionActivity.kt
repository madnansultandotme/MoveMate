package com.application.movemate.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.ui.theme.*
import kotlinx.coroutines.delay

// Custom colors for Dispute Resolution
private val SurfaceDark = Color(0xFF1E293B)
private val SurfaceLight = Color(0xFFFFFFFF)
private val SlateGray = Color(0xFF92A9C9)
private val RedAccent = Color(0xFFEF4444)
private val BlueAccent = Color(0xFF3B82F6)
private val EmeraldAccent = Color(0xFF10B981)

// Data class for party information
data class PartyInfo(
    val name: String,
    val role: String,
    val roleColor: Color,
    val roleBgColor: Color,
    val rating: Float,
    val trips: Int,
    val avatarUrl: String
)

// Data class for evidence item
data class EvidenceItem(
    val imageUrl: String?,
    val uploaderLabel: String,
    val uploadTime: String,
    val isPlaceholder: Boolean = false
)

class DisputeResolutionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                DisputeResolutionScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisputeResolutionScreen(
    disputeId: String = "9482",
    onBackClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Evidence", "Timeline", "Details")

    // SLA Timer state
    var hours by remember { mutableStateOf(23) }
    var minutes by remember { mutableStateOf(59) }
    var seconds by remember { mutableStateOf(0) }

    // Timer effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (seconds > 0) {
                seconds--
            } else if (minutes > 0) {
                minutes--
                seconds = 59
            } else if (hours > 0) {
                hours--
                minutes = 59
                seconds = 59
            }
        }
    }

    // Sample data
    val parties = listOf(
        PartyInfo(
            name = "John Doe",
            role = "LOADER",
            roleColor = BlueAccent,
            roleBgColor = BlueAccent.copy(alpha = 0.1f),
            rating = 4.8f,
            trips = 12,
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBCXdlKdis5rwb7cj_NiYfpWPS78gJjLPJySDQpcmZ09p6p22UKmnffQSAq4eJm6hDe1uKXOI_F5ExdrHyTzwifSgdGJgp4tjkiJGGIyGTWVJzFyjlRGoCUufc8d2uYBjeqGkKfuEkxrCiEhhZThRH6R8Cje3hWIFyt4krmx_h4meR__bL5_OPzpdy6jmYh1EvvT-TJnM4w8Y9jHhHlNP8RdTNWs-EIKVgam7IARtUr0I0fWCiYUFaG4t8BQVDvq35SHgdhwTTZP1Q"
        ),
        PartyInfo(
            name = "FastTrans Inc.",
            role = "CARRIER",
            roleColor = EmeraldAccent,
            roleBgColor = EmeraldAccent.copy(alpha = 0.1f),
            rating = 4.9f,
            trips = 154,
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB804qHIuZhhoDqb3GVnevRY4p25LnWPI_ioG8sq5nT-1ReAY7hNcDGn_t2Gi-cPYh3bsK2-APIUwLuOcDAgDWm2g20001wF6YtmkWN3pqsq5PvennrNeRI55UOxrGG59AsNpk8YUC5ugYjoVzOElXuf_O9xqFFaNUXRm5tFkUEPiXD5QLlNvppRKll_xFsCTL27d9PmA9Pm6r7y89Bb2pxBGnhqlqMPZOL64SzvKm7aNhGRw9pcnFF_ySkAp9GhL3D_2GOJo64ZRM"
        )
    )

    val evidenceItems = listOf(
        EvidenceItem(
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBMST29EG0Uw_EREgNDQUyWbnX1FLUhyELf9o_3ITYQowrXrfJtodvtULfZB7Kq6FZIjiN_zN1Y9kr5Ka40g4WW8ll9oMJl1QJ0_nFs_dtEGvQVA8nmuNT-a3fiTgS_Tjp6EXl-le35t74oM_Q8jDqMa1Ej4wUXwWzb9icAAcMvTYiv1Goj_YOYXnK8a-5rAhogutA1pAsAZpVjiTb8oNzBXGcE7fcUm5KeUtp6F_wONeVcd43OrXs7u8PPn6x2VZEbOdMreGAQY0s",
            uploaderLabel = "Loader Upload",
            uploadTime = "2 days ago"
        ),
        EvidenceItem(
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBkCCOLRp9VRdSFFEIlH0G_5NGgjsX3IjWaF99CiTUXbp6v2zygIcjtK_X6vv04078egrQn3AFrXQtD39OkHRS-62CZGeI3oFRL7VIZZBUCJaPyeBbf0ZW-m1LbfuXuTygBWnVbh3VGfBxe8Q8Y8yfp0HQVosQ5WFywMkrVzEaDsxP_2nXfNsGvUvA0Uju-3Dxun8y6mbn-f5vtpM4um1kRigj8gaYDfh3144_ZhnC4paheFHS5IAxhtKTCRkdobFKBeUSY13scyIc",
            uploaderLabel = "Carrier POD",
            uploadTime = "1 day ago"
        ),
        EvidenceItem(
            imageUrl = null,
            uploaderLabel = "",
            uploadTime = "",
            isPlaceholder = true
        )
    )

    Scaffold(
        topBar = {
            DisputeTopBar(
                disputeId = disputeId,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            DisputeBottomBar(
                onRequestInfoClick = { },
                onResolveClick = { }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
                .padding(paddingValues)
        ) {
            // Status & SLA Timer Section
            item {
                StatusAndTimerSection(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }

            // Dispute Summary Card
            item {
                DisputeSummaryCard()
            }

            // Involved Parties
            item {
                InvolvedPartiesSection(parties = parties)
            }

            // Evidence & Activity Tabs
            item {
                TabsSection(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Tab Content: Evidence Gallery
            item {
                EvidenceGallery(evidenceItems = evidenceItems)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisputeTopBar(
    disputeId: String,
    onBackClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }

            // Title
            Text(
                text = "Dispute #$disputeId",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                letterSpacing = (-0.5).sp
            )

            // Help Button
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Help,
                    contentDescription = "Help",
                    tint = SlateGray
                )
            }
        }
    }
}

@Composable
private fun StatusAndTimerSection(
    hours: Int,
    minutes: Int,
    seconds: Int
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Under Review Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RedAccent.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    RedAccent.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Gavel,
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "UNDER REVIEW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedAccent,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // High Priority Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Primary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Primary.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PriorityHigh,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "HIGH PRIORITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // SLA Timer Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else SurfaceLight,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
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
                        text = "SLA Resolution Deadline",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) SlateGray else Color(0xFF64748B)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = if (isDarkTheme) SlateGray else Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimerUnit(
                        value = hours,
                        label = "Hours",
                        modifier = Modifier.weight(1f)
                    )
                    TimerUnit(
                        value = minutes,
                        label = "Minutes",
                        modifier = Modifier.weight(1f)
                    )
                    TimerUnit(
                        value = seconds,
                        label = "Seconds",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerUnit(
    value: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            color = if (isDarkTheme) Color(0xFF233348) else Color(0xFFF1F5F9)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", value),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) SlateGray else Color(0xFF64748B)
        )
    }
}

@Composable
private fun DisputeSummaryCard() {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Dispute Summary",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else SurfaceLight,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Claim Amount
                    Column {
                        Text(
                            text = "CLAIM AMOUNT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) SlateGray else Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$450.00",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                            letterSpacing = (-0.5).sp
                        )
                    }

                    // Reason
                    Column {
                        Text(
                            text = "REASON",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) SlateGray else Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"Goods arrived with significant water damage. Packaging was soaked.\"",
                            fontSize = 14.sp,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF374151),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Evidence Image
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
                    )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuDpG1QMPyXAWXIkUpBq9Z6GYeXlpRfG-77J1bqNV-GDDhBWgarWGSqSrI7mvTIyMRUVPQVBugfJPceaSsgJlm2sfhza3u2hUyy0UQlMOqt7Yl5-8yPDLuTjgtbJ1vyuf6Rcj0Ry1Qq3LhRAJOUncObbyJ8Nbq2PqDon3FzIk-zb3jB1Jr6Qh1vJL8Cjvg4pmPYQ8LBOSS2YrZn0I9Ut1rrCRv4MPQDp87PebcNInw-kKi0LebGCiZjug849SYhzGtzE6Ez002sD9mI")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Damaged goods",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun InvolvedPartiesSection(parties: List<PartyInfo>) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Involved Parties",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
            Text(
                text = "View Contract",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Primary,
                modifier = Modifier.clickable { }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            parties.forEachIndexed { index, party ->
                PartyCard(
                    party = party,
                    indicatorColor = if (index == 0) BlueAccent else EmeraldAccent
                )
            }
        }
    }
}

@Composable
private fun PartyCard(
    party: PartyInfo,
    indicatorColor: Color
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        )
    ) {
        Box {
            // Left indicator bar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(party.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = party.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0),
                            CircleShape
                        )
                )

                // Name and details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = party.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = party.roleBgColor
                        ) {
                            Text(
                                text = party.role,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = party.roleColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFEAB308),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${party.rating} • ${party.trips} Trips",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) SlateGray else Color(0xFF64748B)
                        )
                    }
                }

                // Chat button
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { },
                    shape = CircleShape,
                    color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Chat,
                            contentDescription = "Chat",
                            tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabsSection(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = if (isDarkTheme) SurfaceDark else Color(0xFFE2E8F0)
        ) {
            Row(
                modifier = Modifier.padding(4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedTab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabSelected(index) },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) {
                            if (isDarkTheme) Primary else Color.White
                        } else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) {
                                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
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
}

@Composable
private fun EvidenceGallery(evidenceItems: List<EvidenceItem>) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            evidenceItems.take(2).forEach { item ->
                EvidenceCard(
                    item = item,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Placeholder card
            EvidenceCard(
                item = evidenceItems.last(),
                modifier = Modifier.weight(1f)
            )
            // Empty space to maintain grid
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun EvidenceCard(
    item: EvidenceItem,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
        border = if (item.isPlaceholder) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
            )
        } else null
    ) {
        if (item.isPlaceholder) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddPhotoAlternate,
                    contentDescription = "Add Photo",
                    tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Request Photo",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Evidence",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${item.uploaderLabel} • ${item.uploadTime}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DisputeBottomBar(
    onRequestInfoClick: () -> Unit,
    onResolveClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDarkTheme) SurfaceDark else SurfaceLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Request Info Button
            OutlinedButton(
                onClick = onRequestInfoClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDarkTheme) Color.White else Color(0xFF374151)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) Color(0xFF334155) else Color(0xFFD1D5DB)
                )
            ) {
                Text(
                    text = "Request Info",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Resolve Dispute Button
            Button(
                onClick = onResolveClick,
                modifier = Modifier
                    .weight(2f)
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
                    imageVector = Icons.Filled.Gavel,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Resolve Dispute",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisputeResolutionScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        DisputeResolutionScreen()
    }
}


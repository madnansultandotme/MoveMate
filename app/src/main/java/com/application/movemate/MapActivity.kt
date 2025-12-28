package com.application.movemate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.ui.theme.*
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

// Custom colors for the map view
private val GlassPanelBackground = Color(0xB31E1E1E) // rgba(30, 30, 30, 0.7)
private val GlassPanelBorder = Color(0x1AFFFFFF) // rgba(255, 255, 255, 0.1)
private val DarkCardBackground = Color(0xFF192433)
private val DarkButtonBackground = Color(0xFF233348)
private val SearchHintColor = Color(0xFF92A9C9)
private val GrayText = Color(0xFF9CA3AF)
private val DarkDivider = Color(0xFF374151)

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                MapScreen()
            }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var isFirstLocationUpdate by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Active Routes") }

    // Dispose of MapView when the composable leaves the composition
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                initLocationComponent(mapView) { isFirstLocationUpdate = false }
                setupGesturesListener(mapView)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                initLocationComponent(mapView) { isFirstLocationUpdate = false }
                setupGesturesListener(mapView)
            }
            else -> { }
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            initLocationComponent(mapView) { isFirstLocationUpdate = false }
            setupGesturesListener(mapView)
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full Screen Map
        AndroidView(
            factory = {
                mapView.apply {
                    getMapboxMap().loadStyle(Style.DARK)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Floating Section: Search & Chips
        TopSearchSection(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            selectedFilter = selectedFilter,
            onFilterChange = { selectedFilter = it }
        )

        // Right Side Controls (FABs)
        RightSideControls(
            onLayersClick = { /* Handle layers */ },
            onZoomIn = {
                val currentZoom = mapView.getMapboxMap().cameraState.zoom
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().zoom(currentZoom + 1).build())
            },
            onZoomOut = {
                val currentZoom = mapView.getMapboxMap().cameraState.zoom
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().zoom(currentZoom - 1).build())
            },
            onMyLocation = {
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
            },
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        // Bottom Sheet / Detail Card
        ShipmentDetailSheet(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun TopSearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("Active Routes", "Nearby Loads", "Available Trucks", "High Value")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.9f),
                        Color.Black.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = GlassPanelBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassPanelBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = SearchHintColor,
                    modifier = Modifier.padding(start = 16.dp)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Search loads, drivers, or routes",
                            color = SearchHintColor,
                            fontSize = 14.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = { /* Filter options */ },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .background(DarkButtonBackground, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Filter",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter =>
                FilterChipButton(
                    text = filter,
                    isSelected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) }
                )
            }
        }
    }
}

@Composable
fun FilterChipButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) Primary else DarkButtonBackground.copy(alpha = 0.9f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, GlassPanelBorder),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun RightSideControls(
    onLayersClick: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onMyLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(end = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map Layers FAB
        GlassFab(
            onClick = onLayersClick,
            icon = Icons.Outlined.Layers
        )

        // Zoom Controls Group
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GlassPanelBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassPanelBorder),
            shadowElevation = 8.dp
        ) {
            Column {
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Zoom In",
                        tint = Color.White
                    )
                }
                Divider(
                    color = GlassPanelBorder,
                    thickness = 1.dp
                )
                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "Zoom Out",
                        tint = Color.White
                    )
                }
            }
        }

        // Current Location FAB
        FloatingActionButton(
            onClick = onMyLocation,
            modifier = Modifier.size(48.dp),
            containerColor = Primary,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.MyLocation,
                contentDescription = "My Location"
            )
        }
    }
}

@Composable
fun GlassFab(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = GlassPanelBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassPanelBorder),
        shadowElevation = 8.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun ShipmentDetailSheet(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = DarkCardBackground,
        shadowElevation = 16.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            // Handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .background(Color(0xFF4B5563), RoundedCornerShape(3.dp))
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Driver / Status Header
                DriverHeader()

                // Route Timeline
                RouteTimeline()

                // Stats Grid
                StatsGrid()

                // Action Buttons
                ActionButtons()
            }
        }
    }
}

@Composable
fun DriverHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Driver Avatar with online indicator
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://lh3.googleusercontent.com/aida-public/AB6AXuBwDLq78zllQkIl_uIVpfxHVrNGFzxsyOQVRyyWUr_PQhOcVCbGDID3zqw7AGCeh28tLPGKN51ABXG9SCCIu9jl4LsDtqJb_OEDby0LsUdCC3NRbfOTcf4YRjaBCgRWZyoubmGatV1ZHMpR8UQ0TmYF3GBoEDn7BrX150FJYY4zYzQJcd04KAijCNOftScu10TtQ987yLODDrB3TcAW_92f8FReLQIrthneEA6fLGhPUwIcKn8znu9ulYutnV-UiNHA2gwd_yarkcg")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Driver Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4B5563))
                )
                // Online indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .background(Color(0xFF22C55E), CircleShape)
                        .border(2.dp, DarkCardBackground, CircleShape)
                )
            }

            Column {
                Text(
                    text = "Marcus J.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = GrayText,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "4.9 • Volvo VNL 760",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$1,250",
                color = Primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF22C55E).copy(alpha = 0.1f)
            ) {
                Text(
                    text = "On Time",
                    color = Color(0xFF4ADE80),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun RouteTimeline() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timeline dots and line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pickup dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, DarkCardBackground, CircleShape)
            )
            // Line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(32.dp)
                    .background(Color(0xFF374151))
            )
            // Dropoff dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Primary, CircleShape)
                    .border(2.dp, DarkCardBackground, CircleShape)
            )
        }

        // Route details
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "Pickup • 10:30 AM",
                    color = GrayText,
                    fontSize = 12.sp
                )
                Text(
                    text = "Distribution Center #4, Chicago, IL",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column {
                Text(
                    text = "Drop-off • 06:45 PM (Est.)",
                    color = GrayText,
                    fontSize = 12.sp
                )
                Text(
                    text = "Logistics Hub, Columbus, OH",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(label = "Distance", value = "342 mi", modifier = Modifier.weight(1f))
        StatCard(label = "Weight", value = "12k lbs", modifier = Modifier.weight(1f))
        StatCard(label = "Load ID", value = "#8492", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = DarkButtonBackground
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                color = GrayText,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Call Driver Button
        Button(
            onClick = { /* Call driver */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkButtonBackground
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Call",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Call Driver",
                fontWeight = FontWeight.Medium
            )
        }

        // Track Details Button
        Button(
            onClick = { /* Track details */ },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Navigation,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Track Details",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun hasLocationPermission(context: android.content.Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun initLocationComponent(mapView: MapView, onFirstFix: () -> Unit) {
    val locationComponentPlugin = mapView.location
    locationComponentPlugin.updateSettings {
        this.enabled = true
        locationPuck = createDefault2DPuck(withBearing = true)
    }
    val positionListener = object : OnIndicatorPositionChangedListener {
        override fun onIndicatorPositionChanged(point: com.mapbox.geojson.Point) {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(point).zoom(14.0).build())
            mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(point)
            locationComponentPlugin.removeOnIndicatorPositionChangedListener(this)
            onFirstFix()
        }
    }
    locationComponentPlugin.addOnIndicatorPositionChangedListener(positionListener)
    locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener(mapView))
}

private fun onIndicatorPositionChangedListener(mapView: MapView): OnIndicatorPositionChangedListener {
    return OnIndicatorPositionChangedListener { }
}

private fun onIndicatorBearingChangedListener(mapView: MapView): OnIndicatorBearingChangedListener {
    return OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }
}

private fun setupGesturesListener(mapView: MapView) {
    val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener(mapView))
            mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener(mapView))
            mapView.gestures.removeOnMoveListener(this)
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    mapView.gestures.addOnMoveListener(onMoveListener)
}

@Preview(showBackground = true)
@Composable
fun ShipmentDetailSheetPreview() {
    MoveMateTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101822))
        ) {
            ShipmentDetailSheet(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

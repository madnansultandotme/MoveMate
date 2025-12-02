package com.application.movemate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen()
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var isFirstLocationUpdate by remember { mutableStateOf(true) }

    // Dispose of MapView when the composable leaves the composition to prevent memory leaks
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
                // Precise location access granted.
                initLocationComponent(mapView) { isFirstLocationUpdate = false }
                setupGesturesListener(mapView)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                initLocationComponent(mapView) { isFirstLocationUpdate = false }
                setupGesturesListener(mapView)
            }
            else -> {
                // No location access granted.
            }
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
        AndroidView(
            factory = {
                mapView.apply {
                    getMapboxMap().loadStyle(Style.MAPBOX_STREETS)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        FloatingActionButton(
            onClick = {
                // Re-center the map on the user's location
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.0).build())
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = "My Location")
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
            // Only center on the first location update
            locationComponentPlugin.removeOnIndicatorPositionChangedListener(this)
            onFirstFix()
        }
    }
    locationComponentPlugin.addOnIndicatorPositionChangedListener(positionListener)
    locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener(mapView))
}

private fun onIndicatorPositionChangedListener(mapView: MapView): OnIndicatorPositionChangedListener {
    return OnIndicatorPositionChangedListener {
        // This listener is now only used for removal, the logic is moved to initLocationComponent
    }
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

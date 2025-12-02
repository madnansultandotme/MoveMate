package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.CreateShipmentViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.gestures.addOnMapClickListener

class CreateShipmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                CreateShipmentScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShipmentScreen(viewModel: CreateShipmentViewModel = viewModel()) {
    var currentStep by remember { mutableStateOf(1) }
    val shipmentCreated by viewModel.shipmentCreated.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    // Form fields
    var goodsType by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Small Truck") }

    var pickupAddress by remember { mutableStateOf("") }
    var pickupLat by remember { mutableStateOf(0.0) }
    var pickupLng by remember { mutableStateOf(0.0) }

    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryLat by remember { mutableStateOf(0.0) }
    var deliveryLng by remember { mutableStateOf(0.0) }

    var estimatedPrice by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(shipmentCreated) {
        if (shipmentCreated) {
            (context as? ComponentActivity)?.finish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Shipment - Step $currentStep/4") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (currentStep) {
                1 -> ShipmentDetailsStep(
                    goodsType = goodsType,
                    onGoodsTypeChange = { goodsType = it },
                    weight = weight,
                    onWeightChange = { weight = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    vehicleType = vehicleType,
                    onVehicleTypeChange = { vehicleType = it },
                    onNext = { currentStep = 2 }
                )

                2 -> PickupLocationStep(
                    address = pickupAddress,
                    onAddressChange = { pickupAddress = it },
                    onLocationSelected = { lat, lng ->
                        pickupLat = lat
                        pickupLng = lng
                    },
                    onNext = { currentStep = 3 },
                    onBack = { currentStep = 1 }
                )

                3 -> DeliveryLocationStep(
                    address = deliveryAddress,
                    onAddressChange = { deliveryAddress = it },
                    onLocationSelected = { lat, lng ->
                        deliveryLat = lat
                        deliveryLng = lng
                    },
                    onNext = { currentStep = 4 },
                    onBack = { currentStep = 2 }
                )

                4 -> PricingAndConfirmationStep(
                    estimatedPrice = estimatedPrice,
                    onEstimatedPriceChange = { estimatedPrice = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    goodsType = goodsType,
                    weight = weight,
                    pickupAddress = pickupAddress,
                    deliveryAddress = deliveryAddress,
                    vehicleType = vehicleType,
                    onConfirm = {
                        viewModel.createShipment(
                            goodsType = goodsType,
                            weight = weight.toDoubleOrNull() ?: 0.0,
                            description = description,
                            vehicleType = vehicleType,
                            pickupAddress = pickupAddress,
                            pickupLat = pickupLat,
                            pickupLng = pickupLng,
                            deliveryAddress = deliveryAddress,
                            deliveryLat = deliveryLat,
                            deliveryLng = deliveryLng,
                            estimatedPrice = estimatedPrice.toDoubleOrNull() ?: 0.0,
                            notes = notes
                        )
                    },
                    onBack = { currentStep = 3 }
                )
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun ShipmentDetailsStep(
    goodsType: String,
    onGoodsTypeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    vehicleType: String,
    onVehicleTypeChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column {
        Text(
            "Shipment Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = goodsType,
            onValueChange = onGoodsTypeChange,
            label = { Text("Goods Type") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., Electronics, Furniture, Food Items") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Vehicle Type Required", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val vehicleTypes = listOf("Small Truck", "Medium Truck", "Large Truck", "Van", "Pickup")
        vehicleTypes.forEach { type ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = vehicleType == type,
                    onClick = { onVehicleTypeChange(type) }
                )
                Text(type)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = goodsType.isNotEmpty() && weight.isNotEmpty()
        ) {
            Text("Next: Pickup Location")
        }
    }
}

@Composable
fun PickupLocationStep(
    address: String,
    onAddressChange: (String) -> Unit,
    onLocationSelected: (Double, Double) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedLat by remember { mutableStateOf(0.0) }
    var selectedLng by remember { mutableStateOf(0.0) }

    Column {
        Text(
            "Pickup Location",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Pickup Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Tap on map to select exact location", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Map View
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            MapPickerView(
                onLocationSelected = { lat, lng ->
                    selectedLat = lat
                    selectedLng = lng
                    onLocationSelected(lat, lng)
                }
            )
        }

        if (selectedLat != 0.0 && selectedLng != 0.0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Selected: $selectedLat, $selectedLng",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
            Button(
                onClick = onNext,
                enabled = address.isNotEmpty() && selectedLat != 0.0
            ) {
                Text("Next: Delivery Location")
            }
        }
    }
}

@Composable
fun DeliveryLocationStep(
    address: String,
    onAddressChange: (String) -> Unit,
    onLocationSelected: (Double, Double) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedLat by remember { mutableStateOf(0.0) }
    var selectedLng by remember { mutableStateOf(0.0) }

    Column {
        Text(
            "Delivery Location",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Delivery Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Tap on map to select exact location", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Map View
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            MapPickerView(
                onLocationSelected = { lat, lng ->
                    selectedLat = lat
                    selectedLng = lng
                    onLocationSelected(lat, lng)
                }
            )
        }

        if (selectedLat != 0.0 && selectedLng != 0.0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Selected: $selectedLat, $selectedLng",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
            Button(
                onClick = onNext,
                enabled = address.isNotEmpty() && selectedLat != 0.0
            ) {
                Text("Next: Pricing")
            }
        }
    }
}

@Composable
fun PricingAndConfirmationStep(
    estimatedPrice: String,
    onEstimatedPriceChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    goodsType: String,
    weight: String,
    pickupAddress: String,
    deliveryAddress: String,
    vehicleType: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    Column {
        Text(
            "Pricing & Confirmation",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Shipment Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Goods: $goodsType")
                Text("Weight: $weight kg")
                Text("Vehicle: $vehicleType")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Pickup: $pickupAddress", style = MaterialTheme.typography.bodySmall)
                Text("Delivery: $deliveryAddress", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estimatedPrice,
            onValueChange = onEstimatedPriceChange,
            label = { Text("Budget (Rs.)") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Amount you're willing to pay") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Additional Notes (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
            Button(
                onClick = onConfirm,
                enabled = estimatedPrice.isNotEmpty()
            ) {
                Text("Publish Shipment")
            }
        }
    }
}

@Composable
fun MapPickerView(onLocationSelected: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapboxMap().loadStyle(Style.MAPBOX_STREETS) {
                    // Set default camera to Pakistan (Karachi)
                    getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(67.0011, 24.8607))
                            .zoom(11.0)
                            .build()
                    )

                    val annotationApi = annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                    // Use gestures plugin for map clicks
                    gestures.addOnMapClickListener { point ->
                        onLocationSelected(point.latitude(), point.longitude())

                        // Clear previous annotations
                        pointAnnotationManager.deleteAll()

                        // Add new marker
                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(point)
                        pointAnnotationManager.create(pointAnnotationOptions)

                        true
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

package com.application.movemate.loader


import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.*
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

// Custom colors for Create Shipment
private val SurfaceDarkShipment = Color(0xFF233348)
private val TextSecondaryShipment = Color(0xFF92A9C9)
private val BorderDarkShipment = Color(0xFF324867)

class CreateShipmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val isDarkTheme = isSystemInDarkTheme()

    // Form fields - Step 1: Cargo Details
    var selectedCargoType by remember { mutableStateOf("Pallet") }
    var weight by remember { mutableStateOf("") }
    var weightUnit by remember { mutableStateOf("kg") }
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }

    // Step 2: Pickup
    var pickupAddress by remember { mutableStateOf("") }
    var pickupLat by remember { mutableStateOf(0.0) }
    var pickupLng by remember { mutableStateOf(0.0) }

    // Step 3: Delivery
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryLat by remember { mutableStateOf(0.0) }
    var deliveryLng by remember { mutableStateOf(0.0) }

    // Step 4: Review
    var estimatedPrice by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val steps = listOf("Cargo", "Pickup", "Delivery", "Review")

    LaunchedEffect(shipmentCreated) {
        if (shipmentCreated) {
            (context as? ComponentActivity)?.finish()
        }
    }

    Scaffold(
        topBar = {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = {
                            if (currentStep > 1) currentStep--
                            else (context as? ComponentActivity)?.finish()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Title
                    Text(
                        text = "Create Shipment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    // Cancel button
                    TextButton(
                        onClick = { (context as? ComponentActivity)?.finish() }
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryShipment
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Sticky Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) MaterialTheme.colorScheme.background.copy(alpha = 0.95f) else MaterialTheme.colorScheme.background
            ) {
                Column {
                    Divider(
                        color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Back button (shown from step 2)
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB)
                                )
                            ) {
                                Text(
                                    text = "Back",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF374151)
                                )
                            }
                        }

                        // Next/Submit button
                        Button(
                            onClick = {
                                if (currentStep < 4) {
                                    currentStep++
                                } else {
                                    viewModel.createShipment(
                                        goodsType = selectedCargoType,
                                        weight = weight.toDoubleOrNull() ?: 0.0,
                                        description = description,
                                        vehicleType = selectedCargoType,
                                        pickupAddress = pickupAddress,
                                        pickupLat = pickupLat,
                                        pickupLng = pickupLng,
                                        deliveryAddress = deliveryAddress,
                                        deliveryLat = deliveryLat,
                                        deliveryLng = deliveryLng,
                                        estimatedPrice = estimatedPrice.toDoubleOrNull() ?: 0.0,
                                        notes = notes
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(if (currentStep > 1) 2f else 1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = if (currentStep < 4) "Next Step" else "Create Shipment",
                                fontWeight = FontWeight.Bold
                            )
                            if (currentStep < 4) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress Indicators
            ProgressIndicatorSection(
                currentStep = currentStep,
                steps = steps
            )

            // Scrollable Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                when (currentStep) {
                    1 -> CargoDetailsStep(
                        selectedCargoType = selectedCargoType,
                        onCargoTypeChange = { selectedCargoType = it },
                        weight = weight,
                        onWeightChange = { weight = it },
                        weightUnit = weightUnit,
                        onWeightUnitChange = { weightUnit = it },
                        length = length,
                        onLengthChange = { length = it },
                        width = width,
                        onWidthChange = { width = it },
                        height = height,
                        onHeightChange = { height = it },
                        photoUri = photoUri,
                        onPhotoSelected = { photoUri = it }
                    )
                    2 -> PickupLocationStepNew(
                        address = pickupAddress,
                        onAddressChange = { pickupAddress = it },
                        onLocationSelected = { lat, lng ->
                            pickupLat = lat
                            pickupLng = lng
                        }
                    )
                    3 -> DeliveryLocationStepNew(
                        address = deliveryAddress,
                        onAddressChange = { deliveryAddress = it },
                        onLocationSelected = { lat, lng ->
                            deliveryLat = lat
                            deliveryLng = lng
                        }
                    )
                    4 -> ReviewStep(
                        cargoType = selectedCargoType,
                        weight = weight,
                        weightUnit = weightUnit,
                        pickupAddress = pickupAddress,
                        deliveryAddress = deliveryAddress,
                        estimatedPrice = estimatedPrice,
                        onEstimatedPriceChange = { estimatedPrice = it },
                        notes = notes,
                        onNotesChange = { notes = it }
                    )
                }

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressIndicatorSection(currentStep: Int, steps: List<String>) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            // Step labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, step ->
                    Text(
                        text = step,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (index + 1 == currentStep) Primary
                               else TextSecondaryShipment
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                steps.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (index + 1 <= currentStep) Primary
                                else if (isDarkTheme) BorderDarkShipment else Color(0xFFD1D5DB)
                            )
                    )
                }
            }
        }
    }

    Divider(
        color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
        thickness = 1.dp
    )
}

@Composable
fun CargoDetailsStep(
    selectedCargoType: String,
    onCargoTypeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    weightUnit: String,
    onWeightUnitChange: (String) -> Unit,
    length: String,
    onLengthChange: (String) -> Unit,
    width: String,
    onWidthChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    photoUri: Uri?,
    onPhotoSelected: (Uri?) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onPhotoSelected(uri) }

    val cargoTypes = listOf(
        CargoTypeItem("Pallet", Icons.Outlined.ViewInAr),
        CargoTypeItem("Box", Icons.Outlined.Inventory2),
        CargoTypeItem("Vehicle", Icons.Outlined.DirectionsCar),
        CargoTypeItem("Furniture", Icons.Outlined.Chair),
        CargoTypeItem("Machinery", Icons.Outlined.PrecisionManufacturing)
    )

    Column {
        // Headline
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Step 1: Cargo Details",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "What kind of items are you shipping? Provide accurate details for the best quotes.",
                fontSize = 14.sp,
                color = TextSecondaryShipment,
                lineHeight = 20.sp
            )
        }

        // Cargo Type Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Cargo Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Cargo type chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cargoTypes.take(3).forEach { cargoType ->
                    CargoTypeChip(
                        label = cargoType.label,
                        icon = cargoType.icon,
                        isSelected = selectedCargoType == cargoType.label,
                        onClick = { onCargoTypeChange(cargoType.label) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cargoTypes.drop(3).forEach { cargoType ->
                    CargoTypeChip(
                        label = cargoType.label,
                        icon = cargoType.icon,
                        isSelected = selectedCargoType == cargoType.label,
                        onClick = { onCargoTypeChange(cargoType.label) }
                    )
                }
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .height(1.dp)
                .background(if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB))
        )

        // Weight Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Weight",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
                )

                // Unit toggle
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFE5E7EB)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        UnitToggleButton(
                            text = "kg",
                            isSelected = weightUnit == "kg",
                            onClick = { onWeightUnitChange("kg") }
                        )
                        UnitToggleButton(
                            text = "lbs",
                            isSelected = weightUnit == "lbs",
                            onClick = { onWeightUnitChange("lbs") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weight input
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Scale,
                        contentDescription = "Weight",
                        tint = TextSecondaryShipment
                    )
                    TextField(
                        value = weight,
                        onValueChange = onWeightChange,
                        placeholder = {
                            Text("0", color = Color(0xFF9CA3AF))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Primary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Dimensions Section
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dimensions (L x W x H)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
                )
                Text(
                    text = "in cm",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondaryShipment
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DimensionInput(
                    label = "LENGTH",
                    value = length,
                    onValueChange = onLengthChange,
                    modifier = Modifier.weight(1f)
                )
                DimensionInput(
                    label = "WIDTH",
                    value = width,
                    onValueChange = onWidthChange,
                    modifier = Modifier.weight(1f)
                )
                DimensionInput(
                    label = "HEIGHT",
                    value = height,
                    onValueChange = onHeightChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Photo Upload Section
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Cargo Photos (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { photoPickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) MaterialTheme.colorScheme.background else Color(0xFFF9FAFB),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (photoUri != null) Primary
                    else if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (photoUri != null) Icons.Filled.CheckCircle else Icons.Outlined.AddAPhoto,
                            contentDescription = "Upload",
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (photoUri != null) "Photo uploaded" else "Tap to upload",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (photoUri != null) Primary else MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (photoUri != null) "Tap to change" else "Supports JPG, PNG",
                        fontSize = 12.sp,
                        color = TextSecondaryShipment
                    )
                }
            }
        }
    }
}

data class CargoTypeItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun CargoTypeChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Primary
               else if (isDarkTheme) SurfaceDarkShipment else Color(0xFFE5E7EB),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Primary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White
                       else if (isDarkTheme) Color.White else Color(0xFF475569),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White
                        else if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun UnitToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) {
            if (isDarkTheme) Color(0xFF4B5563) else Color.White
        } else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
                if (isDarkTheme) Color.White else Color(0xFF0F172A)
            } else {
                if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF64748B)
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DimensionInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondaryShipment
            )
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "0",
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Keep the remaining step composables for compatibility
@Composable
fun PickupLocationStepNew(
    address: String,
    onAddressChange: (String) -> Unit,
    onLocationSelected: (Double, Double) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    var selectedLat by remember { mutableStateOf(0.0) }
    var selectedLng by remember { mutableStateOf(0.0) }

    Column {
        // Headline
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Step 2: Pickup Location",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Where should the carrier pick up your shipment?",
                fontSize = 14.sp,
                color = TextSecondaryShipment,
                lineHeight = 20.sp
            )
        }

        // Address input
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Pickup Address",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
                TextField(
                    value = address,
                    onValueChange = onAddressChange,
                    placeholder = {
                        Text("Enter full address", color = Color(0xFF9CA3AF))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Map
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Or tap on map to select location",
                fontSize = 14.sp,
                color = TextSecondaryShipment
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
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
                    text = "Selected: ${"%.4f".format(selectedLat)}, ${"%.4f".format(selectedLng)}",
                    fontSize = 12.sp,
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun DeliveryLocationStepNew(
    address: String,
    onAddressChange: (String) -> Unit,
    onLocationSelected: (Double, Double) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    var selectedLat by remember { mutableStateOf(0.0) }
    var selectedLng by remember { mutableStateOf(0.0) }

    Column {
        // Headline
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Step 3: Delivery Location",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Where should the shipment be delivered?",
                fontSize = 14.sp,
                color = TextSecondaryShipment,
                lineHeight = 20.sp
            )
        }

        // Address input
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Delivery Address",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
                TextField(
                    value = address,
                    onValueChange = onAddressChange,
                    placeholder = {
                        Text("Enter full address", color = Color(0xFF9CA3AF))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Map
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Or tap on map to select location",
                fontSize = 14.sp,
                color = TextSecondaryShipment
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
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
                    text = "Selected: ${"%.4f".format(selectedLat)}, ${"%.4f".format(selectedLng)}",
                    fontSize = 12.sp,
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun ReviewStep(
    cargoType: String,
    weight: String,
    weightUnit: String,
    pickupAddress: String,
    deliveryAddress: String,
    estimatedPrice: String,
    onEstimatedPriceChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        // Headline
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Step 4: Review & Submit",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please review your shipment details before submitting.",
                fontSize = 14.sp,
                color = TextSecondaryShipment,
                lineHeight = 20.sp
            )
        }

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isDarkTheme) SurfaceDarkShipment else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) BorderDarkShipment else Color(0xFFE5E7EB)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ReviewItem(label = "Cargo Type", value = cargoType)
                Divider(
                    color = if (isDarkTheme) BorderDarkShipment else Color(0xFFE5E7EB),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                ReviewItem(label = "Weight", value = "$weight $weightUnit")
                Divider(
                    color = if (isDarkTheme) BorderDarkShipment else Color(0xFFE5E7EB),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                ReviewItem(label = "Pickup", value = pickupAddress.ifEmpty { "Not specified" })
                Divider(
                    color = if (isDarkTheme) BorderDarkShipment else Color(0xFFE5E7EB),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                ReviewItem(label = "Delivery", value = deliveryAddress.ifEmpty { "Not specified" })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Price & Notes
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Your Budget (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryShipment
                    )
                    TextField(
                        value = estimatedPrice,
                        onValueChange = onEstimatedPriceChange,
                        placeholder = {
                            Text("0.00", color = Color(0xFF9CA3AF))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Primary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Additional Notes",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDarkTheme) SurfaceDarkShipment else Color(0xFFF3F4F6)
            ) {
                TextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    placeholder = {
                        Text("Any special instructions...", color = Color(0xFF9CA3AF))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }
    }
}

@Composable
fun ReviewItem(label: String, value: String) {
    val isDarkTheme = isSystemInDarkTheme()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondaryShipment
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Keep the MapPickerView for compatibility
@Composable
fun MapPickerView(onLocationSelected: (Double, Double) -> Unit) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
                    getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(-98.0, 39.5))
                            .zoom(3.0)
                            .build()
                    )
                }
                gestures.addOnMapClickListener { point ->
                    onLocationSelected(point.latitude(), point.longitude())
                    val annotationApi = annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                    pointAnnotationManager.deleteAll()
                    true
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun CreateShipmentScreenPreview() {
    MoveMateTheme {
        CreateShipmentScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CreateShipmentScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        CreateShipmentScreen()
    }
}

package com.application.movemate.carrier

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.CarrierLoadDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_SHIPMENT_ID = "SHIPMENT_ID"

class LoadDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val context = androidx.compose.ui.platform.LocalContext.current

    var showBidDialog by remember { mutableStateOf(false) }
    var bidAmount by remember { mutableStateOf("") }
    var estimatedDays by remember { mutableStateOf("") }
    var bidMessage by remember { mutableStateOf("") }

    LaunchedEffect(shipmentId) {
        if (shipmentId.isNotEmpty()) {
            viewModel.fetchShipment(shipmentId)
        }
    }

    if (showBidDialog) {
        AlertDialog(
            onDismissRequest = { showBidDialog = false },
            title = { Text("Place Your Bid") },
            text = {
                Column {
                    OutlinedTextField(
                        value = bidAmount,
                        onValueChange = { bidAmount = it },
                        label = { Text("Bid Amount (Rs.)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your price") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = estimatedDays,
                        onValueChange = { estimatedDays = it },
                        label = { Text("Estimated Delivery (days)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Number of days") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bidMessage,
                        onValueChange = { bidMessage = it },
                        label = { Text("Message (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Add a message to the loader") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitBid(
                            shipmentId = shipmentId,
                            bidAmount = bidAmount.toDoubleOrNull() ?: 0.0,
                            estimatedDays = estimatedDays.toIntOrNull() ?: 0,
                            message = bidMessage
                        )
                        showBidDialog = false
                    },
                    enabled = bidAmount.isNotEmpty() && estimatedDays.isNotEmpty()
                ) {
                    Text("Submit Bid")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBidDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Load Details") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                shipment == null -> Text("Shipment not found", modifier = Modifier.align(Alignment.Center))
                else -> {
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    shipment!!.goodsType,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Budget: Rs. ${shipment!!.estimatedPrice}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Route Information
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Route Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Text("Pickup Location", style = MaterialTheme.typography.labelMedium)
                                Text(shipment!!.pickupAddress, style = MaterialTheme.typography.bodyLarge)

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Delivery Location", style = MaterialTheme.typography.labelMedium)
                                Text(shipment!!.deliveryAddress, style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Shipment Details
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Shipment Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                DetailRow("Weight", "${shipment!!.weight} kg")
                                DetailRow("Vehicle Type", shipment!!.vehicleType)
                                DetailRow("Posted By", shipment!!.loaderName)
                                DetailRow("Posted On", dateFormat.format(Date(shipment!!.createdAt)))

                                if (shipment!!.description.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Description", style = MaterialTheme.typography.labelMedium)
                                    Text(shipment!!.description, style = MaterialTheme.typography.bodyMedium)
                                }

                                if (shipment!!.notes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Additional Notes", style = MaterialTheme.typography.labelMedium)
                                    Text(shipment!!.notes, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bid Status/Action
                        if (bidSubmitted) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "✅ Bid Submitted Successfully!",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "The loader will review your bid and notify you if accepted.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { showBidDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Place Your Bid")
                            }
                        }

                        error?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

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

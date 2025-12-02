package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Shipment
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.LoaderMyShipmentsViewModel
import java.text.SimpleDateFormat
import java.util.*

class LoaderMyShipmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                LoaderMyShipmentsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderMyShipmentsScreen(viewModel: LoaderMyShipmentsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchMyShipments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Shipments") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (shipments.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No shipments yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Create your first shipment to get started",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(shipments) { shipment ->
                        ShipmentCard(shipment = shipment)
                    }
                }
            }
        }
    }
}

@Composable
fun ShipmentCard(shipment: Shipment) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val statusColor = when (shipment.status) {
        ShipmentStatus.OPEN_FOR_BIDS -> MaterialTheme.colorScheme.primary
        ShipmentStatus.ASSIGNED -> MaterialTheme.colorScheme.tertiary
        ShipmentStatus.PICKED_UP, ShipmentStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
        ShipmentStatus.DELIVERED, ShipmentStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    shipment.goodsType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = statusColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        shipment.status.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Route", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            Text("${shipment.pickupAddress} → ${shipment.deliveryAddress}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Weight", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("${shipment.weight} kg", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Budget", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("Rs. ${shipment.estimatedPrice}", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Created", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(dateFormat.format(Date(shipment.createdAt)), style = MaterialTheme.typography.bodySmall)
                }
            }

            if (shipment.carrierName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Carrier: ${shipment.carrierName}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


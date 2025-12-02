package com.application.movemate.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.ShipmentViewModel

class ShipmentMonitoringActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                ShipmentMonitoringScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentMonitoringScreen(viewModel: ShipmentViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shipment Monitoring") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Filters can be added here

            LazyColumn {
                items(shipments) { shipment ->
                    ShipmentListItem(shipment = shipment)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ShipmentListItem(shipment: Shipment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("ID: ${shipment.id}", style = MaterialTheme.typography.titleMedium)
            Text("Loader: ${shipment.loaderName} | Carrier: ${shipment.carrierName ?: "Not assigned"}")
            Text("Route: ${shipment.pickupAddress} -> ${shipment.deliveryAddress}")
            Text("Status: ${shipment.status.name}", style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(onClick = { /* View Shipment */ }) {
            Text("View")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShipmentMonitoringScreenPreview() {
    MoveMateTheme {
        ShipmentMonitoringScreen()
    }
}

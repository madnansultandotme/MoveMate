package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.ActiveShipmentsViewModel

class ActiveShipmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                ActiveShipmentsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveShipmentsScreen(viewModel: ActiveShipmentsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()

    LaunchedEffect(Unit) {
        // Replace with actual loader ID
        viewModel.fetchActiveShipments("loader_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Active Shipments") })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(shipments) { shipment ->
                ActiveShipmentListItem(shipment = shipment)
                Divider()
            }
        }
    }
}

@Composable
fun ActiveShipmentListItem(shipment: Shipment) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Shipment ID: ${shipment.id}", style = MaterialTheme.typography.titleMedium)
        Text("Status: ${shipment.status}")
    }
}

@Preview(showBackground = true)
@Composable
fun ActiveShipmentsScreenPreview() {
    MoveMateTheme {
        ActiveShipmentsScreen()
    }
}

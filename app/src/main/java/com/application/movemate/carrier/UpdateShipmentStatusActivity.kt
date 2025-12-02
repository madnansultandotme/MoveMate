package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.UpdateShipmentStatusViewModel

class UpdateShipmentStatusActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shipmentId = intent.getStringExtra(EXTRA_SHIPMENT_ID)
        setContent {
            MoveMateTheme {
                UpdateShipmentStatusScreen(shipmentId = shipmentId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateShipmentStatusScreen(
    shipmentId: String?,
    viewModel: UpdateShipmentStatusViewModel = viewModel()
) {
    var selectedStatus by remember { mutableStateOf("") }
    val statuses = listOf("En Route to Pickup", "Arrived at Pickup", "Loaded & Departing", "In Transit", "Arrived at Delivery", "Unloaded & Completed")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Update Shipment Status") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Shipment ID: $shipmentId", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            statuses.forEach { status ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(status)
                    RadioButton(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (shipmentId != null) {
                        viewModel.updateShipmentStatus(shipmentId, selectedStatus)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedStatus.isNotEmpty()
            ) {
                Text("Submit Status")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateShipmentStatusScreenPreview() {
    MoveMateTheme {
        UpdateShipmentStatusScreen(shipmentId = "dummy_id")
    }
}


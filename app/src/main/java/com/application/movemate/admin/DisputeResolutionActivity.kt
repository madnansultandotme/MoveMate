package com.application.movemate.admin

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
import com.application.movemate.models.Dispute
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.DisputeViewModel

class DisputeResolutionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                DisputeResolutionScreen()
            }
        }
    }
}

data class Dispute(
    val id: String,
    val shipmentId: String,
    val raisedBy: String,
    val reason: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisputeResolutionScreen(viewModel: DisputeViewModel = viewModel()) {
    val disputes by viewModel.disputes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dispute Center") })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            items(disputes) { dispute ->
                DisputeListItem(dispute = dispute)
                Divider()
            }
        }
    }
}

@Composable
fun DisputeListItem(dispute: Dispute) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Dispute ID: ${dispute.id}", style = MaterialTheme.typography.titleMedium)
        Text("Shipment ID: ${dispute.shipmentId} | Raised by: ${dispute.raisedBy}")
        Text("Reason: ${dispute.reason}")
        Text("Status: ${dispute.status}", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun DisputeResolutionScreenPreview() {
    MoveMateTheme {
        DisputeResolutionScreen()
    }
}

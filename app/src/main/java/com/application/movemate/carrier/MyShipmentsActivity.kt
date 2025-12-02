package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.MyShipmentsViewModel

class MyShipmentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                MyShipmentsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShipmentsScreen(viewModel: MyShipmentsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()

    LaunchedEffect(Unit) {
        // Replace with actual carrier ID
        viewModel.fetchMyShipments("carrier_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Shipments") })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(shipments) { shipment ->
                MyShipmentListItem(shipment = shipment)
                Divider()
            }
        }
    }
}

@Composable
fun MyShipmentListItem(shipment: Shipment) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Shipment ID: ${shipment.id}", style = MaterialTheme.typography.titleMedium)
        Text("Status: ${shipment.status}")
    }
}

@Preview(showBackground = true)
@Composable
fun MyShipmentsScreenPreview() {
    MoveMateTheme {
        MyShipmentsScreen()
    }
}

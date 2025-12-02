package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.Transaction
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.EarningsViewModel

class EarningsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                EarningsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(viewModel: EarningsViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(Unit) {
        // Replace with actual carrier ID
        viewModel.fetchEarnings("carrier_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Earnings Overview") })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            item {
                Text("Total Earnings (This Month): $2,500", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(transactions) { transaction ->
                EarningListItem(transaction = transaction)
                Divider()
            }
        }
    }
}

@Composable
fun EarningListItem(transaction: Transaction) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Shipment ID: ${transaction.shipmentId}", style = MaterialTheme.typography.titleMedium)
        Text("Amount: $${transaction.amount}")
        Text("Status: ${transaction.status}")
    }
}

@Preview(showBackground = true)
@Composable
fun EarningsScreenPreview() {
    MoveMateTheme {
        EarningsScreen()
    }
}

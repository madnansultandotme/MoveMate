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
import com.application.movemate.models.Transaction
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.FinancialViewModel

class FinancialDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                FinancialDashboardScreen()
            }
        }
    }
}

data class Transaction(
    val id: String,
    val shipmentId: String,
    val loader: String,
    val carrier: String,
    val amount: Double,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialDashboardScreen(viewModel: FinancialViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Financial Overview") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Summary cards can be added here

            LazyColumn {
                items(transactions) { transaction ->
                    TransactionListItem(transaction = transaction)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Payment ID: ${transaction.id}", style = MaterialTheme.typography.titleMedium)
        Text("Shipment ID: ${transaction.shipmentId}")
        Text("Amount: $${transaction.amount} | Status: ${transaction.status}")
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialDashboardScreenPreview() {
    MoveMateTheme {
        FinancialDashboardScreen()
    }
}

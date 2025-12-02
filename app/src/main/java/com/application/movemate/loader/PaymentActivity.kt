package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.PaymentViewModel

class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                PaymentsAndInvoicesScreen()
            }
        }
    }
}

data class Invoice(
    val id: String,
    val shipmentId: String,
    val amount: Double,
    val status: String, // "Paid" or "Unpaid"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsAndInvoicesScreen(viewModel: PaymentViewModel = viewModel()) {
    var selectedInvoiceToPay by remember { mutableStateOf<Invoice?>(null) }
    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(Unit) {
        // Replace with actual loader ID
        viewModel.fetchTransactions("loader_id_1")
    }

    if (selectedInvoiceToPay != null) {
        PayNowScreen(invoice = selectedInvoiceToPay!!, onDismiss = { selectedInvoiceToPay = null })
    } else {
        val invoices = remember {
            listOf(
                Invoice("INV-001", "SH123", 150.0, "Unpaid"),
                Invoice("INV-002", "SH456", 250.0, "Paid"),
                Invoice("INV-003", "SH789", 300.0, "Unpaid"),
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Payments & Invoices") })
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                // Summary Section
                Text("Total Spend this Month: $700.00", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // Invoice List
                LazyColumn {
                    items(invoices) { invoice ->
                        InvoiceListItem(
                            invoice = invoice,
                            onPayNow = { selectedInvoiceToPay = it }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceListItem(invoice: Invoice, onPayNow: (Invoice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Invoice #${invoice.id}", style = MaterialTheme.typography.titleMedium)
            Text("Shipment: ${invoice.shipmentId}")
            Text("Amount: $${invoice.amount}")
        }
        if (invoice.status == "Unpaid") {
            Button(onClick = { onPayNow(invoice) }) {
                Text("Pay Now")
            }
        } else {
            Text("Status: ${invoice.status}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PayNowScreen(invoice: Invoice, onDismiss: () -> Unit) {
    var cardNumber by remember { mutableStateOf("") }
    var cardError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pay Invoice #${invoice.id}") },
        text = {
            Column {
                Text("Amount: $${invoice.amount}")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        cardNumber = it
                        cardError = false
                    },
                    label = { Text("Enter Card Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = cardError,
                    singleLine = true
                )
                if (cardError) {
                    Text("Invalid card number length.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (cardNumber.length in 15..16) {
                    // Dummy payment success
                    onDismiss()
                } else {
                    cardError = true
                }
            }) {
                Text("Pay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PaymentsAndInvoicesScreenPreview() {
    MoveMateTheme {
        PaymentsAndInvoicesScreen()
    }
}

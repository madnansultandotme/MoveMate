package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.PayoutRequestViewModel
import com.application.movemate.viewmodels.PayoutState

class PayoutRequestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                PayoutRequestScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutRequestScreen(viewModel: PayoutRequestViewModel = viewModel()) {
    var amount by remember { mutableStateOf("") }
    val payoutState by viewModel.payoutState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Request Payout") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Available Balance: PKR 5000", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount to Withdraw") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Replace with actual carrier ID
                    viewModel.requestPayout("carrier_id_1", amount.toDoubleOrNull() ?: 0.0)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotEmpty() && payoutState != PayoutState.Loading
            ) {
                Text("Submit Request")
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (payoutState) {
                is PayoutState.Loading -> CircularProgressIndicator()
                is PayoutState.Success -> Text("Payout request submitted successfully!", color = MaterialTheme.colorScheme.primary)
                is PayoutState.Error -> Text("Error: ${(payoutState as PayoutState.Error).message}", color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PayoutRequestScreenPreview() {
    MoveMateTheme {
        PayoutRequestScreen()
    }
}

package com.application.movemate.carrier

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.RoleSelectionActivity
import com.application.movemate.auth.CarrierAuthViewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.CarrierViewModel

class CarrierDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                CarrierDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierDashboardScreen(
    viewModel: CarrierViewModel = viewModel(),
    authViewModel: CarrierAuthViewModel = viewModel()
) {
    val carrier by viewModel.carrier.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Replace with actual carrier ID after authentication
        viewModel.fetchCarrier("carrier_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrier Dashboard") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        val intent = Intent(context, RoleSelectionActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Welcome, Carrier!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryCard("Active Deliveries", "3")
                SummaryCard("Available Loads", "15")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Quick Actions
            Button(
                onClick = {
                    context.startActivity(Intent(context, AvailableLoadsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Browse Available Loads")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, MyShipmentsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Active Deliveries")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, CarrierMyBidsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Bids")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, EarningsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Earnings")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, PayoutRequestActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request Payout")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, CarrierProfileSettingsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Profile & Verification")
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierDashboardScreenPreview() {
    MoveMateTheme {
        CarrierDashboardScreen()
    }
}

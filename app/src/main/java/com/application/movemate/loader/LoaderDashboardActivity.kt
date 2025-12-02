package com.application.movemate.loader

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
import com.application.movemate.auth.LoaderAuthViewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.LoaderViewModel

class LoaderDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                LoaderDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderDashboardScreen(
    viewModel: LoaderViewModel = viewModel(),
    authViewModel: LoaderAuthViewModel = viewModel()
) {
    val loader by viewModel.loader.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Replace with actual loader ID after authentication
        viewModel.fetchLoader("loader_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loader Dashboard") },
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
            Text("Welcome, Loader!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryCard("My Shipments", "5")
                SummaryCard("Pending Bids", "12")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Quick Actions
            Button(
                onClick = {
                    context.startActivity(Intent(context, CreateShipmentActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create New Shipment")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoaderMyShipmentsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Shipments")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoaderManageBidsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage Carrier Bids")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, PaymentActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Payments & Invoices")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, LoaderProfileSettingsActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Profile & KYC Status")
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
fun LoaderDashboardScreenPreview() {
    MoveMateTheme {
        LoaderDashboardScreen()
    }
}

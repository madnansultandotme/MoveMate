package com.application.movemate.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                AdminDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: AdminAuthViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(user) {
        if (user == null) {
            val intent = Intent(context, AdminLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                actions = {
                    IconButton(onClick = { /* Handle search */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            NavigationDrawer()
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main content goes here
                Text("Welcome, Admin!")
            }
        }
    }
}

@Composable
fun NavigationDrawer() {
    val context = LocalContext.current
    Column(modifier = Modifier.width(240.dp).padding(16.dp)) {
        Text("Dashboard", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { context.startActivity(Intent(context, UserManagementActivity::class.java)) }) {
            Text("User Management")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { context.startActivity(Intent(context, ShipmentMonitoringActivity::class.java)) }) {
            Text("Shipments")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { context.startActivity(Intent(context, FinancialDashboardActivity::class.java)) }) {
            Text("Financials")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { context.startActivity(Intent(context, AnalyticsReportingActivity::class.java)) }) {
            Text("Analytics")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { context.startActivity(Intent(context, DisputeResolutionActivity::class.java)) }) {
            Text("Disputes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("System Settings")
    }
}


@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    MoveMateTheme {
        AdminDashboardScreen()
    }
}

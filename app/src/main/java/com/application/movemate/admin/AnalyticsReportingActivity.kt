package com.application.movemate.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.movemate.ui.theme.MoveMateTheme

class AnalyticsReportingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                AnalyticsReportingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsReportingScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics & Reports") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("User Growth Report", style = MaterialTheme.typography.titleMedium)
            // Placeholder for chart
            Spacer(modifier = Modifier.height(16.dp))
            Text("Shipments Performance", style = MaterialTheme.typography.titleMedium)
            // Placeholder for chart
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Export CSV */ }) {
                Text("Export CSV")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsReportingScreenPreview() {
    MoveMateTheme {
        AnalyticsReportingScreen()
    }
}


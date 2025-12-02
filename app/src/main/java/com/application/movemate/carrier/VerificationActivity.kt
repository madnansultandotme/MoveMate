package com.application.movemate.carrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.VerificationViewModel

class VerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                VerificationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(viewModel: VerificationViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Verification Center") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Verification Status: Pending", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(progress = 0.25f, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Required Documents", style = MaterialTheme.typography.titleMedium)
            DocumentUploadItem("Driver License")
            DocumentUploadItem("Vehicle Registration")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Submit for Review */ }) {
                Text("Submit for Review")
            }
        }
    }
}

@Composable
fun DocumentUploadItem(documentName: String, viewModel: VerificationViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    var documentUrl by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(documentName)
        Button(onClick = { showDialog = true }) {
            Text("Upload")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Upload $documentName") },
            text = {
                OutlinedTextField(
                    value = documentUrl,
                    onValueChange = { documentUrl = it },
                    label = { Text("Document URL") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Replace with actual carrier ID
                        viewModel.uploadDocument("carrier_id_1", documentName, documentUrl)
                        showDialog = false
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    MoveMateTheme {
        VerificationScreen()
    }
}

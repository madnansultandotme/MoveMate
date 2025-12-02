package com.application.movemate.loader

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
import com.application.movemate.models.Loader
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.LoaderProfileViewModel
import com.application.movemate.viewmodels.ProfileSaveState

class LoaderProfileSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                LoaderProfileSettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderProfileSettingsScreen(viewModel: LoaderProfileViewModel = viewModel()) {
    val tabs = listOf("Personal Info", "Payment Methods", "Security")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val profile by viewModel.profile.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile("loader_id_1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile Settings") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                        Text(title, modifier = Modifier.padding(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (selectedTabIndex) {
                0 -> PersonalInfoTab(viewModel, profile)
                1 -> PaymentMethodsTab(viewModel, profile)
                2 -> SecurityTab()
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (saveState) {
                is ProfileSaveState.Saving -> LinearProgressIndicator()
                is ProfileSaveState.Success -> Text("Profile updated successfully!", color = MaterialTheme.colorScheme.primary)
                is ProfileSaveState.Error -> Text((saveState as ProfileSaveState.Error).message, color = MaterialTheme.colorScheme.error)
                else -> {}
            }
        }
    }
}

@Composable
fun PersonalInfoTab(viewModel: LoaderProfileViewModel, profile: Loader?) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }

    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.savePersonalInfo("loader_id_1", name, phone, email)
        }) {
            Text("Save")
        }
    }
}

@Composable
fun PaymentMethodsTab(viewModel: LoaderProfileViewModel, profile: Loader?) {
    var cardNumber by remember { mutableStateOf(profile?.payment?.cardNumber ?: "") }
    var accountNumber by remember { mutableStateOf(profile?.payment?.accountNumber ?: "") }

    Column {
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("Account Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.savePaymentMethod("loader_id_1", cardNumber, accountNumber)
        }) {
            Text("Save")
        }
    }
}

@Composable
fun SecurityTab() {
    Text("Security settings coming soon.")
}

@Preview(showBackground = true)
@Composable
fun LoaderProfileSettingsScreenPreview() {
    MoveMateTheme {
        LoaderProfileSettingsScreen()
    }
}

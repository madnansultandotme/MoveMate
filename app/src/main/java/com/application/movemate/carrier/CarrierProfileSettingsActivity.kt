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
import com.application.movemate.models.Carrier
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.CarrierProfileViewModel

class CarrierProfileSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                CarrierProfileSettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierProfileSettingsScreen(viewModel: CarrierProfileViewModel = viewModel()) {
    val tabs = listOf("Personal Info", "Vehicle Info", "Bank Details", "Security")
    var selectedTab by remember { mutableStateOf(0) }
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProfile("carrier_id_1") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile Settings") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
                        Text(title, modifier = Modifier.padding(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (selectedTab) {
                0 -> PersonalInfoTab(viewModel, profile)
                1 -> VehicleInfoTab(viewModel, profile)
                2 -> BankDetailsTab(viewModel, profile)
                else -> SecurityTab()
            }
        }
    }
}

@Composable
fun PersonalInfoTab(viewModel: CarrierProfileViewModel, profile: Carrier?) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }

    Column {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.savePersonalInfo("carrier_id_1", name, email) }) {
            Text("Save")
        }
    }
}

@Composable
fun VehicleInfoTab(viewModel: CarrierProfileViewModel, profile: Carrier?) {
    var companyName by remember { mutableStateOf(profile?.companyName ?: "") }
    var totalVehicles by remember { mutableStateOf(profile?.totalVehicles?.toString() ?: "") }

    Column {
        OutlinedTextField(value = companyName, onValueChange = { companyName = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = totalVehicles, onValueChange = { totalVehicles = it }, label = { Text("Total Vehicles") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Update to use new save method */ }) {
            Text("Save")
        }
    }
}

@Composable
fun BankDetailsTab(viewModel: CarrierProfileViewModel, profile: Carrier?) {
    var bankName by remember { mutableStateOf(profile?.bankName ?: "") }
    var accountNumber by remember { mutableStateOf(profile?.accountNumber ?: "") }

    Column {
        OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = accountNumber, onValueChange = { accountNumber = it }, label = { Text("Account Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.saveBankDetails("carrier_id_1", bankName, accountNumber) }) {
            Text("Save")
        }
    }
}

@Composable
fun SecurityTab() {
    Text("Security features coming soon.")
}

@Preview(showBackground = true)
@Composable
fun CarrierProfileSettingsScreenPreview() {
    MoveMateTheme {
        CarrierProfileSettingsScreen()
    }
}

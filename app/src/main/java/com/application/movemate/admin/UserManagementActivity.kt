package com.application.movemate.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.User
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.UserManagementViewModel

class UserManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                UserManagementScreen()
            }
        }
    }
}

data class User(
    val id: String,
    val name: String,
    val role: String,
    val email: String,
    val verificationStatus: String,
    val registrationDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(viewModel: UserManagementViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("User Management") })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FilterDropdown(
                    label = "Role",
                    items = listOf("Loader", "Carrier", "Admin")
                )
                FilterDropdown(
                    label = "Status",
                    items = listOf("Active", "Suspended", "Pending")
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search by Name/Email/Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // User List
            LazyColumn {
                items(users) { user ->
                    UserListItem(user = user)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(label: String, items: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(items[0]) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedItem)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        selectedItem = item
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(user.name, style = MaterialTheme.typography.titleMedium)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
            Text("Role: ${user.role} | Status: ${user.verificationStatus}", style = MaterialTheme.typography.bodySmall)
        }
        Row {
            TextButton(onClick = { /* View */ }) {
                Text("View")
            }
            TextButton(onClick = { /* Suspend */ }) {
                Text("Suspend")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserManagementScreenPreview() {
    MoveMateTheme {
        UserManagementScreen()
    }
}

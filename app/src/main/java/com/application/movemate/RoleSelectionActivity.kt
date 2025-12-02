package com.application.movemate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.application.movemate.admin.AdminLoginActivity
import com.application.movemate.carrier.CarrierLoginActivity
import com.application.movemate.loader.LoaderLoginActivity
import com.application.movemate.ui.theme.MoveMateTheme

class RoleSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                RoleSelectionScreen()
            }
        }
    }
}

@Composable
fun RoleSelectionScreen() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select Your Role", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { context.startActivity(Intent(context, AdminLoginActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Admin")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { context.startActivity(Intent(context, LoaderLoginActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Loader")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { context.startActivity(Intent(context, CarrierLoginActivity::class.java)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Carrier")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    MoveMateTheme {
        RoleSelectionScreen()
    }
}

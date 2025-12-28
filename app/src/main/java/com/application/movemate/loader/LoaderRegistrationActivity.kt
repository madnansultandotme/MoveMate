package com.application.movemate.loader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.R
import com.application.movemate.auth.LoaderAuthViewModel
import com.application.movemate.ui.theme.*

// Custom colors for Loader Registration
private val DarkInputBackground = Color(0xFF192433)
private val DarkInputBorder = Color(0xFF324867)
private val LightInputBorder = Color(0xFFCBD5E1)
private val TextSecondaryLightReg = Color(0xFF64748B)
private val TextSecondaryDarkReg = Color(0xFF94A3B8)
private val InputHintDarkReg = Color(0xFF92A9C9)

class LoaderRegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderRegistrationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderRegistrationScreen(viewModel: LoaderAuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var selectedShipType by remember { mutableStateOf("Full Truckload") }
    var documentUri by remember { mutableStateOf<Uri?>(null) }

    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        documentUri = uri
    }

    val shipTypes = listOf(
        ShipTypeOption("Full Truckload", Icons.Outlined.LocalShipping),
        ShipTypeOption("Part Load", Icons.Outlined.Inventory2),
        ShipTypeOption("Perishables", Icons.Outlined.AcUnit)
    )

    LaunchedEffect(user) {
        if (user != null) {
            context.startActivity(Intent(context, LoaderDashboardActivity::class.java))
        }
    }

    Scaffold(
        topBar = {
            // Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { (context as? ComponentActivity)?.finish() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Title
                    Text(
                        text = "Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 40.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shadowElevation = 0.dp
            ) {
                Column {
                    Divider(
                        color = if (isDarkTheme) DarkInputBackground else Color(0xFFE2E8F0),
                        thickness = 1.dp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Create Account Button
                        Button(
                            onClick = {
                                viewModel.register(fullName, email, "")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Create",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already have an account?",
                                fontSize = 14.sp,
                                color = if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log in",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                                modifier = Modifier.clickable {
                                    context.startActivity(Intent(context, LoaderLoginActivity::class.java))
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Progress Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "STEP 1 OF 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Step 1 - Active
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Primary)
                    )
                    // Step 2 - Inactive
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isDarkTheme) DarkInputBorder else Color(0xFFCBD5E1))
                    )
                    // Step 3 - Inactive
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isDarkTheme) DarkInputBorder else Color(0xFFCBD5E1))
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            ) {
                // Header Section
                Column(
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Create your Loader Account",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start moving goods instantly. Enter your business details below.",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg,
                        lineHeight = 24.sp
                    )
                }

                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Full Name
                    LoaderRegTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "John Doe"
                    )

                    // Work Email
                    LoaderRegTextField(
                        label = "Work Email",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "name@company.com"
                    )

                    // Phone Number with Prefix
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Phone Number",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkTheme) DarkInputBackground else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkTheme) DarkInputBorder else LightInputBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Country code prefix
                                Row(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .border(
                                            width = 0.dp,
                                            color = Color.Transparent,
                                            shape = RoundedCornerShape(0.dp)
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+1",
                                        fontSize = 16.sp,
                                        color = if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Divider
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(if (isDarkTheme) DarkInputBorder else LightInputBorder)
                                )

                                // Phone input
                                TextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    placeholder = {
                                        Text(
                                            "(555) 000-0000",
                                            color = if (isDarkTheme) InputHintDarkReg else TextSecondaryLightReg
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = Primary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                                    ),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Company Name
                    LoaderRegTextField(
                        label = "Company Name",
                        value = companyName,
                        onValueChange = { companyName = it },
                        placeholder = "Logistics Co."
                    )

                    // Shipping Type Chips
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "What do you typically ship?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            shipTypes.forEach { shipType ->
                                ShipTypeChip(
                                    text = shipType.label,
                                    icon = shipType.icon,
                                    isSelected = selectedShipType == shipType.label,
                                    onClick = { selectedShipType = shipType.label }
                                )
                            }
                        }
                    }

                    // Upload Zone
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Business Verification",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filePickerLauncher.launch("*/*") },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDarkTheme) DarkInputBackground.copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (documentUri != null) Primary
                                else if (isDarkTheme) DarkInputBorder else LightInputBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Upload Icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (documentUri != null) Primary.copy(alpha = 0.2f)
                                            else if (isDarkTheme) DarkInputBorder else Color(0xFFE2E8F0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (documentUri != null) Icons.Filled.CheckCircle else Icons.Outlined.CloudUpload,
                                        contentDescription = "Upload",
                                        tint = if (documentUri != null) Primary
                                               else if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (documentUri != null) "Document uploaded" else "Upload Tax ID / Registration",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (documentUri != null) Primary else MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (documentUri != null) "Tap to change" else "PDF, JPG or PNG up to 5MB",
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg
                                )
                            }
                        }
                    }

                    // Error message
                    error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Bottom spacer
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

data class ShipTypeOption(
    val label: String,
    val icon: ImageVector
)

@Composable
fun LoaderRegTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) DarkInputBackground else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) DarkInputBorder else LightInputBorder
            )
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = if (isDarkTheme) InputHintDarkReg else TextSecondaryLightReg
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                singleLine = true
            )
        }
    }
}

@Composable
fun ShipTypeChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) {
            if (isDarkTheme) Primary else Primary.copy(alpha = 0.1f)
        } else {
            if (isDarkTheme) DarkInputBackground else Color.White
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Primary
            else if (isDarkTheme) DarkInputBorder else LightInputBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) {
                    if (isDarkTheme) Color.White else Primary
                } else {
                    if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg
                },
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) {
                    if (isDarkTheme) Color.White else Primary
                } else {
                    if (isDarkTheme) TextSecondaryDarkReg else TextSecondaryLightReg
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoaderRegistrationScreenPreview() {
    MoveMateTheme {
        LoaderRegistrationScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoaderRegistrationScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderRegistrationScreen()
    }
}

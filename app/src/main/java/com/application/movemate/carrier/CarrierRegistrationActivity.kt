package com.application.movemate.carrier

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.R
import com.application.movemate.auth.CarrierAuthViewModel
import com.application.movemate.ui.theme.*

// Custom colors for Registration
private val SurfaceDark = Color(0xFF192433)
private val BorderDark = Color(0xFF324867)
private val TextSecondary = Color(0xFF92A9C9)
private val LightBorder = Color(0xFFD1D5DB)

class CarrierRegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                CarrierRegistrationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrierRegistrationScreen(viewModel: CarrierAuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var selectedCountryCode by remember { mutableStateOf("+1") }
    var selectedVehicleType by remember { mutableStateOf("") }
    var licenseUri by remember { mutableStateOf<Uri?>(null) }
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }

    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        licenseUri = uri
    }

    val vehicleTypes = listOf(
        "Cargo Van" to "van",
        "Flatbed Truck" to "flatbed",
        "Semi-Trailer" to "semi",
        "Box Truck" to "box"
    )

    LaunchedEffect(user) {
        if (user != null) {
            context.startActivity(Intent(context, CarrierDashboardActivity::class.java))
        }
    }

    Scaffold(
        topBar = {
            // Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = { (context as? ComponentActivity)?.finish() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Title
                    Text(
                        text = "Join MoveMate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 40.dp) // Balance the back button
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) BorderDark.copy(alpha = 0.3f) else Color(0xFFE2E8F0)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Continue Button
                    Button(
                        onClick = {
                            // For now, register with available data
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
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Continue",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Terms text
                    Text(
                        text = buildAnnotatedString {
                            append("By registering, you agree to our ")
                            withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Medium)) {
                                append("Terms of Service")
                            }
                            append(" & ")
                            withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Medium)) {
                                append("Privacy Policy")
                            }
                        },
                        fontSize = 11.sp,
                        color = if (isDarkTheme) TextSecondary.copy(alpha = 0.6f) else Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
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
            // Progress Stepper
            RegistrationStepper(currentStep = 1)

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp, bottom = 100.dp)
            ) {
                // Headline
                Text(
                    text = "Create your account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We need your personal details to verify your eligibility to drive on the MoveMate network.",
                    fontSize = 16.sp,
                    color = if (isDarkTheme) TextSecondary else Color(0xFF64748B),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form Fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Full Name Field
                    RegistrationTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Enter your legal name",
                        leadingIcon = Icons.Outlined.Person
                    )

                    // Email Field
                    RegistrationTextField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "name@example.com",
                        leadingIcon = Icons.Outlined.Email
                    )

                    // Mobile Number Field
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Mobile Number",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Country Code Dropdown
                            CountryCodeDropdown(
                                selectedCode = selectedCountryCode,
                                onCodeSelected = { selectedCountryCode = it },
                                modifier = Modifier.width(100.dp)
                            )

                            // Phone Number Input
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkTheme) SurfaceDark else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkTheme) BorderDark else LightBorder
                                )
                            ) {
                                TextField(
                                    value = mobileNumber,
                                    onValueChange = { mobileNumber = it },
                                    placeholder = {
                                        Text(
                                            "(555) 000-0000",
                                            color = if (isDarkTheme) TextSecondary.copy(alpha = 0.7f) else Color(0xFF94A3B8)
                                        )
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        cursorColor = Primary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                                        unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                    ),
                                    modifier = Modifier.fillMaxSize(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Vehicle Type Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Primary Vehicle Type",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = vehicleDropdownExpanded,
                            onExpandedChange = { vehicleDropdownExpanded = it }
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDarkTheme) SurfaceDark else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkTheme) BorderDark else LightBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { vehicleDropdownExpanded = true }
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_local_shipping),
                                        contentDescription = "Vehicle",
                                        tint = if (isDarkTheme) TextSecondary else Color(0xFF94A3B8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (selectedVehicleType.isEmpty()) "Select vehicle type"
                                               else vehicleTypes.find { it.second == selectedVehicleType }?.first ?: "",
                                        color = if (selectedVehicleType.isEmpty()) {
                                            if (isDarkTheme) TextSecondary.copy(alpha = 0.7f) else Color(0xFF94A3B8)
                                        } else {
                                            if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B)
                                    )
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = vehicleDropdownExpanded,
                                onDismissRequest = { vehicleDropdownExpanded = false }
                            ) {
                                vehicleTypes.forEach { (label, value) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            selectedVehicleType = value
                                            vehicleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Document Upload Zone
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Driver's License",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color.White else Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDarkTheme) SurfaceDark.copy(alpha = 0.4f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (licenseUri != null) Primary else if (isDarkTheme) BorderDark else LightBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Upload Icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (licenseUri != null) Primary.copy(alpha = 0.2f)
                                            else if (isDarkTheme) BorderDark else Color(0xFFE2E8F0)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (licenseUri != null) Icons.Filled.CheckCircle else Icons.Outlined.UploadFile,
                                        contentDescription = "Upload",
                                        tint = if (licenseUri != null) Primary
                                               else if (isDarkTheme) TextSecondary else Color(0xFF64748B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = if (licenseUri != null) "License uploaded" else "Tap to upload license",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (licenseUri != null) Primary else MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (licenseUri != null) "Tap to change" else "Supports JPG, PNG (Max 5MB)",
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) TextSecondary else Color(0xFF64748B)
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
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationStepper(currentStep: Int) {
    val isDarkTheme = isSystemInDarkTheme()
    val steps = listOf("Profile", "Company", "Vehicle")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            steps.forEachIndexed { index, step ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (index < currentStep) Primary
                                else if (isDarkTheme) BorderDark else Color(0xFFE2E8F0)
                            )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Step label
                    Text(
                        text = step.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = if (index < currentStep) FontWeight.Bold else FontWeight.Medium,
                        color = if (index < currentStep) Primary
                                else if (isDarkTheme) TextSecondary else Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color.White else Color(0xFF334155),
            modifier = Modifier.padding(start = 4.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) BorderDark else LightBorder
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    tint = if (isDarkTheme) TextSecondary else Color(0xFF94A3B8),
                    modifier = Modifier.padding(start = 16.dp)
                )

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = if (isDarkTheme) TextSecondary.copy(alpha = 0.7f) else Color(0xFF94A3B8)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Primary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    singleLine = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeDropdown(
    selectedCode: String,
    onCodeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    var expanded by remember { mutableStateOf(false) }

    val countryCodes = listOf(
        "🇺🇸" to "+1",
        "🇬🇧" to "+44",
        "🇮🇳" to "+91"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) SurfaceDark else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) BorderDark else LightBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = countryCodes.find { it.second == selectedCode }?.first ?: "🇺🇸",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedCode,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { (flag, code) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(code)
                        }
                    },
                    onClick = {
                        onCodeSelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierRegistrationScreenPreview() {
    MoveMateTheme {
        CarrierRegistrationScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CarrierRegistrationScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierRegistrationScreen()
    }
}

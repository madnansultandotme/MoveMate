package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.models.Loader
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.LoaderProfileViewModel
import com.application.movemate.viewmodels.ProfileSaveState

// Custom colors for Profile Settings
private val SurfaceDarkProfile = Color(0xFF192433)
private val TextSecondaryProfile = Color(0xFF92A9C9)
private val BorderDarkProfile = Color(0xFF324867)

class LoaderProfileSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val profile by viewModel.profile.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    // Form states
    var fullName by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by remember { mutableStateOf("+1 (555) 012-3456") }
    var companyName by remember { mutableStateOf("Doe Logistics LLC") }
    var taxId by remember { mutableStateOf("US-987654321") }
    var businessAddress by remember { mutableStateOf("123 Market Street, Suite 400, San Francisco, CA") }
    var preferredVehicle by remember { mutableStateOf("Box Truck (16ft)") }
    var defaultCargo by remember { mutableStateOf("Palletized Goods") }
    var shipmentUpdates by remember { mutableStateOf(true) }
    var promotionalEmails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile("loader_id_1")
    }

    Scaffold(
        topBar = {
            // Top App Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = { (context as? ComponentActivity)?.finish() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                    }

                    // Title
                    Text(
                        text = "Profile Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    // Spacer for visual balance
                    Spacer(modifier = Modifier.size(40.dp))
                }

                HorizontalDivider(
                    color = if (isDarkTheme) BorderDarkProfile.copy(alpha = 0.3f) else Color(0xFFE5E7EB),
                    thickness = 1.dp
                )
            }
        },
        bottomBar = {
            // Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) BackgroundDark.copy(alpha = 0.95f) else Color(0xFFF6F7F8).copy(alpha = 0.95f)
            ) {
                Column {
                    HorizontalDivider(
                        color = if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB),
                        thickness = 1.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavItem(
                            icon = Icons.Outlined.Home,
                            label = "Home",
                            isSelected = false,
                            onClick = { /* Navigate to home */ }
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.LocalShipping,
                            label = "Shipments",
                            isSelected = false,
                            onClick = { /* Navigate to shipments */ }
                        )
                        BottomNavItem(
                            icon = Icons.Outlined.AccountBalanceWallet,
                            label = "Wallet",
                            isSelected = false,
                            onClick = { /* Navigate to wallet */ }
                        )
                        BottomNavItem(
                            icon = Icons.Filled.Person,
                            label = "Profile",
                            isSelected = true,
                            onClick = { /* Already on profile */ }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            ProfileHeader(
                avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA_Nd22MOrH6OKMjy3MepgySWD9qRCvhvrnauMHctOrcYUrkz7pQxzMB76viplqvZ_-JHR7I-_rdcdv9zrtM0ULXVN8deZpgVpEJ-Ora8KW0AznYAZdPYu2tj-rt1iDT7VPee30k-VFbLlHB5NyarCZ22Sj16m52SSPqTqYoO25fQTcme_Ssj05mPdkjAjrF5kbpa8bjhDTP3s7hGz3Botsd5AfhY_rJz3XKHAW4y0IpCffxj60DVk8Tzviz7L-5Cue15LVYegJLn8",
                name = fullName,
                accountType = "Loader Account",
                isVerified = true,
                onEditClick = { /* Edit avatar */ }
            )

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Section 1: Account Information
                ProfileSection(title = "Account Information") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProfileTextField(
                            label = "Full Name",
                            value = fullName,
                            onValueChange = { fullName = it }
                        )
                        ProfileTextField(
                            label = "Email Address",
                            value = email,
                            onValueChange = { email = it }
                        )
                        ProfileTextField(
                            label = "Phone Number",
                            value = phone,
                            onValueChange = { phone = it }
                        )
                        ProfileNavigationRow(
                            icon = Icons.Outlined.Lock,
                            title = "Change Password",
                            onClick = { /* Navigate to change password */ }
                        )
                    }
                }

                // Section 2: Business Details
                ProfileSection(title = "Business Details") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProfileTextField(
                            label = "Company Name",
                            value = companyName,
                            onValueChange = { companyName = it }
                        )
                        ProfileTextField(
                            label = "Tax ID / VAT",
                            value = taxId,
                            onValueChange = { taxId = it }
                        )
                        ProfileTextArea(
                            label = "Business Address",
                            value = businessAddress,
                            onValueChange = { businessAddress = it }
                        )
                    }
                }

                // Section 3: Shipping Preferences
                ProfileSection(title = "Shipping Preferences") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ProfileNavigationRow(
                            icon = Icons.Outlined.LocationOn,
                            title = "Default Pickup Address",
                            subtitle = "Home",
                            onClick = { /* Navigate to pickup address */ }
                        )
                        ProfileDropdown(
                            label = "Preferred Vehicle Type",
                            value = preferredVehicle,
                            options = listOf("Box Truck (16ft)", "Cargo Van", "Flatbed", "Semi-Truck"),
                            onValueChange = { preferredVehicle = it }
                        )
                        ProfileDropdown(
                            label = "Default Cargo Category",
                            value = defaultCargo,
                            options = listOf("Palletized Goods", "Loose Boxes", "Furniture", "Machinery"),
                            onValueChange = { defaultCargo = it }
                        )
                    }
                }

                // Section 4: Notifications
                ProfileSection(title = "Notifications") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB)
                        )
                    ) {
                        Column {
                            NotificationToggle(
                                title = "Shipment Updates",
                                subtitle = "Get notified when status changes",
                                isChecked = shipmentUpdates,
                                onCheckedChange = { shipmentUpdates = it },
                                showDivider = true
                            )
                            NotificationToggle(
                                title = "Promotional Emails",
                                subtitle = "News and special offers",
                                isChecked = promotionalEmails,
                                onCheckedChange = { promotionalEmails = it },
                                showDivider = false
                            )
                        }
                    }
                }

                // Footer Actions
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.savePersonalInfo("loader_id_1", fullName, phone, email)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = { /* Log out */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // Save state feedback
                when (saveState) {
                    is ProfileSaveState.Saving -> LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Primary
                    )
                    is ProfileSaveState.Success -> Text(
                        "Profile updated successfully!",
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Medium
                    )
                    is ProfileSaveState.Error -> Text(
                        (saveState as ProfileSaveState.Error).message,
                        color = Color(0xFFEF4444)
                    )
                    else -> {}
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    avatarUrl: String,
    name: String,
    accountType: String,
    isVerified: Boolean,
    onEditClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with edit button
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(
                        4.dp,
                        if (isDarkTheme) SurfaceDarkProfile else Color.White,
                        CircleShape
                    )
                    .shadow(8.dp, CircleShape)
            )

            // Edit button
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onEditClick() },
                shape = CircleShape,
                color = Primary,
                border = androidx.compose.foundation.BorderStroke(
                    4.dp,
                    if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
                ),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name with verified badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
            if (isVerified) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verified",
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = accountType,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) TextSecondaryProfile else Color(0xFF64748B)
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
            modifier = Modifier.padding(start = 4.dp, bottom = 16.dp)
        )
        content()
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                unfocusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                focusedBorderColor = Primary,
                unfocusedBorderColor = if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB),
                focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            ),
            singleLine = true
        )
    }
}

@Composable
private fun ProfileTextArea(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                unfocusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                focusedBorderColor = Primary,
                unfocusedBorderColor = if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB),
                focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            ),
            maxLines = 3
        )
    }
}

@Composable
private fun ProfileNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) SurfaceDarkProfile else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryProfile else Color(0xFF64748B)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = if (isDarkTheme) Color(0xFF9CA3AF) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDropdown(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                    unfocusedContainerColor = if (isDarkTheme) SurfaceDarkProfile else Color.White,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = if (isDarkTheme) BorderDarkProfile else Color(0xFFE5E7EB),
                    focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF64748B)
                    )
                },
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationToggle(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isDarkTheme) TextSecondaryProfile else Color(0xFF64748B),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = if (isDarkTheme) Color(0xFF374151) else Color(0xFFE5E7EB),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = if (isDarkTheme) BorderDarkProfile else Color(0xFFF3F4F6),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Primary else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Primary else Color(0xFF9CA3AF)
        )
    }
}

// Keep old composables for compatibility
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
    MoveMateTheme(darkTheme = true) {
        LoaderProfileSettingsScreen()
    }
}

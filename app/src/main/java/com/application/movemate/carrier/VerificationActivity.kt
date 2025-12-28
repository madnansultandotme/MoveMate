package com.application.movemate.carrier

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.R
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.VerificationViewModel

// Custom colors for Verification
private val SurfaceDark = Color(0xFF1C2633)
private val BorderDark = Color(0xFF334155)
private val AmberLight = Color(0xFFFEF3C7)
private val AmberBorder = Color(0xFFFDE68A)
private val AmberIcon = Color(0xFFD97706)
private val AmberIconDark = Color(0xFFF59E0B)
private val GreenSuccess = Color(0xFF16A34A)
private val GreenSuccessDark = Color(0xFF4ADE80)
private val PurpleAccent = Color(0xFF9333EA)
private val PurpleAccentDark = Color(0xFFC084FC)

class VerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    var fullName by remember { mutableStateOf("Alex Morgan") }
    var govIdNumber by remember { mutableStateOf("") }
    var driversLicenseUri by remember { mutableStateOf<Uri?>(null) }
    var vehicleRegistrationUri by remember { mutableStateOf<Uri?>(Uri.parse("uploaded")) } // Simulating already uploaded
    var cargoInsuranceUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // File picker launchers
    val driversLicensePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> driversLicenseUri = uri }

    val vehicleRegistrationPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> vehicleRegistrationUri = uri }

    val cargoInsurancePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> cargoInsuranceUri = uri }

    Scaffold(
        topBar = {
            // Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                shadowElevation = 0.dp
            ) {
                Column {
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
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Title
                        Text(
                            text = "Verification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 40.dp)
                        )
                    }

                    // Bottom border
                    Divider(
                        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE5E7EB),
                        thickness = 1.dp
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Column {
                    Divider(
                        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE5E7EB),
                        thickness = 1.dp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Submit Button
                        Button(
                            onClick = { /* Submit for verification */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text(
                                text = "Submit for Verification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Submit",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Security text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.alpha(0.6f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Secure",
                                tint = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Data is encrypted and securely stored.",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
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
                .padding(bottom = 24.dp)
        ) {
            // Progress Bar Section
            VerificationProgressBar(currentStep = 2, totalSteps = 4, progress = 0.5f)

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Text
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Verify Your Identity",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "To start accepting shipments on MoveMate, please complete the following steps to validate your carrier profile.",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        lineHeight = 24.sp
                    )
                }

                // Status Banner
                StatusBanner()

                // Personal Details Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Personal Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Full Legal Name
                    VerificationTextField(
                        label = "Full Legal Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "e.g. Johnathan Doe"
                    )

                    // Government ID Number
                    VerificationTextField(
                        label = "Government ID Number",
                        value = govIdNumber,
                        onValueChange = { govIdNumber = it },
                        placeholder = "Enter ID Number",
                        trailingIcon = Icons.Outlined.Badge
                    )
                }

                // Required Documents Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Required Documents",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Driver's License
                    DocumentUploadCard(
                        title = "Driver's License",
                        subtitle = "Upload front and back",
                        icon = Icons.Outlined.Badge,
                        iconBackgroundColor = Color(0xFFEFF6FF),
                        iconBackgroundColorDark = Color(0xFF1E3A5F),
                        iconTint = Primary,
                        isUploaded = driversLicenseUri != null,
                        onUpload = { driversLicensePicker.launch("image/*") },
                        onDelete = { driversLicenseUri = null }
                    )

                    // Vehicle Registration (already uploaded example)
                    DocumentUploadCard(
                        title = "Vehicle Registration",
                        subtitle = "Uploaded",
                        icon = Icons.Outlined.Description,
                        iconBackgroundColor = Color(0xFFEFF6FF),
                        iconBackgroundColorDark = Color(0xFF1E3A5F),
                        iconTint = Primary,
                        isUploaded = true,
                        hasPreview = true,
                        previewUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAalusOtFZeZxUetwZds2qfO39zHBohb5ggr187W6MSHi7y0FdlR00jJmxjZB4PaSgaYfuENU6749dCPOKdEUNwt1i3Bqwa4qoyDeL7_xStNQsQD1MRlwWK3O81jwH9ZT9JLZxsOSnICvQv4VEHxV6fTF0YmDR4g1CJBbTermQpO7RR8DCcEvUscc5MEkGD_9dmtjRhtUOOvBMZPkfX8loOV0h5zy3E96fEgg1FrsL1KrJpiAtjmepITGtQkW8Uj70-uHp-zsnwtWE",
                        onUpload = { vehicleRegistrationPicker.launch("image/*") },
                        onDelete = { vehicleRegistrationUri = null }
                    )

                    // Cargo Insurance
                    DocumentUploadCard(
                        title = "Cargo Insurance",
                        subtitle = "Proof of active policy",
                        icon = Icons.Outlined.LocalShipping,
                        iconBackgroundColor = Color(0xFFFAF5FF),
                        iconBackgroundColorDark = Color(0xFF3B1E5E),
                        iconTint = PurpleAccent,
                        iconTintDark = PurpleAccentDark,
                        isUploaded = cargoInsuranceUri != null,
                        onUpload = { cargoInsurancePicker.launch("image/*") },
                        onDelete = { cargoInsuranceUri = null }
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationProgressBar(currentStep: Int, totalSteps: Int, progress: Float) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step $currentStep of $totalSteps",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE5E7EB))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Primary)
            )
        }
    }
}

@Composable
fun StatusBanner() {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDark else AmberLight,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) BorderDark else AmberBorder
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Warning Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) Color(0xFF78350F).copy(alpha = 0.3f)
                        else Color(0xFFFEF3C7)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Warning",
                    tint = if (isDarkTheme) AmberIconDark else AmberIcon,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Status: Action Required",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Please complete the required fields and upload documents below.",
                    fontSize = 14.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun VerificationTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: ImageVector? = null
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF334155)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF334155) else Color(0xFFD1D5DB)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
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
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DocumentUploadCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconBackgroundColorDark: Color,
    iconTint: Color,
    iconTintDark: Color = iconTint,
    isUploaded: Boolean,
    hasPreview: Boolean = false,
    previewUrl: String = "",
    onUpload: () -> Unit,
    onDelete: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isUploaded) onUpload() },
        shape = RoundedCornerShape(16.dp),
        color = if (isUploaded) {
            if (isDarkTheme) Primary.copy(alpha = 0.1f) else Primary.copy(alpha = 0.05f)
        } else {
            if (isDarkTheme) SurfaceDark else Color.White
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUploaded) Primary.copy(alpha = 0.3f)
            else if (isDarkTheme) BorderDark else Color(0xFFE5E7EB)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon or Preview
            if (hasPreview && isUploaded) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(previewUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                    )
                    // Edit overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkTheme) iconBackgroundColorDark else iconBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isDarkTheme) iconTintDark else iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isUploaded) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Uploaded",
                            tint = if (isDarkTheme) GreenSuccessDark else GreenSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Uploaded",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) GreenSuccessDark else GreenSuccess
                        )
                    }
                } else {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Action Icon
            if (isUploaded) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = "Upload",
                    tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer(alpha = alpha)
)

@Preview(showBackground = true)
@Composable
fun VerificationScreenPreview() {
    MoveMateTheme {
        VerificationScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VerificationScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        VerificationScreen()
    }
}

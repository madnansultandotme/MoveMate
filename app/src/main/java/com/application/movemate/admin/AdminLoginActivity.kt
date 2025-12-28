package com.application.movemate.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.R
import com.application.movemate.ui.theme.*

// Custom colors for Admin Login
private val DarkInputBackground = Color(0xFF192433)
private val DarkInputBorder = Color(0xFF324867)
private val InputHintColor = Color(0xFF92A9C9)
private val GreenOnline = Color(0xFF22C55E)

class AdminLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                AdminLoginScreen()
            }
        }
    }
}

@Composable
fun AdminLoginScreen(viewModel: AdminAuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // Pulse animation for online indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(user) {
        if (user != null) {
            context.startActivity(Intent(context, AdminDashboardActivity::class.java))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background Decoration - Gradient from primary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Image Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    // Background Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuB8xr_s7BHqf28POeeeC29YgnLfSQ6gWNBKPHxBtkWw3zF4TOA6C85uscUbrCD8Vpvk9q5tnpx5YZ2Tw4GwYSLUfjQEeRdZfEWtqWHJnTnZWdpoaWNzyXoyp6vNDds5yISUCMvI5Vvq-SA750Tq93XsKfUfOqqAzNwWyNdCJ15qvCPuHvUUMgGtIWWcLsIAD0_YgVpvecUOrFMWDkfnQIoTFVX-HXnARXxw9c8-gPQLuzzEtGTs-XfZHZQ5cAGukvLL2pPznto1qo4")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Admin Login Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        BackgroundDark.copy(alpha = 0.6f),
                                        BackgroundDark
                                    )
                                )
                            )
                    )

                    // Logo & Brand Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Logo Container
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Primary.copy(alpha = 0.2f))
                                    .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_local_shipping),
                                    contentDescription = "MoveMate",
                                    tint = Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "MoveMate",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // System Online Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        GreenOnline.copy(alpha = pulseAlpha),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = "SYSTEM ONLINE",
                                color = Color(0xFFD1D5DB),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Headline Text
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Admin Access",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sign in to manage logistics operations and monitor fleet status.",
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Form
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Email Field
                    Column {
                        Text(
                            text = "Email Address",
                            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        LoginTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "admin@movemate.com",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    tint = InputHintColor
                                )
                            }
                        )
                    }

                    // Password Field
                    Column {
                        Text(
                            text = "Password",
                            color = if (isDarkTheme) Color(0xFFD1D5DB) else Color(0xFF374151),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        LoginTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "••••••••",
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = InputHintColor
                                    )
                                }
                            }
                        )
                    }

                    // Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { /* Handle forgot password */ }
                        )
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

                    // Login Button
                    Button(
                        onClick = { viewModel.login(email, password) },
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
                            text = "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.Login,
                            contentDescription = "Login",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Biometric Alternative
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB)
                            )
                            Text(
                                text = "OR LOGIN WITH",
                                color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = if (isDarkTheme) Color(0xFF374151) else Color(0xFFD1D5DB)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fingerprint Button
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { /* Handle biometric */ },
                            shape = CircleShape,
                            color = if (isDarkTheme) DarkInputBackground else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkTheme) DarkInputBorder else Color(0xFFE5E7EB)
                            ),
                            shadowElevation = 2.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Fingerprint,
                                    contentDescription = "Fingerprint Login",
                                    tint = if (isDarkTheme) Color.White else Color(0xFF374151),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Restricted access. Authorized personnel only.",
                    color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Row {
                    Text(
                        text = "Need help? ",
                        color = if (isDarkTheme) Color(0xFF6B7280) else Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Contact Support",
                        color = Primary,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { /* Handle support */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom indicator bar
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .background(
                            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFD1D5DB),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) DarkInputBackground else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) DarkInputBorder else Color(0xFFD1D5DB)
        ),
        shadowElevation = 2.dp
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
                        color = InputHintColor,
                        fontSize = 16.sp
                    )
                },
                visualTransformation = visualTransformation,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = Primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF111827),
                    unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF111827)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminLoginScreenPreview() {
    MoveMateTheme {
        AdminLoginScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AdminLoginScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        AdminLoginScreen()
    }
}

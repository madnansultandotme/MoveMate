package com.application.movemate.carrier

import android.content.Intent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.R
import com.application.movemate.auth.CarrierAuthViewModel
import com.application.movemate.loader.LoaderLoginActivity
import com.application.movemate.ui.theme.*

// Custom colors for Carrier Login
private val DarkInputBackground = Color(0xFF1E293B)
private val DarkInputBorder = Color(0xFF334155)
private val LightInputBorder = Color(0xFFE2E8F0)
private val InputHintColor = Color(0xFF94A3B8)

class CarrierLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                CarrierLoginScreen()
            }
        }
    }
}

@Composable
fun CarrierLoginScreen(viewModel: CarrierAuthViewModel = viewModel()) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(user) {
        if (user != null) {
            context.startActivity(Intent(context, CarrierDashboardActivity::class.java))
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Header Section
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_local_shipping),
                        contentDescription = "MoveMate",
                        tint = Primary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Carrier Portal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Log in to find your next load.",
                    fontSize = 16.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email/Phone Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Email or Phone Number",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        CarrierLoginTextField(
                            value = emailOrPhone,
                            onValueChange = { emailOrPhone = it },
                            placeholder = "example@carrier.com",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = "Email",
                                    tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }

                    // Password Input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Password",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF334155),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        CarrierLoginTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "••••••••",
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = "Password",
                                    tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary,
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login Button
                    Button(
                        onClick = { viewModel.login(emailOrPhone, password) },
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
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Divider with "Or continue with"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                    )
                    Text(
                        text = "Or continue with",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 12.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Apple Button
                    SocialLoginButton(
                        onClick = { /* Handle Apple login */ },
                        icon = Icons.Filled.Apple,
                        text = "Apple",
                        modifier = Modifier.weight(1f),
                        iconTint = MaterialTheme.colorScheme.onBackground
                    )

                    // Face ID Button
                    SocialLoginButton(
                        onClick = { /* Handle Face ID */ },
                        icon = Icons.Filled.Face,
                        text = "Face ID",
                        modifier = Modifier.weight(1f),
                        iconTint = Primary
                    )
                }
            }

            // Footer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Register link
                Row {
                    Text(
                        text = "New to MoveMate?",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Register as a Carrier",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, CarrierRegistrationActivity::class.java))
                        }
                    )
                }

                // Switch to Shipper
                Row(
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, LoaderLoginActivity::class.java))
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warehouse,
                        contentDescription = "Shipper",
                        tint = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Log in as a Shipper instead",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun CarrierLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
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
            if (isDarkTheme) DarkInputBorder else LightInputBorder
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier.padding(start = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingIcon()
                }
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
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
                    focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            if (trailingIcon != null) {
                Box(
                    modifier = Modifier.padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
fun SocialLoginButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Unspecified
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDarkTheme) DarkInputBackground else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White else Color(0xFF334155)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarrierLoginScreenPreview() {
    MoveMateTheme {
        CarrierLoginScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CarrierLoginScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        CarrierLoginScreen()
    }
}

package com.application.movemate.loader

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
import com.application.movemate.auth.LoaderAuthViewModel
import com.application.movemate.carrier.CarrierLoginActivity
import com.application.movemate.ui.theme.*

// Custom colors for Loader Login
private val DarkInputBackground = Color(0xFF192433)
private val DarkInputBorder = Color(0xFF324867)
private val LightInputBorder = Color(0xFFDCE0E5)
private val TextSecondaryLight = Color(0xFF637588)
private val TextSecondaryDarkCustom = Color(0xFF93AABF)
private val InputHintDark = Color(0xFF92A9C9)

class LoaderLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderLoginScreen()
            }
        }
    }
}

@Composable
fun LoaderLoginScreen(viewModel: LoaderAuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(user) {
        if (user != null) {
            context.startActivity(Intent(context, LoaderDashboardActivity::class.java))
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
        ) {
            // Header / Navbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                IconButton(
                    onClick = { (context as? ComponentActivity)?.finish() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Logo and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = "MoveMate",
                        tint = Primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "MoveMate",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Spacer for optical centering
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp)
            ) {
                // Welcome Section
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Welcome Back, Loader",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Log in to manage your shipments, track deliveries, and handle bids.",
                        fontSize = 16.sp,
                        color = if (isDarkTheme) TextSecondaryDarkCustom else TextSecondaryLight,
                        lineHeight = 24.sp
                    )
                }

                // Login Form
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Email Field
                    LoaderLoginTextField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "john@company.com",
                        leadingIcon = Icons.Outlined.Email
                    )

                    // Password Field
                    LoaderLoginTextField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.Outlined.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = if (isDarkTheme) InputHintDark else TextSecondaryLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    // Forgot Password Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-8).dp),
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
                            fontSize = 14.sp
                        )
                    }

                    // Login Button
                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Social Login Divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = if (isDarkTheme) DarkInputBorder else LightInputBorder
                    )
                    Text(
                        text = "Or continue with",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) TextSecondaryDarkCustom else TextSecondaryLight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = if (isDarkTheme) DarkInputBorder else LightInputBorder
                    )
                }

                // Social Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Google Button
                    SocialLoginButtonLoader(
                        text = "Google",
                        onClick = { /* Handle Google login */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        // Google icon placeholder - using a generic icon
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Google",
                            tint = Color(0xFFDB4437),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Apple Button
                    SocialLoginButtonLoader(
                        text = "Apple",
                        onClick = { /* Handle Apple login */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhoneIphone,
                            contentDescription = "Apple",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Footer Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Sign up link
                Row {
                    Text(
                        text = "Don't have an account?",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryDarkCustom else TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, LoaderRegistrationActivity::class.java))
                        }
                    )
                }

                // Role Switcher Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            context.startActivity(Intent(context, CarrierLoginActivity::class.java))
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isDarkTheme) DarkInputBackground else Color(0xFFF3F4F6),
                    border = if (isDarkTheme) {
                        androidx.compose.foundation.BorderStroke(1.dp, DarkInputBorder)
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF111822) else Color.White)
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_local_shipping),
                                contentDescription = "Carrier",
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Text content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Are you a Carrier?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Switch to carrier login to find loads",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) TextSecondaryDarkCustom else TextSecondaryLight
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Switch",
                            tint = if (isDarkTheme) TextSecondaryDarkCustom else TextSecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoaderLoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                // Leading icon
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    tint = if (isDarkTheme) InputHintDark else TextSecondaryLight,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(20.dp)
                )

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = if (isDarkTheme) InputHintDark else TextSecondaryLight
                        )
                    },
                    visualTransformation = visualTransformation,
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
                        .weight(1f)
                        .padding(start = 8.dp),
                    singleLine = true
                )

                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
fun SocialLoginButtonLoader(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
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
            if (isDarkTheme) DarkInputBorder else LightInputBorder
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoaderLoginScreenPreview() {
    MoveMateTheme {
        LoaderLoginScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoaderLoginScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderLoginScreen()
    }
}

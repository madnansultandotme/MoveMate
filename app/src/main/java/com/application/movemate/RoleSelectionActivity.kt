package com.application.movemate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.application.movemate.admin.AdminLoginActivity
import com.application.movemate.carrier.CarrierLoginActivity
import com.application.movemate.loader.LoaderLoginActivity
import com.application.movemate.ui.theme.*

class RoleSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header with Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Logo Icon Container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_local_shipping),
                        contentDescription = "MoveMate Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "MoveMate",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hero Text
            Text(
                text = "Logistics Simplified",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "How will you use MoveMate today?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Role Selection Cards
            // Carrier Card
            RoleCard(
                categoryIcon = R.drawable.ic_directions_car,
                categoryLabel = "Transport",
                title = "I am a Carrier",
                subtitle = "Find loads & drive",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCCnj7LYVWwMJQkubjTmaGllQqH0QYeJngc9-Znv7Is3sDqjokwBVhfupgSWC9UltXgiYiN4dcxikRavXrBKb6yydcfJYHUrpibmkfQyVRXf5VAOpRhzVLOzs7kdBox93ic4SQ1S3fYzzC3le-V1VS3hM1FySyRdxgxc4CuBVnFxePfWwDnotBOYfPpYvSfNQ8fDj9HutsSkfou95jXWy96iZwLjlxfJcBmLicaBKNTpael9IqooXY5vn6Iqq1J-79rrba5wFhBBQw",
                onClick = { context.startActivity(Intent(context, CarrierLoginActivity::class.java)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loader Card
            RoleCard(
                categoryIcon = R.drawable.ic_inventory,
                categoryLabel = "Shipping",
                title = "I am a Loader",
                subtitle = "Post loads & ship",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB8CzNQCsTOic5kLxQ-FAbo392PHjr05MQ0n3ufycOZeyTiiWo5gu_-nW4Ck4M_d8tmNdwaOSLLUIDdt9Tyd1iXMJTMdTLlDlCXoIhRjtClCeEfdt_6eobYnM7sSSet9fROo-BwM5N9cABxA1-kOJ7PVNpkZlKstEpYH64a5plx7rGla4McIE27ZdHOMVA3U-VcaHDj2BoCZ4ORRQGlvsC0VEACCEF-wZkJG-uGmSaahEezyqxdB-_GbE2wzlWi4OhQmG05A_mWmf4",
                onClick = { context.startActivity(Intent(context, LoaderLoginActivity::class.java)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Card
            RoleCard(
                categoryIcon = R.drawable.ic_admin_panel,
                categoryLabel = "Internal",
                title = "Admin Access",
                subtitle = "Manage platform",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDmqrJd-PW9CMTU6eHg_j7jy-Idz0kBjc9jY0YF-fX02FVkyU1zfPNVYMV3RE6vNqlRncXKmONKVeGpamEtEXPu52xpuPLLUplORHeQsOu6dt3pdXoSFqBYG3h3-Q-8zXYsrmO0Gv6tCr5Gh1L_Sak9Xh7iGxRzdD0Z20C29YBHoun-cICMqqkZL5DgTbK57c-ApXIXiNPPNfp07iv-AIxT8CxdwPJztXXWAl-tnlGimzkXpuWmkxRvUj-3MDGyhtl-BSiumBzhgeM",
                onClick = { context.startActivity(Intent(context, AdminLoginActivity::class.java)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                    )
                    Text(
                        text = "Sign up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            // Navigate to sign up - can be updated later
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Version 1.0.4",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) TextTertiaryDark else TextTertiaryLight
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RoleCard(
    categoryIcon: Int,
    categoryLabel: String,
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.98f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isDarkTheme) BorderDark else BorderLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Category Row
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = categoryIcon),
                        contentDescription = categoryLabel,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryLabel.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
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

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RoleSelectionScreenDarkPreview() {
    MoveMateTheme(darkTheme = true) {
        RoleSelectionScreen()
    }
}

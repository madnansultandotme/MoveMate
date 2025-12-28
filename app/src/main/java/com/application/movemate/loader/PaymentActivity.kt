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
import androidx.compose.ui.graphics.Color
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
import com.application.movemate.ui.theme.*
import com.application.movemate.viewmodels.PaymentViewModel

// Custom colors for Payment screen
private val SurfaceDarkPayment = Color(0xFF192433)
private val TextSecondaryPayment = Color(0xFF92A9C9)
private val BorderDarkPayment = Color(0xFF374151)

class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoveMateTheme {
                LoaderPaymentScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderPaymentScreen(viewModel: PaymentViewModel = viewModel()) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    var selectedTipPercent by remember { mutableStateOf(10) }
    val baseAmount = 450.0
    val serviceFee = 25.0
    val tipAmount = baseAmount * selectedTipPercent / 100
    val totalAmount = baseAmount + serviceFee + tipAmount

    val tipOptions = listOf(
        TipOption(5, baseAmount * 0.05),
        TipOption(10, baseAmount * 0.10),
        TipOption(15, baseAmount * 0.15),
        TipOption(-1, 0.0) // Other
    )

    Scaffold(
        topBar = {
            // TopAppBar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8))
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
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                        )
                    }

                    // Title
                    Text(
                        text = "Complete Payment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 48.dp),
                        textAlign = TextAlign.Center
                    )
                }

                HorizontalDivider(
                    color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                    thickness = 1.dp
                )
            }
        },
        bottomBar = {
            // Footer Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFE5E7EB),
                        thickness = 1.dp,
                        modifier = Modifier.offset(y = (-16).dp)
                    )

                    Button(
                        onClick = { /* Process payment */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Pay $${String.format("%.2f", totalAmount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = TextSecondaryPayment,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Payments are secure and encrypted",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondaryPayment.copy(alpha = 0.6f)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Total Amount Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL AMOUNT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = TextSecondaryPayment
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", baseAmount + serviceFee)}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                    letterSpacing = (-1).sp
                )
            }

            // Shipment Summary Card
            PaymentSection(title = "Shipment Summary") {
                ShipmentSummaryPaymentCard(
                    status = "Delivered",
                    orderId = "#8832",
                    title = "Furniture Delivery",
                    carrierName = "John Doe Trucking",
                    carrierImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuAYKecEvAdI2CMTqIyt8GIHSSoZkfEtsY1RyiSrE1T1CRjBXi_zbPXxZVzJKQ4vrX5lrUppiEhD60EgAVlz8tfFiqGyl2GErvvdLXLVdJ_pxPe8W47m3Ov0lXwmm5oEpWMh6xqEpeHz6NtVSLCoB3kN1snrT3wPd8Fd0AzPeLDjAfU_fCvTqxmPyQcX4ztM41kLHwKJLeIXFXr2wvriTvBAvjmV4qf3Dl7EcLOsSnGG3JACeBs1DsSYI_dVQSxxBj0halP5-bq6nIw",
                    mapImage = "https://lh3.googleusercontent.com/aida-public/AB6AXuDu9pxyE20i7U7kFS9qKM3dYcVINOwxiSWmdXbyeL7svDr8UVh59X92dLzXIKTL7gWbl_synKxZ31cAuCSW-N9X6BaKtAXCVW3DT2EWQ6gbyNrWgoVMFZhv0NE4pmXf6-NyUWppDiYXT7gp9TzZz3RgDZ4IBybb-NfNuR-z09oCPH9_Dz-eTPUY5KbPEpipza600_-PDD4KyNtSIbC5Rcsgc3ymVaU12R4z2m-l-0Q8ROgPughD6hUxELxV3v_-TgBaUiESWJEwGZE"
                )
            }

            // Payment Method
            PaymentSection(title = "Payment Method") {
                PaymentMethodCard(
                    cardType = "Visa",
                    lastFour = "4242",
                    expiry = "12/25",
                    onChangeClick = { /* Change payment method */ }
                )
            }

            // Add a Tip
            PaymentSection(
                title = "Add a Tip",
                subtitle = "100% goes to the driver"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tipOptions.forEach { option ->
                        TipButton(
                            percent = option.percent,
                            amount = option.amount,
                            isSelected = selectedTipPercent == option.percent,
                            onClick = { selectedTipPercent = option.percent },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Invoice Details
            PaymentSection(title = "Invoice Details") {
                InvoiceDetailsCard(
                    freightCost = baseAmount,
                    serviceFee = serviceFee,
                    tipPercent = selectedTipPercent,
                    tipAmount = tipAmount,
                    totalAmount = totalAmount
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

data class TipOption(val percent: Int, val amount: Double)

@Composable
private fun PaymentSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                modifier = Modifier.padding(start = 4.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondaryPayment
                )
            }
        }
        content()
    }
}

@Composable
private fun ShipmentSummaryPaymentCard(
    status: String,
    orderId: String,
    title: String,
    carrierName: String,
    carrierImage: String,
    mapImage: String
) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkPayment else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Delivered badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF22C55E).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = orderId,
                        fontSize = 12.sp,
                        color = TextSecondaryPayment
                    )
                }

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(carrierImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Carrier",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) BorderDarkPayment else Color(0xFFE5E7EB))
                    )
                    Text(
                        text = "Carrier: $carrierName",
                        fontSize = 14.sp,
                        color = TextSecondaryPayment
                    )
                }
            }

            // Map preview
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(mapImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(96.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (isDarkTheme) BorderDarkPayment.copy(alpha = 0.5f) else Color(0xFFF3F4F6),
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
private fun PaymentMethodCard(
    cardType: String,
    lastFour: String,
    expiry: String,
    onChangeClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkPayment else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
        ),
        shadowElevation = 2.dp
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card icon placeholder
                Surface(
                    modifier = Modifier.size(width = 56.dp, height = 40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDarkTheme) BorderDarkPayment else Color(0xFFE5E7EB)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = "Card",
                            tint = if (isDarkTheme) Color.White else Color(0xFF475569),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "$cardType ending in $lastFour",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Expires $expiry",
                        fontSize = 12.sp,
                        color = TextSecondaryPayment
                    )
                }
            }

            TextButton(onClick = onChangeClick) {
                Text(
                    text = "Change",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
            }
        }
    }
}

@Composable
private fun TipButton(
    percent: Int,
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isOther = percent == -1

    Surface(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Primary.copy(alpha = 0.1f)
                else if (isDarkTheme) SurfaceDarkPayment else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) Primary
            else if (isDarkTheme) BorderDarkPayment else Color(0xFFE5E7EB)
        ),
        shadowElevation = if (isSelected) 0.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (isOther) "Other" else "$percent%",
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) Primary
                            else if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = if (isOther) "--" else "$${String.format("%.1f", amount)}",
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Primary else TextSecondaryPayment
                )
            }

            // Checkmark for selected
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = Primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-20).dp)
                        .size(18.dp)
                        .background(
                            if (isDarkTheme) BackgroundDark else Color(0xFFF6F7F8),
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun InvoiceDetailsCard(
    freightCost: Double,
    serviceFee: Double,
    tipPercent: Int,
    tipAmount: Double,
    totalAmount: Double
) {
    val isDarkTheme = isSystemInDarkTheme()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) SurfaceDarkPayment else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF3F4F6)
        ),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InvoiceRow(
                label = "Freight Cost",
                value = "$${String.format("%.2f", freightCost)}",
                isTotal = false
            )
            InvoiceRow(
                label = "Service Fee",
                value = "$${String.format("%.2f", serviceFee)}",
                isTotal = false
            )
            InvoiceRow(
                label = if (tipPercent > 0) "Tip ($tipPercent%)" else "Tip",
                value = "$${String.format("%.2f", tipAmount)}",
                isTotal = false,
                showDivider = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Pay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = "$${String.format("%.2f", totalAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}

@Composable
private fun InvoiceRow(
    label: String,
    value: String,
    isTotal: Boolean,
    showDivider: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
                color = if (isTotal) {
                    if (isDarkTheme) Color.White else Color(0xFF0F172A)
                } else TextSecondaryPayment
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isTotal) Primary
                        else if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color = if (isDarkTheme) BorderDarkPayment else Color(0xFFF3F4F6),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Keep old composables for compatibility
data class Invoice(
    val id: String,
    val shipmentId: String,
    val amount: Double,
    val status: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsAndInvoicesScreen(viewModel: PaymentViewModel = viewModel()) {
    LoaderPaymentScreen(viewModel)
}

@Composable
fun InvoiceListItem(invoice: Invoice, onPayNow: (Invoice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Invoice #${invoice.id}", style = MaterialTheme.typography.titleMedium)
            Text("Shipment: ${invoice.shipmentId}")
            Text("Amount: $${invoice.amount}")
        }
        if (invoice.status == "Unpaid") {
            Button(onClick = { onPayNow(invoice) }) {
                Text("Pay Now")
            }
        } else {
            Text("Status: ${invoice.status}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PayNowScreen(invoice: Invoice, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pay Invoice #${invoice.id}") },
        text = { Text("Amount: $${invoice.amount}") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Pay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoaderPaymentScreenPreview() {
    MoveMateTheme(darkTheme = true) {
        LoaderPaymentScreen()
    }
}

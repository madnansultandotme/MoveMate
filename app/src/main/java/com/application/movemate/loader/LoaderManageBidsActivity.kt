package com.application.movemate.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.Shipment
import com.application.movemate.ui.theme.MoveMateTheme
import com.application.movemate.viewmodels.LoaderManageBidsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class LoaderManageBidsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoveMateTheme {
                LoaderManageBidsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoaderManageBidsScreen(viewModel: LoaderManageBidsViewModel = viewModel()) {
    val shipments by viewModel.shipments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bidAccepted by viewModel.bidAccepted.collectAsState()
    var selectedShipment by remember { mutableStateOf<Shipment?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        currentUser?.let {
            viewModel.fetchShipmentsWithBids(it.uid)
        }
    }

    LaunchedEffect(bidAccepted) {
        if (bidAccepted) {
            selectedShipment = null
            viewModel.resetBidAccepted()
            currentUser?.let {
                viewModel.fetchShipmentsWithBids(it.uid)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Bids") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (shipments.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No shipments open for bids",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(shipments) { shipment ->
                        ShipmentWithBidsCard(
                            shipment = shipment,
                            onViewBids = {
                                selectedShipment = shipment
                                viewModel.fetchBidsForShipment(shipment.id)
                            }
                        )
                    }
                }
            }
        }
    }

    selectedShipment?.let { shipment ->
        BidsBottomSheet(
            shipment = shipment,
            viewModel = viewModel,
            onDismiss = { selectedShipment = null }
        )
    }
}

@Composable
fun ShipmentWithBidsCard(shipment: Shipment, onViewBids: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                shipment.goodsType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("${shipment.pickupAddress} → ${shipment.deliveryAddress}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Budget: Rs. ${shipment.estimatedPrice}", style = MaterialTheme.typography.bodyMedium)
            Text("Weight: ${shipment.weight} kg", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onViewBids, modifier = Modifier.fillMaxWidth()) {
                Text("View & Manage Bids")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidsBottomSheet(
    shipment: Shipment,
    viewModel: LoaderManageBidsViewModel,
    onDismiss: () -> Unit
) {
    val bids by viewModel.bids.collectAsState()
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                "Bids for ${shipment.goodsType}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your budget: Rs. ${shipment.estimatedPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (bids.isEmpty()) {
                Text(
                    "No bids yet. Carriers will submit bids soon.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(bids) { bid ->
                        BidItemCard(
                            bid = bid,
                            onAccept = {
                                viewModel.acceptBid(bid.id, shipment.id, bid.carrierId, bid.carrierName)
                                onDismiss()
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BidItemCard(bid: CarrierBid, onAccept: () -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val deliveryDays = ((bid.estimatedDeliveryTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(bid.carrierName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rating: ", style = MaterialTheme.typography.bodySmall)
                        Text("${bid.carrierRating} ⭐", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(
                    "Rs. ${bid.bidAmount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Delivery in: $deliveryDays days", style = MaterialTheme.typography.bodySmall)
            Text("Bid placed: ${dateFormat.format(Date(bid.createdAt))}", style = MaterialTheme.typography.bodySmall)

            if (bid.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Message: ${bid.message}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Accept This Bid")
            }
        }
    }
}


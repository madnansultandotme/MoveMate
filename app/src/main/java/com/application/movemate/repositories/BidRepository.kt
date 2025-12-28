package com.application.movemate.repositories

import com.application.movemate.models.CarrierBid
import com.application.movemate.models.BidStatus
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BidRepository {

    private val db = Firebase.firestore
    private val bidsCollection = db.collection("bids")

    // Submit a new bid
    suspend fun submitBid(bid: CarrierBid): Result<String> {
        return try {
            val documentReference = bidsCollection.add(bid).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get bid by ID
    suspend fun getBid(bidId: String): Result<CarrierBid?> {
        return try {
            val document = bidsCollection.document(bidId).get().await()
            val bid = document.toObject(CarrierBid::class.java)?.copy(id = document.id)
            Result.success(bid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update bid
    suspend fun updateBid(bidId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            bidsCollection.document(bidId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update bid status
    suspend fun updateBidStatus(bidId: String, status: BidStatus): Result<Unit> {
        return try {
            bidsCollection.document(bidId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all bids for a shipment
    fun getBidsForShipment(shipmentId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get bids for a shipment sorted by price (lowest first)
    fun getBidsForShipmentByPrice(shipmentId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .orderBy("bidAmount", Query.Direction.ASCENDING)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get bids by carrier
    fun getBidsByCarrier(carrierId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("carrierId", carrierId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get pending bids by carrier
    fun getPendingBidsByCarrier(carrierId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("carrierId", carrierId)
                .whereEqualTo("status", BidStatus.PENDING.name)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get accepted bids by carrier
    fun getAcceptedBidsByCarrier(carrierId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("carrierId", carrierId)
                .whereEqualTo("status", BidStatus.ACCEPTED.name)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get outbid bids by carrier
    fun getOutbidBidsByCarrier(carrierId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("carrierId", carrierId)
                .whereEqualTo("status", BidStatus.OUTBID.name)
                .get()
                .await()
            val bids = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            emit(bids)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Accept a bid (and reject all others for the same shipment)
    suspend fun acceptBid(bidId: String, shipmentId: String): Result<Unit> {
        return try {
            // Accept the selected bid
            bidsCollection.document(bidId)
                .update("status", BidStatus.ACCEPTED.name)
                .await()

            // Reject all other bids for this shipment
            val otherBids = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .whereEqualTo("status", BidStatus.PENDING.name)
                .get()
                .await()

            otherBids.documents.forEach { doc ->
                if (doc.id != bidId) {
                    bidsCollection.document(doc.id)
                        .update("status", BidStatus.REJECTED.name)
                        .await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete bid
    suspend fun deleteBid(bidId: String): Result<Unit> {
        return try {
            bidsCollection.document(bidId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get bid count for a shipment
    suspend fun getBidCountForShipment(shipmentId: String): Result<Int> {
        return try {
            val snapshot = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get lowest bid for a shipment
    suspend fun getLowestBidForShipment(shipmentId: String): Result<CarrierBid?> {
        return try {
            val snapshot = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .whereEqualTo("status", BidStatus.PENDING.name)
                .orderBy("bidAmount", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .await()
            val bid = snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(CarrierBid::class.java)?.copy(id = doc.id)
            }
            Result.success(bid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get carrier bid stats (for dashboard)
    suspend fun getCarrierBidStats(carrierId: String): Result<BidStats> {
        return try {
            val allBids = bidsCollection
                .whereEqualTo("carrierId", carrierId)
                .get()
                .await()

            var pending = 0
            var accepted = 0
            var outbid = 0
            var totalAmount = 0.0

            allBids.documents.forEach { doc ->
                val bid = doc.toObject(CarrierBid::class.java)
                bid?.let {
                    when (it.status) {
                        BidStatus.PENDING -> pending++
                        BidStatus.ACCEPTED -> {
                            accepted++
                            totalAmount += it.bidAmount
                        }
                        BidStatus.OUTBID -> outbid++
                        else -> {}
                    }
                }
            }

            Result.success(BidStats(
                totalBids = allBids.size(),
                pendingBids = pending,
                acceptedBids = accepted,
                outbidBids = outbid,
                totalWonAmount = totalAmount
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class BidStats(
    val totalBids: Int = 0,
    val pendingBids: Int = 0,
    val acceptedBids: Int = 0,
    val outbidBids: Int = 0,
    val totalWonAmount: Double = 0.0
)


package com.application.movemate.repositories

import com.application.movemate.models.Shipment
import com.application.movemate.models.ShipmentStatus
import com.application.movemate.models.CarrierBid
import com.application.movemate.models.BidStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShipmentRepository {

    private val db = Firebase.firestore
    private val shipmentsCollection = db.collection("shipments")
    private val bidsCollection = db.collection("bids")

    // Create shipment (by Carrier)
    suspend fun createShipment(shipment: Shipment): Result<String> {
        return try {
            val documentReference = shipmentsCollection.add(shipment).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update shipment
    suspend fun updateShipment(shipmentId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            shipmentsCollection.document(shipmentId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get shipment by ID
    suspend fun getShipment(shipmentId: String): Result<Shipment?> {
        return try {
            val document = shipmentsCollection.document(shipmentId).get().await()
            val shipment = document.toObject(Shipment::class.java)?.copy(id = document.id)
            Result.success(shipment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get available loads for Loaders (status = OPEN_FOR_BIDS)
    fun getAvailableLoads(): Flow<List<Shipment>> = flow {
        try {
            val snapshot = shipmentsCollection
                .whereEqualTo("status", ShipmentStatus.OPEN_FOR_BIDS.name)
                .get()
                .await()
            val shipments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Shipment::class.java)?.copy(id = doc.id)
            }
            emit(shipments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get shipments by Carrier ID
    fun getShipmentsByCarrierId(carrierId: String): Flow<List<Shipment>> = flow {
        try {
            val snapshot = shipmentsCollection
                .whereEqualTo("carrierId", carrierId)
                .get()
                .await()
            val shipments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Shipment::class.java)?.copy(id = doc.id)
            }
            emit(shipments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get shipments assigned to a Loader
    fun getShipmentsByLoaderId(loaderId: String): Flow<List<Shipment>> = flow {
        try {
            val snapshot = shipmentsCollection
                .whereEqualTo("loaderId", loaderId)
                .get()
                .await()
            val shipments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Shipment::class.java)?.copy(id = doc.id)
            }
            emit(shipments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get all shipments (for Admin)
    fun getAllShipments(): Flow<List<Shipment>> = flow {
        try {
            val snapshot = shipmentsCollection.get().await()
            val shipments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Shipment::class.java)?.copy(id = doc.id)
            }
            emit(shipments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Filter shipments by status
    fun getShipmentsByStatus(status: ShipmentStatus): Flow<List<Shipment>> = flow {
        try {
            val snapshot = shipmentsCollection
                .whereEqualTo("status", status.name)
                .get()
                .await()
            val shipments = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Shipment::class.java)?.copy(id = doc.id)
            }
            emit(shipments)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Submit a bid (by Carrier - the driver)
    suspend fun submitBid(bid: CarrierBid): Result<String> {
        return try {
            val documentReference = bidsCollection.add(bid).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get bids for a shipment
    fun getBidsForShipment(shipmentId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
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

    // Get bids by Carrier (driver)
    fun getBidsByCarrierId(carrierId: String): Flow<List<CarrierBid>> = flow {
        try {
            val snapshot = bidsCollection
                .whereEqualTo("carrierId", carrierId)
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

    // Accept a bid and assign carrier (by Loader - the shipper)
    suspend fun acceptBid(bidId: String, shipmentId: String, carrierId: String, carrierName: String): Result<Unit> {
        return try {
            // Update bid status to ACCEPTED
            bidsCollection.document(bidId).update("status", BidStatus.ACCEPTED.name).await()

            // Reject all other bids for this shipment
            val otherBids = bidsCollection
                .whereEqualTo("shipmentId", shipmentId)
                .get()
                .await()

            otherBids.documents.forEach { doc ->
                if (doc.id != bidId) {
                    doc.reference.update("status", BidStatus.REJECTED.name).await()
                }
            }

            // Update shipment with assigned carrier
            val updates = mapOf(
                "carrierId" to carrierId,
                "carrierName" to carrierName,
                "status" to ShipmentStatus.ASSIGNED.name,
                "updatedAt" to System.currentTimeMillis()
            )
            shipmentsCollection.document(shipmentId).update(updates).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update shipment status
    suspend fun updateShipmentStatus(shipmentId: String, status: ShipmentStatus): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            shipmentsCollection.document(shipmentId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Upload delivery proof
    suspend fun uploadDeliveryProof(shipmentId: String, proofUrl: String, signature: String?): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "deliveryProof" to proofUrl,
                "status" to ShipmentStatus.DELIVERED.name,
                "deliveryTime" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            signature?.let { updates["deliverySignature"] = it }

            shipmentsCollection.document(shipmentId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}




package com.application.movemate.repositories

import com.application.movemate.models.Carrier
import com.application.movemate.models.BusinessDocuments
import com.application.movemate.models.VehicleInfo
import com.application.movemate.models.BankInfo
import com.application.movemate.models.VerificationStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CarrierRepository {

    private val db = Firebase.firestore
    private val carriersCollection = db.collection("carriers")

    // Create carrier profile
    suspend fun createCarrier(carrier: Carrier): Result<String> {
        return try {
            carriersCollection.document(carrier.id).set(carrier).await()
            Result.success(carrier.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get carrier by ID
    suspend fun getCarrier(carrierId: String): Result<Carrier?> {
        return try {
            val document = carriersCollection.document(carrierId).get().await()
            val carrier = document.toObject(Carrier::class.java)?.copy(id = document.id)
            Result.success(carrier)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update carrier profile
    suspend fun updateCarrier(carrierId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            carriersCollection.document(carrierId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all carriers (for Admin)
    fun getAllCarriers(): Flow<List<Carrier>> = flow {
        try {
            val snapshot = carriersCollection.get().await()
            val carriers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Carrier::class.java)?.copy(id = doc.id)
            }
            emit(carriers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get pending verification carriers
    fun getPendingVerificationCarriers(): Flow<List<Carrier>> = flow {
        try {
            val snapshot = carriersCollection
                .whereEqualTo("verificationStatus", VerificationStatus.PENDING.name)
                .get()
                .await()
            val carriers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Carrier::class.java)?.copy(id = doc.id)
            }
            emit(carriers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get active carriers
    fun getActiveCarriers(): Flow<List<Carrier>> = flow {
        try {
            val snapshot = carriersCollection
                .whereEqualTo("isActive", true)
                .whereEqualTo("isVerified", true)
                .get()
                .await()
            val carriers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Carrier::class.java)?.copy(id = doc.id)
            }
            emit(carriers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Update carrier verification status
    suspend fun updateVerificationStatus(
        carrierId: String,
        status: VerificationStatus,
        reason: String? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "verificationStatus" to status.name
            )
            if (status == VerificationStatus.APPROVED) {
                updates["approvedAt"] = System.currentTimeMillis()
                updates["isVerified"] = true
            } else if (status == VerificationStatus.REJECTED && reason != null) {
                updates["rejectionReason"] = reason
            }
            carriersCollection.document(carrierId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update carrier documents
    suspend fun updateCarrierDocuments(carrierId: String, documents: BusinessDocuments): Result<Unit> {
        return try {
            carriersCollection.document(carrierId)
                .update("businessDocuments", documents)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Add vehicle to carrier fleet
    suspend fun addVehicle(carrierId: String, vehicle: VehicleInfo): Result<Unit> {
        return try {
            val carrierDoc = carriersCollection.document(carrierId).get().await()
            val carrier = carrierDoc.toObject(Carrier::class.java)
            carrier?.let {
                val updatedVehicles = it.vehicles + vehicle
                carriersCollection.document(carrierId).update(
                    mapOf(
                        "vehicles" to updatedVehicles,
                        "totalVehicles" to updatedVehicles.size
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update primary vehicle
    suspend fun updatePrimaryVehicle(carrierId: String, vehicle: VehicleInfo): Result<Unit> {
        return try {
            carriersCollection.document(carrierId)
                .update("primaryVehicle", vehicle)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update bank info
    suspend fun updateBankInfo(carrierId: String, bankInfo: BankInfo): Result<Unit> {
        return try {
            carriersCollection.document(carrierId)
                .update("bankInfo", bankInfo)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update carrier earnings
    suspend fun updateCarrierEarnings(
        carrierId: String,
        amount: Double,
        isPending: Boolean = false
    ): Result<Unit> {
        return try {
            val carrierDoc = carriersCollection.document(carrierId).get().await()
            val carrier = carrierDoc.toObject(Carrier::class.java)
            carrier?.let {
                val updates = if (isPending) {
                    mapOf(
                        "pendingPayments" to (it.pendingPayments + amount)
                    )
                } else {
                    mapOf(
                        "totalEarnings" to (it.totalEarnings + amount),
                        "availableBalance" to (it.availableBalance + amount),
                        "lifetimeEarnings" to (it.lifetimeEarnings + amount)
                    )
                }
                carriersCollection.document(carrierId).update(updates).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update carrier statistics
    suspend fun updateCarrierStats(
        carrierId: String,
        completedShipments: Int? = null,
        totalShipments: Int? = null,
        activeBids: Int? = null,
        rating: Double? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()
            completedShipments?.let { updates["completedShipments"] = it }
            totalShipments?.let { updates["totalShipments"] = it }
            activeBids?.let { updates["activeBids"] = it }
            rating?.let { updates["rating"] = it }

            if (updates.isNotEmpty()) {
                carriersCollection.document(carrierId).update(updates).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Suspend carrier
    suspend fun suspendCarrier(carrierId: String, reason: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to false,
                "suspendedAt" to System.currentTimeMillis(),
                "suspensionReason" to reason
            )
            carriersCollection.document(carrierId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reactivate carrier
    suspend fun reactivateCarrier(carrierId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to true,
                "suspendedAt" to null,
                "suspensionReason" to null
            )
            carriersCollection.document(carrierId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update notification settings
    suspend fun updateNotificationSettings(
        carrierId: String,
        pushEnabled: Boolean,
        emailEnabled: Boolean
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "pushNotificationsEnabled" to pushEnabled,
                "emailNotificationsEnabled" to emailEnabled
            )
            carriersCollection.document(carrierId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update last active timestamp
    suspend fun updateLastActive(carrierId: String): Result<Unit> {
        return try {
            carriersCollection.document(carrierId)
                .update("lastActiveAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


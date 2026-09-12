package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.FeeDoc
import com.example.mytuition.core.domain.repository.FeeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseFeeRepository(
    private val db: FirebaseFirestore = FirebaseConfig.db,
    private val auth: FirebaseAuth = FirebaseConfig.auth
) : FeeRepository {

    override suspend fun getFees(): Result<List<FeeDoc>> {
        return try {
            val uid = auth.currentUser?.uid ?: "stu_789"
            val snapshot = db.collection("fees")
                .whereEqualTo("studentUid", uid)
                .get().await()
            val list = snapshot.documents.mapNotNull { it.toObject(FeeDoc::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRazorpayOrder(feeId: String): Result<Map<String, Any>> {
        return try {
            val result = FirebaseConfig.functions.getHttpsCallable("createRazorpayOrder")
                .call(mapOf("feeId" to feeId))
                .await()
            @Suppress("UNCHECKED_CAST")
            Result.success(result.data as? Map<String, Any> ?: emptyMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyRazorpayPayment(
        feeId: String,
        paymentId: String,
        orderId: String,
        signature: String
    ): Result<Unit> {
        return try {
            FirebaseConfig.functions.getHttpsCallable("verifyRazorpayPayment")
                .call(mapOf(
                    "feeId" to feeId,
                    "razorpayPaymentId" to paymentId,
                    "razorpayOrderId" to orderId,
                    "razorpaySignature" to signature
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

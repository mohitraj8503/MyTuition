package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.data.model.FeeDoc

interface FeeRepository {
    suspend fun getFees(): Result<List<FeeDoc>>
    suspend fun createRazorpayOrder(feeId: String): Result<Map<String, Any>>
    suspend fun verifyRazorpayPayment(feeId: String, paymentId: String, orderId: String, signature: String): Result<Unit>
}

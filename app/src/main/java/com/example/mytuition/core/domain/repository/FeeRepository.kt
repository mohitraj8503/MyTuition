package com.example.mytuition.core.domain.repository

import com.example.mytuition.core.data.model.FeeDoc

interface FeeRepository {
    suspend fun getFees(): Result<List<FeeDoc>>
    suspend fun markFeePaid(feeId: String, paymentMethod: String = "CASH", note: String? = null): Result<Unit>
    suspend fun undoMarkPaid(feeId: String): Result<Unit>
}

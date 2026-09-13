package com.example.mytuition.core.data.repository

import com.example.mytuition.core.data.model.FeeDoc
import com.example.mytuition.core.data.network.PbMarkFeePaidRequest
import com.example.mytuition.core.data.network.PbMarkFeePendingRequest
import com.example.mytuition.core.data.network.PocketBaseApi
import com.example.mytuition.core.domain.repository.FeeRepository

class PocketBaseFeeRepository(
    private val api: PocketBaseApi
) : FeeRepository {

    override suspend fun getFees(): Result<List<FeeDoc>> {
        return try {
            val resp = api.getFees()
            if (resp.isSuccessful && resp.body() != null) {
                val feeDocs = resp.body()!!.items.map { rec ->
                    val amt = rec.amount ?: 0.0
                    FeeDoc(
                        feeId = rec.id,
                        studentUid = rec.student ?: "",
                        instituteId = rec.institute ?: "inst_456",
                        amount = amt,
                        amountDisplay = "₹${amt.toLong()}",
                        dueDate = rec.dueDate ?: "",
                        month = rec.month ?: "CURRENT",
                        academicYear = rec.academicYear ?: "2025-2026",
                        status = rec.status ?: "PENDING",
                        paidDate = rec.paidDate,
                        paymentMethod = rec.paymentMethod,
                        razorpayOrderId = rec.razorpayOrderId,
                        razorpayPaymentId = rec.razorpayPaymentId
                    )
                }
                Result.success(feeDocs)
            } else {
                Result.failure(Exception("Failed to fetch fees: ${resp.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markFeePaid(feeId: String, paymentMethod: String, note: String?): Result<Unit> {
        return try {
            val resp = api.markFeePaid(
                PbMarkFeePaidRequest(
                    feeId = feeId,
                    paymentMethod = paymentMethod,
                    paymentNote = note
                )
            )
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to mark fee paid: ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun undoMarkPaid(feeId: String): Result<Unit> {
        return try {
            val resp = api.markFeePending(
                PbMarkFeePendingRequest(feeId = feeId)
            )
            if (resp.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to undo fee status: ${resp.code()} ${resp.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

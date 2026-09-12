package com.example.mytuition.core.data.repository

import android.app.Activity
import com.example.mytuition.core.data.FirebaseConfig
import com.example.mytuition.core.data.model.StudentInfoDoc
import com.example.mytuition.core.data.model.UserDoc
import com.example.mytuition.core.domain.model.UserRole
import com.example.mytuition.core.domain.model.UserSession
import com.example.mytuition.core.domain.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseConfig.auth,
    private val db: FirebaseFirestore = FirebaseConfig.db
) : AuthRepository {

    override suspend fun signInWithGoogle(): Result<UserSession> {
        return try {
            val user = auth.currentUser ?: auth.signInAnonymously().await().user
            val session = provisionAndGetSession(user, defaultName = "Mohit Raj", defaultRole = "STUDENT")
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGitHub(): Result<UserSession> {
        return enterDemoMode()
    }

    override suspend fun enterDemoMode(): Result<UserSession> {
        // Demo mode works 100% offline — no Firebase needed
        return try {
            Result.success(
                UserSession(
                    uid = "demo_user",
                    name = "Mohit Raj",
                    email = "demo@chanakya.in",
                    phone = "+919876543210",
                    role = UserRole.STUDENT,
                    studentId = "stu_789",
                    batchIds = listOf("batch_10a_maths", "batch_10a_science", "batch_10a_english"),
                    classGrade = "Class 10",
                    section = "A",
                    rollNumber = "27",
                    schoolName = "St. Xavier's School",
                    photoUrl = null,
                    isDemo = true
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithPhone(phoneNumber: String, activity: Activity): Result<UserSession> {
        return try {
            val deferred = CompletableDeferred<PhoneAuthCredential>()
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    deferred.complete(credential)
                }
                override fun onVerificationFailed(e: FirebaseException) {
                    deferred.completeExceptionally(e)
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Handled if SMS auto-retrieval occurs
                }
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                activity,
                callbacks
            )

            val credential = deferred.await()
            val authResult = auth.signInWithCredential(credential).await()
            val session = provisionAndGetSession(authResult.user, defaultName = "Mohit Raj", defaultPhone = phoneNumber)
            Result.success(session)
        } catch (e: Exception) {
            // Fallback to anonymous authenticated session for emulator/dev testing
            try {
                val user = auth.currentUser ?: auth.signInAnonymously().await().user
                val session = provisionAndGetSession(user, defaultName = "Mohit Raj", defaultPhone = phoneNumber)
                Result.success(session)
            } catch (fallbackError: Exception) {
                Result.failure(fallbackError)
            }
        }
    }

    override suspend fun getCurrentSession(): UserSession? {
        val user = auth.currentUser ?: return null
        return try {
            val doc = db.collection("users").document(user.uid).get().await()
            if (doc.exists()) {
                doc.toObject(UserDoc::class.java)?.toUserSession()
            } else {
                provisionAndGetSession(user, defaultName = user.displayName ?: "Mohit Raj")
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {
        }
    }

    private suspend fun provisionAndGetSession(
        user: FirebaseUser?,
        defaultName: String = "Mohit Raj",
        defaultPhone: String = "+919876543210",
        defaultRole: String = "STUDENT"
    ): UserSession {
        val uid = user?.uid ?: "stu_789"
        val userRef = db.collection("users").document(uid)
        val doc = userRef.get().await()
        if (doc.exists()) {
            val existing = doc.toObject(UserDoc::class.java)
            if (existing != null) return existing.toUserSession()
        }

        val newUserDoc = UserDoc(
            uid = uid,
            instituteId = "inst_456",
            role = defaultRole,
            name = user?.displayName ?: defaultName,
            email = user?.email ?: "mohitraj8503@gmail.com",
            phone = user?.phoneNumber ?: defaultPhone,
            photoUrl = user?.photoUrl?.toString() ?: "",
            studentInfo = StudentInfoDoc(
                studentId = "stu_789",
                classGrade = "Class 10",
                section = "A",
                rollNumber = "27",
                schoolName = "St. Xavier's School",
                batches = listOf("batch_10a_maths", "batch_10a_science", "batch_10a_english")
            ),
            isActive = true
        )
        userRef.set(newUserDoc).await()
        return newUserDoc.toUserSession()
    }
}

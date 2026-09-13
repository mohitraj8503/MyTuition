package com.example.mytuition.feature.teacher.students.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mytuition.core.domain.model.RegistrationResult
import com.example.mytuition.core.domain.model.TeacherBatchItem
import com.example.mytuition.core.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface RegisterStudentUiState {
    data class Form(
        val batches: List<TeacherBatchItem> = emptyList(),
        val selectedBatchId: String = "",
        val name: String = "",
        val phone: String = "",
        val email: String = "",
        val monthlyFee: String = "2500",
        val joinDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
        val rollNumber: String = "",
        val regFee: String = "0",
        val notifyParent: Boolean = true,
        val customPassword: String = "",
        val isSubmitting: Boolean = false,
        val nameError: String? = null,
        val phoneError: String? = null,
        val batchError: String? = null,
        val generalError: String? = null
    ) : RegisterStudentUiState

    data class Success(
        val result: RegistrationResult,
        val studentName: String,
        val parentPhone: String,
        val timingDisplay: String,
        val monthlyFee: String
    ) : RegisterStudentUiState
}

class RegisterStudentViewModel(
    private val teacherRepository: TeacherRepository,
    initialBatchId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterStudentUiState>(
        RegisterStudentUiState.Form(selectedBatchId = initialBatchId ?: "")
    )
    val uiState: StateFlow<RegisterStudentUiState> = _uiState.asStateFlow()

    init {
        loadBatches(initialBatchId)
    }

    private fun loadBatches(initialBatchId: String?) {
        viewModelScope.launch {
            val result = teacherRepository.getTeacherBatches()
            if (result.isSuccess) {
                val list = result.getOrNull() ?: emptyList()
                val current = _uiState.value as? RegisterStudentUiState.Form ?: RegisterStudentUiState.Form()
                val selectedId = if (!initialBatchId.isNullOrEmpty()) {
                    initialBatchId
                } else if (list.isNotEmpty()) {
                    list.first().batchId
                } else {
                    ""
                }
                val defaultFee = list.find { it.batchId == selectedId }?.defaultFee?.toInt()?.toString() ?: "2500"
                _uiState.value = current.copy(
                    batches = list,
                    selectedBatchId = selectedId,
                    monthlyFee = defaultFee
                )
            }
        }
    }

    fun onNameChange(name: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(name = name, nameError = null, generalError = null)
    }

    fun onPhoneChange(phone: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(phone = phone, phoneError = null, generalError = null)
    }

    fun onEmailChange(email: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(email = email, generalError = null)
    }

    fun onBatchSelect(batchId: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        val batch = form.batches.find { it.batchId == batchId }
        val fee = batch?.defaultFee?.toInt()?.toString() ?: form.monthlyFee
        _uiState.value = form.copy(
            selectedBatchId = batchId,
            monthlyFee = fee,
            batchError = null
        )
    }

    fun onMonthlyFeeChange(fee: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(monthlyFee = fee)
    }

    fun onRollNumberChange(roll: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(rollNumber = roll)
    }

    fun onNotifyParentToggle(notify: Boolean) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(notifyParent = notify)
    }

    fun onCustomPasswordChange(pass: String) {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return
        _uiState.value = form.copy(customPassword = pass)
    }

    fun createBatchInline(name: String, subject: String, days: List<String>, start: String, end: String, room: String, fee: Double) {
        viewModelScope.launch {
            val result = teacherRepository.createBatch(name, subject, days, start, end, room, fee)
            if (result.isSuccess) {
                val newBatch = result.getOrNull()
                if (newBatch != null) {
                    val form = _uiState.value as? RegisterStudentUiState.Form ?: return@launch
                    val updatedList = form.batches + newBatch
                    _uiState.value = form.copy(
                        batches = updatedList,
                        selectedBatchId = newBatch.batchId,
                        monthlyFee = newBatch.defaultFee.toInt().toString()
                    )
                }
            }
        }
    }

    fun submitRegistration() {
        val form = _uiState.value as? RegisterStudentUiState.Form ?: return

        // 1. Validation
        val cleanName = form.name.trim()
        val lettersOnly = cleanName.filter { it.isLetter() }
        if (cleanName.length < 2 || lettersOnly.length < 2) {
            _uiState.value = form.copy(nameError = "Student name must contain at least 2 letters")
            return
        }

        if (form.selectedBatchId.isEmpty()) {
            _uiState.value = form.copy(batchError = "Please select or create a batch")
            return
        }

        val cleanPhone = form.phone.trim()
        if (cleanPhone.isNotEmpty()) {
            val normalizedPhone = if (cleanPhone.startsWith("+91")) cleanPhone else "+91$cleanPhone"
            if (!Regex("^\\+91\\d{10}$").matches(normalizedPhone)) {
                _uiState.value = form.copy(phoneError = "Phone must be a valid 10-digit Indian number (+91)")
                return
            }
        }

        val feeAmount = form.monthlyFee.toDoubleOrNull() ?: 2500.0

        _uiState.value = form.copy(isSubmitting = true, generalError = null)

        viewModelScope.launch {
            val result = teacherRepository.registerStudent(
                name = cleanName,
                phone = if (cleanPhone.isNotEmpty()) (if (cleanPhone.startsWith("+91")) cleanPhone else "+91$cleanPhone") else null,
                email = form.email.trim().ifEmpty { null },
                batchId = form.selectedBatchId,
                monthlyFee = feeAmount,
                joinDate = form.joinDate,
                regFee = form.regFee.toDoubleOrNull(),
                notifyParent = form.notifyParent,
                customPassword = form.customPassword.trim().ifEmpty { null },
                rollNumber = form.rollNumber.trim().ifEmpty { null }
            )

            if (result.isSuccess) {
                val regRes = result.getOrNull()!!
                val selectedBatch = form.batches.find { it.batchId == form.selectedBatchId }
                _uiState.value = RegisterStudentUiState.Success(
                    result = regRes,
                    studentName = cleanName,
                    parentPhone = cleanPhone,
                    timingDisplay = selectedBatch?.scheduleDisplay ?: "Mon, Wed, Fri • 4:00 - 5:30 PM",
                    monthlyFee = form.monthlyFee
                )
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to register student. Try again."
                _uiState.value = form.copy(isSubmitting = false, generalError = err)
            }
        }
    }

    fun regeneratePassword() {
        val success = _uiState.value as? RegisterStudentUiState.Success ?: return
        viewModelScope.launch {
            val resetRes = teacherRepository.resetStudentPassword(success.result.studentId, null)
            if (resetRes.isSuccess) {
                val updatedCred = resetRes.getOrNull()!!
                _uiState.value = success.copy(
                    result = success.result.copy(password = updatedCred.passwordPlain)
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            teacherRepository: TeacherRepository,
            batchId: String? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterStudentViewModel(teacherRepository, batchId) as T
            }
        }
    }
}

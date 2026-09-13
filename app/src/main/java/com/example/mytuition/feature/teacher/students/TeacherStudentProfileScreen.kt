package com.example.mytuition.feature.teacher.students

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.PillButton
import com.example.mytuition.core.designsystem.components.PillButtonVariant
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.StudentCredentials
import com.example.mytuition.core.domain.model.StudentRosterItem
import com.example.mytuition.core.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StudentProfileUiState {
    object Loading : StudentProfileUiState
    data class Success(
        val student: StudentRosterItem,
        val credentials: StudentCredentials
    ) : StudentProfileUiState
    data class Error(val message: String) : StudentProfileUiState
}

class TeacherStudentProfileViewModel(
    private val studentId: String,
    private val teacherRepository: TeacherRepository,
    private val feeRepository: com.example.mytuition.core.domain.repository.FeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentProfileUiState>(StudentProfileUiState.Loading)
    val uiState: StateFlow<StudentProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StudentProfileUiState.Loading
            val studentRes = teacherRepository.getStudentProfileForTeacher(studentId)
            val credRes = teacherRepository.getStudentCredentials(studentId)

            if (studentRes.isSuccess && credRes.isSuccess) {
                _uiState.value = StudentProfileUiState.Success(
                    student = studentRes.getOrNull()!!,
                    credentials = credRes.getOrNull()!!
                )
            } else {
                _uiState.value = StudentProfileUiState.Error("Failed to load student profile.")
            }
        }
    }

    fun resetPassword(newPassword: String?) {
        viewModelScope.launch {
            val result = teacherRepository.resetStudentPassword(studentId, newPassword)
            if (result.isSuccess) {
                val current = _uiState.value as? StudentProfileUiState.Success ?: return@launch
                _uiState.value = current.copy(credentials = result.getOrNull()!!)
            }
        }
    }

    fun markFeePaid(feeId: String, paymentMethod: String, paymentNote: String?) {
        viewModelScope.launch {
            val res = feeRepository.markFeePaid(feeId, paymentMethod, paymentNote)
            if (res.isSuccess) {
                val current = _uiState.value as? StudentProfileUiState.Success ?: return@launch
                _uiState.value = current.copy(
                    student = current.student.copy(feeStatus = "Paid")
                )
            }
        }
    }

    fun undoMarkPaid(feeId: String) {
        viewModelScope.launch {
            val res = feeRepository.undoMarkPaid(feeId)
            if (res.isSuccess) {
                val current = _uiState.value as? StudentProfileUiState.Success ?: return@launch
                _uiState.value = current.copy(
                    student = current.student.copy(feeStatus = "Due")
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            studentId: String,
            teacherRepository: TeacherRepository,
            feeRepository: com.example.mytuition.core.domain.repository.FeeRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TeacherStudentProfileViewModel(studentId, teacherRepository, feeRepository) as T
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentProfileScreen(
    studentId: String,
    onBackClick: () -> Unit,
    viewModel: TeacherStudentProfileViewModel = viewModel(
        factory = TeacherStudentProfileViewModel.provideFactory(studentId, AppContainer.teacherRepository, AppContainer.feeRepository)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val uiState = state) {
                StudentProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                    }
                }

                is StudentProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(uiState.message, style = MyTuitionTypography.BodyMedium, color = MyTuitionColors.StatusRed)
                    }
                }

                is StudentProfileUiState.Success -> {
                    val s = uiState.student
                    val cred = uiState.credentials

                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text("Student Profile", style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)) },
                                navigationIcon = {
                                    IconButton(onClick = onBackClick) {
                                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                            )
                        }
                    ) { padding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. Hero Card
                            item {
                                ClayCard(modifier = Modifier.fillMaxWidth(), cardColor = Color.White) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(MyTuitionColors.PrimaryPurpleLight)
                                                .border(2.dp, MyTuitionColors.PrimaryPurple, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val initials = s.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                            Text(initials, style = MyTuitionTypography.HeadlineSmall.copy(color = MyTuitionColors.PrimaryPurple, fontWeight = FontWeight.Bold))
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(s.name, style = MyTuitionTypography.TitleSmall.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("${s.classGrade}-${s.section} • Roll ${s.rollNumber}", style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary))

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // 3 Stats Pills (Attendance, Homework, Fees)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            ProfileStatPill("Attendance", "${s.attendancePercent}%", Color(0xFF34C759))
                                            ProfileStatPill("Pending HW", "${s.pendingHomeworkCount}", if (s.pendingHomeworkCount > 0) Color(0xFFFF9500) else Color(0xFF34C759))
                                            ProfileStatPill("Fee Status", s.feeStatus, if (s.feeStatus == "Paid") Color(0xFF34C759) else Color(0xFFFF3B30))
                                        }
                                    }
                                }
                            }

                            // 2. Fees Management Clay Card
                            item {
                                var showMethodSheet by remember { mutableStateOf(false) }
                                var selectedMethod by remember { mutableStateOf("CASH") }
                                var paymentNote by remember { mutableStateOf("") }

                                ClayCard(modifier = Modifier.fillMaxWidth(), cardColor = Color.White) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Tuition Fees",
                                                style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)
                                            )

                                            val isFeePaid = s.feeStatus.equals("Paid", ignoreCase = true)
                                            val badgeBg = if (isFeePaid) Color(0xFFE8F9EE) else if (s.feeStatus.equals("Overdue", ignoreCase = true)) Color(0xFFFFECEB) else Color(0xFFFFF6E6)
                                            val badgeColor = if (isFeePaid) Color(0xFF34C759) else if (s.feeStatus.equals("Overdue", ignoreCase = true)) Color(0xFFFF3B30) else Color(0xFFFF9500)

                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = badgeBg
                                            ) {
                                                Text(
                                                    text = s.feeStatus,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeColor,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                val feeMonthDisplay = if (s.latestFeeMonth.isNotBlank()) s.latestFeeMonth else "Current Month"
                                                Text(
                                                    text = feeMonthDisplay,
                                                    style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                                )
                                                if (s.latestFeeDueDate.isNotBlank()) {
                                                    Text(
                                                        text = "Due date: ${s.latestFeeDueDate}",
                                                        style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextSecondary)
                                                    )
                                                }
                                            }

                                            val amtDisplay = if (s.latestFeeAmount > 0) "₹${s.latestFeeAmount.toInt()}" else "₹2,500"
                                            Text(
                                                text = amtDisplay,
                                                style = MyTuitionTypography.HeadlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MyTuitionColors.PrimaryPurple
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        val isFeePaid = s.feeStatus.equals("Paid", ignoreCase = true)
                                        if (!isFeePaid) {
                                            PillButton(
                                                text = "Mark Paid ✓",
                                                onClick = { showMethodSheet = true },
                                                modifier = Modifier.fillMaxWidth(),
                                                variant = PillButtonVariant.Primary
                                            )
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(20.dp),
                                                    color = Color(0xFFE8F9EE),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF34C759), modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Received ✓", style = MyTuitionTypography.LabelMedium.copy(color = Color(0xFF1B8738), fontWeight = FontWeight.Bold))
                                                    }
                                                }

                                                PillButton(
                                                    text = "Undo",
                                                    onClick = {
                                                        s.latestFeeId?.let { fid -> viewModel.undoMarkPaid(fid) }
                                                    },
                                                    modifier = Modifier.width(100.dp),
                                                    variant = PillButtonVariant.Outline
                                                )
                                            }
                                        }
                                    }
                                }

                                if (showMethodSheet) {
                                    ModalBottomSheet(
                                        onDismissRequest = { showMethodSheet = false },
                                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                                        containerColor = Color.White
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                                .padding(bottom = 24.dp)
                                        ) {
                                            Text(
                                                text = "Record Fee Payment",
                                                style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Confirm offline payment received from ${s.name}",
                                                style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary)
                                            )

                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("Payment Method", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                            Spacer(modifier = Modifier.height(8.dp))

                                            val methods = listOf("CASH" to "Cash", "DIRECT_UPI" to "Direct UPI", "OTHER" to "Other")
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                methods.forEach { (code, label) ->
                                                    val isSelected = selectedMethod == code
                                                    Surface(
                                                        shape = RoundedCornerShape(16.dp),
                                                        color = if (isSelected) MyTuitionColors.PrimaryPurple else Color(0xFFF4F0FF),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable { selectedMethod = code }
                                                    ) {
                                                        Box(
                                                            modifier = Modifier.padding(vertical = 12.dp),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = label,
                                                                style = MyTuitionTypography.LabelMedium.copy(
                                                                    color = if (isSelected) Color.White else MyTuitionColors.PrimaryPurple,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))
                                            OutlinedTextField(
                                                value = paymentNote,
                                                onValueChange = { paymentNote = it },
                                                label = { Text("Note (optional, e.g. Paid in class)") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp),
                                                singleLine = true
                                            )

                                            Spacer(modifier = Modifier.height(20.dp))
                                            PillButton(
                                                text = "Confirm Payment Received",
                                                onClick = {
                                                    val fid = s.latestFeeId ?: "fee_demo"
                                                    viewModel.markFeePaid(fid, selectedMethod, paymentNote.ifBlank { null })
                                                    showMethodSheet = false
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                variant = PillButtonVariant.Primary
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Login Credentials Vault Card
                            item {
                                CredentialsCard(
                                    credentials = cred,
                                    studentName = s.name,
                                    batchName = "Class 10-A • Maths Batch",
                                    timingDisplay = "Mon, Wed, Fri • 4:00 - 5:30 PM",
                                    feeDisplay = "₹2,500/month",
                                    onResetPassword = { newPass -> viewModel.resetPassword(newPass) }
                                )
                            }

                            // 4. Parent Contact Card (Direct Call / WhatsApp Intents)
                            item {
                                ClayCard(modifier = Modifier.fillMaxWidth(), cardColor = Color.White) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Text("Parent Details", style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("${s.parentName} (${s.parentRelationship})", style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text(s.parentPhone, style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary))

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            PillButton(
                                                text = "Call Parent",
                                                onClick = {
                                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${s.parentPhone}"))
                                                    context.startActivity(dialIntent)
                                                },
                                                modifier = Modifier.weight(1f),
                                                variant = PillButtonVariant.Outline
                                            )
                                            PillButton(
                                                text = "WhatsApp",
                                                onClick = {
                                                    val cleanNum = s.parentPhone.replace("+", "").replace(" ", "")
                                                    val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum"))
                                                    context.startActivity(waIntent)
                                                },
                                                modifier = Modifier.weight(1f),
                                                variant = PillButtonVariant.Primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatPill(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.ExtraBold, color = valueColor, fontSize = 16.sp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextTertiary, fontSize = 11.sp))
    }
}

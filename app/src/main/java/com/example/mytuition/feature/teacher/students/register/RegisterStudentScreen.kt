package com.example.mytuition.feature.teacher.students.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.PillButton
import com.example.mytuition.core.designsystem.components.PillButtonVariant
import com.example.mytuition.core.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStudentScreen(
    batchId: String? = null,
    onBackClick: () -> Unit,
    onRegistrationCompleted: () -> Unit = onBackClick,
    viewModel: RegisterStudentViewModel = viewModel(
        factory = RegisterStudentViewModel.provideFactory(AppContainer.teacherRepository, batchId)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showNewBatchSheet by remember { mutableStateOf(false) }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val uiState = state) {
                is RegisterStudentUiState.Success -> {
                    CredentialsSuccessCard(
                        result = uiState.result,
                        studentName = uiState.studentName,
                        timingDisplay = uiState.timingDisplay,
                        monthlyFee = uiState.monthlyFee,
                        onRegeneratePassword = { viewModel.regeneratePassword() },
                        onDone = onRegistrationCompleted,
                        modifier = Modifier.statusBarsPadding()
                    )
                }

                is RegisterStudentUiState.Form -> {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        "Register New Student",
                                        style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = onBackClick) {
                                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                            )
                        },
                        bottomBar = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White,
                                shadowElevation = 12.dp
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                                    if (uiState.generalError != null) {
                                        Text(
                                            text = uiState.generalError,
                                            color = MyTuitionColors.StatusRed,
                                            style = MyTuitionTypography.LabelMedium,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                    PillButton(
                                        text = if (uiState.isSubmitting) "Generating Credentials..." else "Register Student →",
                                        onClick = { viewModel.submitRegistration() },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !uiState.isSubmitting,
                                        variant = PillButtonVariant.Primary
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Section: Student Info
                            item {
                                Text(
                                    "STUDENT DETAILS",
                                    style = MyTuitionTypography.LabelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MyTuitionColors.PrimaryPurple,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }

                            // 1. Student Name
                            item {
                                Column {
                                    Text("Student Full Name *", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ClayInputBox(
                                        value = uiState.name,
                                        onValueChange = { viewModel.onNameChange(it) },
                                        placeholder = "e.g. Ayush Singh",
                                        isError = uiState.nameError != null
                                    )
                                    if (uiState.nameError != null) {
                                        Text(uiState.nameError, color = MyTuitionColors.StatusRed, style = MyTuitionTypography.LabelSmall, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }

                            // 2. Batch Picker
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Batch *", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text(
                                            "+ New Batch",
                                            style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.PrimaryPurple, fontWeight = FontWeight.Bold),
                                            modifier = Modifier.clickable { showNewBatchSheet = true }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(uiState.batches, key = { it.batchId }) { batch ->
                                            val isSelected = batch.batchId == uiState.selectedBatchId
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(if (isSelected) MyTuitionColors.PrimaryPurple else Color.White)
                                                    .border(1.5.dp, if (isSelected) MyTuitionColors.PrimaryPurple else Color(0xFFE2DCF8), RoundedCornerShape(14.dp))
                                                    .clickable { viewModel.onBatchSelect(batch.batchId) }
                                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = batch.name,
                                                    style = MyTuitionTypography.LabelMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) Color.White else MyTuitionColors.TextPrimary
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Batch details preview card
                                    val selectedBatch = uiState.batches.find { it.batchId == uiState.selectedBatchId }
                                    if (selectedBatch != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        ClayCard(modifier = Modifier.fillMaxWidth(), cardColor = Color(0xFFF9F8FD)) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Text("Timing: ${selectedBatch.scheduleDisplay}", style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary))
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text("Location: ${selectedBatch.roomName}", style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary))
                                            }
                                        }
                                    }
                                }
                            }

                            // 3. Monthly Fee
                            item {
                                Column {
                                    Text("Monthly Tuition Fee (₹) *", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ClayInputBox(
                                        value = uiState.monthlyFee,
                                        onValueChange = { viewModel.onMonthlyFeeChange(it) },
                                        placeholder = "2500",
                                        keyboardType = KeyboardType.Number
                                    )
                                    Text(
                                        "A fee record will automatically be generated for next month",
                                        style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextTertiary, fontSize = 11.sp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            // 4. Optional Phone & Email
                            item {
                                Column {
                                    Text("Parent / Student Phone (Optional)", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ClayInputBox(
                                        value = uiState.phone,
                                        onValueChange = { viewModel.onPhoneChange(it) },
                                        placeholder = "+919876543210",
                                        keyboardType = KeyboardType.Phone,
                                        isError = uiState.phoneError != null
                                    )
                                    if (uiState.phoneError != null) {
                                        Text(uiState.phoneError, color = MyTuitionColors.StatusRed, style = MyTuitionTypography.LabelSmall, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }

                            item {
                                Column {
                                    Text("Roll Number (Optional)", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ClayInputBox(
                                        value = uiState.rollNumber,
                                        onValueChange = { viewModel.onRollNumberChange(it) },
                                        placeholder = "e.g. 28",
                                        keyboardType = KeyboardType.Number
                                    )
                                }
                            }

                            // 5. Custom password (Optional override)
                            item {
                                Column {
                                    Text("Custom Password (Optional)", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.SemiBold))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ClayInputBox(
                                        value = uiState.customPassword,
                                        onValueChange = { viewModel.onCustomPasswordChange(it) },
                                        placeholder = "Leave empty to auto-generate (e.g. ayush@123)"
                                    )
                                }
                            }

                            // 6. Notify Parent Toggle
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Notify Parent on Registration", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Prepares credentials to share via WhatsApp", style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextTertiary))
                                    }
                                    Switch(
                                        checked = uiState.notifyParent,
                                        onCheckedChange = { viewModel.onNotifyParentToggle(it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = MyTuitionColors.PrimaryPurple, checkedTrackColor = Color(0xFFDDD2F8))
                                    )
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }

            // Inline New Batch Creation Bottom Sheet
            if (showNewBatchSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showNewBatchSheet = false },
                    containerColor = Color.White
                ) {
                    var newBatchName by remember { mutableStateOf("") }
                    var newSubject by remember { mutableStateOf("Mathematics") }
                    var newRoom by remember { mutableStateOf("Room 4B") }
                    var newFee by remember { mutableStateOf("2500") }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Create New Batch", style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(16.dp))

                        ClayInputBox(value = newBatchName, onValueChange = { newBatchName = it }, placeholder = "Batch Name (e.g. Class 10-C Maths)")
                        Spacer(modifier = Modifier.height(10.dp))
                        ClayInputBox(value = newSubject, onValueChange = { newSubject = it }, placeholder = "Subject")
                        Spacer(modifier = Modifier.height(10.dp))
                        ClayInputBox(value = newRoom, onValueChange = { newRoom = it }, placeholder = "Room")
                        Spacer(modifier = Modifier.height(10.dp))
                        ClayInputBox(value = newFee, onValueChange = { newFee = it }, placeholder = "Default Fee (₹)", keyboardType = KeyboardType.Number)

                        Spacer(modifier = Modifier.height(20.dp))

                        PillButton(
                            text = "Save Batch ✓",
                            onClick = {
                                if (newBatchName.isNotBlank()) {
                                    viewModel.createBatchInline(
                                        name = newBatchName,
                                        subject = newSubject,
                                        days = listOf("Mon", "Wed", "Fri"),
                                        start = "4:00 PM",
                                        end = "5:30 PM",
                                        room = newRoom,
                                        fee = newFee.toDoubleOrNull() ?: 2500.0
                                    )
                                    showNewBatchSheet = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClayInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    val corner = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(corner)
            .background(Color(0xFFF7F6FA))
            .border(1.5.dp, if (isError) MyTuitionColors.StatusRed else Color(0xFFE5E2EC), corner)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MyTuitionTypography.BodyMedium.copy(color = MyTuitionColors.TextTertiary, fontSize = 14.sp)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF221A44)),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(MyTuitionColors.PrimaryPurple),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

package com.example.mytuition.feature.teacher.homework

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
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
import com.example.mytuition.core.designsystem.*
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.model.TeacherHomeworkSubmission
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeworkReviewScreen(
    homeworkId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repo = AppContainer.teacherRepository

    var submissions by remember { mutableStateOf<List<TeacherHomeworkSubmission>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSubmissionForGrading by remember { mutableStateOf<TeacherHomeworkSubmission?>(null) }

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            val res = repo.getHomeworkSubmissions(homeworkId)
            submissions = res.getOrDefault(emptyList())
            isLoading = false
        }
    }

    LaunchedEffect(homeworkId) {
        loadData()
    }

    val totalCount = submissions.size
    val completedCount = submissions.count { it.status == HomeworkStatus.COMPLETED }
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Homework Review",
                            style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MyTuitionColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = MyTuitionColors.TextPrimary
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    repo.remindPendingHomework(homeworkId)
                                    Toast.makeText(context, "Reminders sent to pending students!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MyTuitionColors.PrimaryPurple)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remind All", color = MyTuitionColors.PrimaryPurple, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Summary Card with Progress Ring
                    item {
                        ClayCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Submission Progress",
                                        style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MyTuitionColors.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "$completedCount of $totalCount completed",
                                        style = MyTuitionTypography.BodyMedium,
                                        color = MyTuitionColors.TextSecondary
                                    )
                                }

                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier.size(54.dp),
                                        color = MyTuitionColors.SuccessGreen,
                                        trackColor = Color(0xFFE2E8F0),
                                        strokeWidth = 6.dp
                                    )
                                    Text(
                                        "${(progressFraction * 100).toInt()}%",
                                        style = MyTuitionTypography.LabelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Student Submissions (${submissions.size})",
                            style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MyTuitionColors.TextPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (submissions.isEmpty()) {
                        item {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Rounded.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No submissions yet", color = MyTuitionColors.TextSecondary)
                                }
                            }
                        }
                    } else {
                        items(submissions) { sub ->
                            SubmissionRowItem(
                                submission = sub,
                                onGradeClick = { selectedSubmissionForGrading = sub }
                            )
                        }
                    }
                }
            }
        }

        // Grade Bottom Sheet
        if (selectedSubmissionForGrading != null) {
            GradeBottomSheet(
                submission = selectedSubmissionForGrading!!,
                onDismiss = { selectedSubmissionForGrading = null },
                onGradeSubmit = { grade, remarks ->
                    coroutineScope.launch {
                        val subId = selectedSubmissionForGrading!!.submissionId
                        val res = repo.gradeSubmission(subId, grade, remarks)
                        selectedSubmissionForGrading = null
                        if (res.isSuccess) {
                            Toast.makeText(context, "Submission graded!", Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            Toast.makeText(context, "Failed to submit grade", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SubmissionRowItem(
    submission: TeacherHomeworkSubmission,
    onGradeClick: () -> Unit
) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGradeClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MyTuitionColors.PrimaryPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    submission.studentName.take(1).uppercase(),
                    style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold, color = MyTuitionColors.PrimaryPurple)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    submission.studentName,
                    style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MyTuitionColors.TextPrimary
                )
                Text(
                    "Roll No: ${submission.rollNumber} • ${submission.submittedAt ?: "Pending"}",
                    style = MyTuitionTypography.BodySmall,
                    color = MyTuitionColors.TextSecondary
                )
            }

            if (submission.grade != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MyTuitionColors.SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        "Grade ${submission.grade}",
                        style = MyTuitionTypography.LabelSmall.copy(fontWeight = FontWeight.Bold, color = MyTuitionColors.SuccessGreen),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        "Grade",
                        style = MyTuitionTypography.LabelSmall.copy(fontWeight = FontWeight.Medium, color = MyTuitionColors.PrimaryPurple),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeBottomSheet(
    submission: TeacherHomeworkSubmission,
    onDismiss: () -> Unit,
    onGradeSubmit: (String, String) -> Unit
) {
    var selectedGrade by remember { mutableStateOf(submission.grade ?: "A") }
    var remarks by remember { mutableStateOf(submission.remarks ?: "") }
    val grades = listOf("A+", "A", "B", "C", "Needs Improvement")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Grade Submission",
                style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                color = MyTuitionColors.TextPrimary
            )
            Text(
                "Student: ${submission.studentName}",
                style = MyTuitionTypography.BodyMedium,
                color = MyTuitionColors.TextSecondary
            )

            Text("Select Grade", style = MyTuitionTypography.LabelMedium.copy(fontWeight = FontWeight.Bold))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grades.forEach { g ->
                    FilterChip(
                        selected = selectedGrade == g,
                        onClick = { selectedGrade = g },
                        label = { Text(g) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MyTuitionColors.PrimaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Teacher Remarks") },
                placeholder = { Text("e.g., Neat handwriting, well explained.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { onGradeSubmit(selectedGrade, remarks) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple)
            ) {
                Text("Save Grade & Feedback", fontWeight = FontWeight.Bold)
            }
        }
    }
}

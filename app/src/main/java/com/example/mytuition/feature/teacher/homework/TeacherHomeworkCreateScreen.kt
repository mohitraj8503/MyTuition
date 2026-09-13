package com.example.mytuition.feature.teacher.homework

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.*
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeworkCreateScreen(
    batchId: String,
    onBackClick: () -> Unit,
    onHomeworkCreated: () -> Unit = onBackClick
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val teacherRepository = AppContainer.teacherRepository

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var chapter by remember { mutableStateOf("") }
    var dueDate by remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
    }
    var notifyStudents by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Create Homework",
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Homework Details",
                            style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MyTuitionColors.TextPrimary
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title *") },
                            placeholder = { Text("e.g., Quadratic Equations Ex 4.2") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = chapter,
                            onValueChange = { chapter = it },
                            label = { Text("Chapter / Unit") },
                            placeholder = { Text("e.g., Chapter 4: Quadratic Equations") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Instructions / Description") },
                            placeholder = { Text("Complete exercises 1 to 10 in homework notebook...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date (YYYY-MM-DD) *") },
                            leadingIcon = {
                                Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = MyTuitionColors.PrimaryPurple)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Notify Students & Parents",
                                    style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MyTuitionColors.TextPrimary
                                )
                                Text(
                                    "Sends instant push notification",
                                    style = MyTuitionTypography.BodySmall,
                                    color = MyTuitionColors.TextSecondary
                                )
                            }
                            Switch(
                                checked = notifyStudents,
                                onCheckedChange = { notifyStudents = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MyTuitionColors.PrimaryPurple
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "Please enter a homework title", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSubmitting = true
                        coroutineScope.launch {
                            val res = teacherRepository.createHomework(
                                batchId = batchId,
                                title = title.trim(),
                                description = description.trim(),
                                chapter = chapter.trim(),
                                dueDate = dueDate.trim(),
                                attachments = emptyList(),
                                notifyStudents = notifyStudents
                            )
                            isSubmitting = false
                            if (res.isSuccess) {
                                Toast.makeText(context, "Homework assigned successfully!", Toast.LENGTH_SHORT).show()
                                onHomeworkCreated()
                            } else {
                                Toast.makeText(context, "Error: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Assign Homework",
                            style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

package com.example.mytuition.feature.teacher.students

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.ClayZeroState
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.StudentRosterItem
import com.example.mytuition.core.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TeacherStudentsUiState {
    object Loading : TeacherStudentsUiState
    data class Success(val students: List<StudentRosterItem>) : TeacherStudentsUiState
    data class Error(val message: String) : TeacherStudentsUiState
}

class TeacherStudentsViewModel(
    private val teacherRepository: TeacherRepository,
    private val batchId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherStudentsUiState>(TeacherStudentsUiState.Loading)
    val uiState: StateFlow<TeacherStudentsUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = TeacherStudentsUiState.Loading
            val result = teacherRepository.getBatchStudents(batchId)
            if (result.isSuccess) {
                _uiState.value = TeacherStudentsUiState.Success(result.getOrNull() ?: emptyList())
            } else {
                _uiState.value = TeacherStudentsUiState.Error("Failed to load students")
            }
        }
    }

    companion object {
        fun provideFactory(teacherRepository: TeacherRepository, batchId: String = "batch_10a_maths"): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TeacherStudentsViewModel(teacherRepository, batchId) as T
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherStudentsScreen(
    batchId: String = "batch_10a_maths",
    onBackClick: () -> Unit,
    onNavigateToStudentProfile: (String) -> Unit,
    onNavigateToAddStudent: () -> Unit,
    viewModel: TeacherStudentsViewModel = viewModel(
        factory = TeacherStudentsViewModel.provideFactory(AppContainer.teacherRepository, batchId)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("My Students", style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onNavigateToAddStudent,
                        containerColor = MyTuitionColors.PrimaryPurple,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Student", modifier = Modifier.size(28.dp))
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                ) {
                    // Search Bar
                    val searchCorner = RoundedCornerShape(16.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(searchCorner)
                            .background(Color.White)
                            .border(1.5.dp, Color(0xFFE8E5F0), searchCorner)
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search", tint = MyTuitionColors.TextTertiary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text("Search student by name or roll...", style = MyTuitionTypography.BodyMedium.copy(color = MyTuitionColors.TextTertiary, fontSize = 14.sp))
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF221A44)),
                                    cursorBrush = SolidColor(MyTuitionColors.PrimaryPurple),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val uiState = state) {
                        TeacherStudentsUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                            }
                        }

                        is TeacherStudentsUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(uiState.message, style = MyTuitionTypography.BodyMedium, color = MyTuitionColors.StatusRed)
                            }
                        }

                        is TeacherStudentsUiState.Success -> {
                            val filtered = uiState.students.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.rollNumber.contains(searchQuery, ignoreCase = true)
                            }

                            if (filtered.isEmpty()) {
                                ClayZeroState(
                                    title = "No Students Found",
                                    subtitle = "Tap '+ Add Student' below to enroll your first student with auto-credentials!"
                                )
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 80.dp)
                                ) {
                                    items(filtered, key = { it.studentId }) { student ->
                                        ClayCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onNavigateToStudentProfile(student.studentId) },
                                            cardColor = Color.White
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(14.dp)
                                                    .fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(42.dp)
                                                            .clip(CircleShape)
                                                            .background(MyTuitionColors.PrimaryPurpleLight),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        val initial = student.name.firstOrNull()?.toString() ?: "S"
                                                        Text(initial, style = MyTuitionTypography.LabelLarge.copy(color = MyTuitionColors.PrimaryPurple, fontWeight = FontWeight.Bold))
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Column {
                                                        Text(student.name, style = MyTuitionTypography.BodyMedium.copy(fontWeight = FontWeight.Bold))
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text("Roll ${student.rollNumber} • ${student.attendancePercent}% attendance", style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextSecondary))
                                                    }
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    val feeStatusColor = when (student.feeStatus.lowercase()) {
                                                        "paid" -> Color(0xFF34C759)
                                                        "overdue" -> Color(0xFFFF3B30)
                                                        else -> Color(0xFFFF9500)
                                                    }
                                                    val feeStatusBg = when (student.feeStatus.lowercase()) {
                                                        "paid" -> Color(0xFFE8F9EE)
                                                        "overdue" -> Color(0xFFFFECEB)
                                                        else -> Color(0xFFFFF6E6)
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = feeStatusBg
                                                    ) {
                                                        Text(
                                                            text = student.feeStatus,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = feeStatusColor,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(6.dp))

                                                    Icon(
                                                        imageVector = Icons.Rounded.ChevronRight,
                                                        contentDescription = "Open",
                                                        tint = MyTuitionColors.TextTertiary,
                                                        modifier = Modifier.size(20.dp)
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
    }
}

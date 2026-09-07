package com.example.mytuition.feature.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.Homework
import com.example.mytuition.core.domain.model.HomeworkStatus
import com.example.mytuition.core.domain.model.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkDetailScreen(
    homeworkId: String,
    onNavigateBack: () -> Unit,
    viewModel: HomeworkDetailViewModel = viewModel(
        factory = HomeworkDetailViewModel.provideFactory(homeworkId, AppContainer.getHomeworkDetailUseCase, AppContainer.markHomeworkCompleteUseCase)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.05f))
                            .background(Color.White, CircleShape)
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MyTuitionColors.DeepNavyText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MyTuitionColors.WarmIvory)
            )
        },
        containerColor = MyTuitionColors.WarmIvory
    ) { padding ->
        when (val uiState = state) {
            is HomeworkDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PremiumPurple)
                }
            }
            is HomeworkDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MyTuitionColors.PremiumCoral)
                }
            }
            is HomeworkDetailUiState.Success -> {
                HomeworkDetailContent(
                    homework = uiState.homework,
                    modifier = Modifier.padding(padding),
                    onMarkComplete = viewModel::markComplete
                )
            }
        }
    }
}

@Composable
private fun HomeworkDetailContent(
    homework: Homework,
    modifier: Modifier = Modifier,
    onMarkComplete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = homework.title,
            style = MyTuitionTypography.Display.copy(fontSize = 28.sp, lineHeight = 34.sp),
            color = MyTuitionColors.DeepNavyText
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = homework.subjectName,
                    style = MyTuitionTypography.SectionTitle,
                    color = MyTuitionColors.PremiumPurple
                )
                Text(
                    text = "Assigned by ${homework.teacherName}",
                    style = MyTuitionTypography.Metadata,
                    color = MyTuitionColors.DeepNavyText.copy(alpha = 0.6f)
                )
                if (homework.dueAt != null) {
                    Text(
                        text = "Due: ${dateFormat.format(Date(homework.dueAt))}",
                        style = MyTuitionTypography.Metadata.copy(fontWeight = FontWeight.Bold),
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.8f)
                    )
                }
            }
            StatusBadge(status = homework.status)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = homework.description,
            style = MyTuitionTypography.Body.copy(fontSize = 16.sp, lineHeight = 24.sp),
            color = MyTuitionColors.DeepNavyText.copy(alpha = 0.8f)
        )
        
        if (homework.attachments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Attachments",
                style = MyTuitionTypography.SectionTitle,
                color = MyTuitionColors.DeepNavyText
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(homework.attachments) { resource ->
                    ResourceItem(resource = resource)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (homework.status != HomeworkStatus.COMPLETED) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp)
                    .background(MyTuitionColors.PremiumLime, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .clickable(onClick = onMarkComplete),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mark Complete",
                    style = MyTuitionTypography.SectionTitle,
                    color = MyTuitionColors.DeepNavyText
                )
            }
        }
    }
}

@Composable
private fun ResourceItem(resource: Resource) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { /* TODO: Open */ }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MyTuitionColors.PremiumPurple.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resource.type.name.take(3),
                    style = MyTuitionTypography.Caption,
                    color = MyTuitionColors.PremiumPurple
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = resource.title,
                    style = MyTuitionTypography.Body.copy(fontWeight = FontWeight.Bold),
                    color = MyTuitionColors.DeepNavyText
                )
                if (resource.sizeBytes != null) {
                    Text(
                        text = "${resource.sizeBytes / 1024} KB",
                        style = MyTuitionTypography.Metadata,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

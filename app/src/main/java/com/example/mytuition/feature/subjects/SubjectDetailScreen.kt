package com.example.mytuition.feature.subjects

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
import com.example.mytuition.core.domain.model.Resource
import com.example.mytuition.core.domain.model.SubjectDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHomeworkDetail: (String) -> Unit,
    viewModel: SubjectDetailViewModel = viewModel(
        factory = SubjectDetailViewModel.provideFactory(subjectId, AppContainer.getSubjectDetailUseCase)
    )
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .size(40.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.05f))
                            .background(Color.White, CircleShape)
                            .clickable(onClick = onNavigateBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MyTuitionColors.DeepNavyText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MyTuitionColors.WarmIvory
                )
            )
        },
        containerColor = MyTuitionColors.WarmIvory
    ) { padding ->
        when (val uiState = state) {
            is SubjectDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PremiumPurple)
                }
            }
            is SubjectDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = MyTuitionColors.PremiumCoral)
                }
            }
            is SubjectDetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = "Subject not found.", color = MyTuitionColors.DeepNavyText)
                }
            }
            is SubjectDetailUiState.Success -> {
                SubjectDetailContent(
                    detail = uiState.detail,
                    modifier = Modifier.padding(padding),
                    onNavigateToHomeworkDetail = onNavigateToHomeworkDetail
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailContent(
    detail: SubjectDetail,
    modifier: Modifier = Modifier,
    onNavigateToHomeworkDetail: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = detail.subject.name,
                    style = MyTuitionTypography.Display.copy(fontSize = 36.sp),
                    color = MyTuitionColors.DeepNavyText
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MyTuitionColors.PremiumBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            detail.subject.teacherName.take(1),
                            style = MyTuitionTypography.SectionTitle,
                            color = MyTuitionColors.PremiumBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = detail.subject.teacherName,
                        style = MyTuitionTypography.Body,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Next class", style = MyTuitionTypography.Metadata, color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))
                        val timeStr = detail.subject.nextClass?.let { dateFormat.format(Date(it)) } ?: "Not scheduled"
                        Text(timeStr, style = MyTuitionTypography.SectionTitle, color = MyTuitionColors.DeepNavyText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pending HW", style = MyTuitionTypography.Metadata, color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(detail.subject.homeworkCount.toString(), style = MyTuitionTypography.SectionTitle, color = MyTuitionColors.DeepNavyText)
                    }
                }
            }
        }
        
        if (detail.recentHomework.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Homework",
                    style = MyTuitionTypography.LargeTitle,
                    color = MyTuitionColors.DeepNavyText,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            items(detail.recentHomework) { homework ->
                ClayHomeworkItem(homework, onClick = { onNavigateToHomeworkDetail(homework.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        if (detail.recentResources.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Resources",
                    style = MyTuitionTypography.LargeTitle,
                    color = MyTuitionColors.DeepNavyText,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
            items(detail.recentResources) { resource ->
                ClayResourceItem(resource)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ClayHomeworkItem(homework: Homework, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = homework.title,
                style = MyTuitionTypography.SectionTitle,
                color = MyTuitionColors.DeepNavyText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Status: ${homework.status.name}",
                style = MyTuitionTypography.Metadata,
                color = MyTuitionColors.DeepNavyText.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ClayResourceItem(resource: Resource) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { /* TODO */ }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MyTuitionColors.PremiumPurple.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = resource.type.name.take(3), style = MyTuitionTypography.Caption, color = MyTuitionColors.PremiumPurple)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = resource.title,
                style = MyTuitionTypography.Body.copy(fontWeight = FontWeight.Bold),
                color = MyTuitionColors.DeepNavyText
            )
        }
    }
}

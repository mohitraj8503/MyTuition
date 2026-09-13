package com.example.mytuition.feature.teacher.announcements

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.mytuition.core.domain.model.AnnouncementItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAnnouncementsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repo = AppContainer.teacherRepository

    var announcements by remember { mutableStateOf<List<AnnouncementItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateSheet by remember { mutableStateOf(false) }

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            val res = repo.getTeacherAnnouncements()
            announcements = res.getOrDefault(emptyList())
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Announcements",
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateSheet = true },
                    containerColor = MyTuitionColors.PrimaryPurple,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "New Announcement")
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                }
            } else if (announcements.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No announcements yet. Tap + to broadcast.", color = MyTuitionColors.TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(announcements) { ann ->
                        AnnouncementCard(ann = ann)
                    }
                }
            }
        }

        if (showCreateSheet) {
            CreateAnnouncementBottomSheet(
                onDismiss = { showCreateSheet = false },
                onPost = { title, message, type ->
                    coroutineScope.launch {
                        val res = repo.postAnnouncement(title, message, type, emptyList())
                        showCreateSheet = false
                        if (res.isSuccess) {
                            Toast.makeText(context, "Announcement posted!", Toast.LENGTH_SHORT).show()
                            loadData()
                        } else {
                            Toast.makeText(context, "Failed to post announcement", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun AnnouncementCard(ann: AnnouncementItem) {
    val tagColor = when (ann.type.uppercase()) {
        "URGENT" -> MyTuitionColors.ErrorRed
        "HOLIDAY" -> MyTuitionColors.AccentGold
        "EVENT" -> MyTuitionColors.PrimaryPurple
        else -> Color(0xFF64748B)
    }

    ClayCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tagColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        ann.type.uppercase(),
                        style = MyTuitionTypography.LabelSmall.copy(fontWeight = FontWeight.Bold, color = tagColor),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    ann.date,
                    style = MyTuitionTypography.BodySmall,
                    color = MyTuitionColors.TextSecondary
                )
            }

            Text(
                ann.title,
                style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                color = MyTuitionColors.TextPrimary
            )

            Text(
                ann.message,
                style = MyTuitionTypography.BodyMedium,
                color = MyTuitionColors.TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateAnnouncementBottomSheet(
    onDismiss: () -> Unit,
    onPost: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("GENERAL") }
    val types = listOf("GENERAL", "HOLIDAY", "URGENT", "EVENT")

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
                "New Announcement",
                style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold),
                color = MyTuitionColors.TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                types.forEach { t ->
                    FilterChip(
                        selected = selectedType == t,
                        onClick = { selectedType = t },
                        label = { Text(t) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MyTuitionColors.PrimaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                placeholder = { Text("e.g. Sunday Doubt Session") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message *") },
                placeholder = { Text("Write announcement details here...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onPost(title.trim(), message.trim(), selectedType)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                enabled = title.isNotBlank() && message.isNotBlank()
            ) {
                Text("Broadcast Announcement", fontWeight = FontWeight.Bold)
            }
        }
    }
}

package com.example.mytuition.feature.teacher.earnings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.*
import com.example.mytuition.core.designsystem.components.*
import com.example.mytuition.core.di.AppContainer
import com.example.mytuition.core.domain.model.TeacherBatchEarning
import com.example.mytuition.core.domain.model.TeacherEarning
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherEarningsScreen(
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val repo = AppContainer.teacherRepository

    val now = Calendar.getInstance()
    var selectedYear by remember { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var selectedMonthNum by remember { mutableIntStateOf(now.get(Calendar.MONTH) + 1) }

    var earningsData by remember { mutableStateOf<TeacherEarning?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        coroutineScope.launch {
            isLoading = true
            val monthStr = String.format(Locale.getDefault(), "%02d", selectedMonthNum)
            val res = repo.getTeacherEarnings(month = monthStr, year = selectedYear)
            earningsData = res.getOrNull()
            isLoading = false
        }
    }

    LaunchedEffect(selectedMonthNum, selectedYear) {
        loadData()
    }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Earnings & Collections",
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Month Picker Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (selectedMonthNum > 1) selectedMonthNum-- else {
                                selectedMonthNum = 12
                                selectedYear--
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        "${SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, selectedMonthNum - 1) }.time)} $selectedYear",
                        style = MyTuitionTypography.TitleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    IconButton(
                        onClick = {
                            if (selectedMonthNum < 12) selectedMonthNum++ else {
                                selectedMonthNum = 1
                                selectedYear++
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.ArrowForward, contentDescription = "Next Month")
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MyTuitionColors.PrimaryPurple)
                    }
                } else if (earningsData != null) {
                    val d = earningsData!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Big Summary Card
                        item {
                            ClayCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "Total Collected This Month",
                                        style = MyTuitionTypography.BodyMedium,
                                        color = MyTuitionColors.TextSecondary
                                    )
                                    Text(
                                        "₹${d.totalCollected}",
                                        style = MyTuitionTypography.HeadlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MyTuitionColors.SuccessGreen
                                        )
                                    )
                                    Divider(color = Color(0xFFF1F5F9))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Pending Students",
                                            style = MyTuitionTypography.BodyMedium,
                                            color = MyTuitionColors.TextSecondary
                                        )
                                        Text(
                                            "${d.totalPendingStudents} Unpaid",
                                            style = MyTuitionTypography.BodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (d.totalPendingStudents > 0) MyTuitionColors.ErrorRed else MyTuitionColors.TextPrimary
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "Batch Breakdown",
                                style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (d.batchSummaries.isEmpty()) {
                            item {
                                ClayCard(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("No batches found for this period.", color = MyTuitionColors.TextSecondary)
                                    }
                                }
                            }
                        } else {
                            items(d.batchSummaries) { b ->
                                BatchEarningCard(batch = b)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchEarningCard(batch: TeacherBatchEarning) {
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
                Text(
                    batch.batchName,
                    style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "₹${batch.amountCollected}",
                    style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold, color = MyTuitionColors.SuccessGreen)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Paid: ${batch.feesCollectedCount}/${batch.totalStudents}",
                    style = MyTuitionTypography.BodyMedium,
                    color = MyTuitionColors.TextSecondary
                )
                if (batch.feesPendingCount > 0) {
                    Text(
                        "${batch.feesPendingCount} Pending",
                        style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.ErrorRed, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

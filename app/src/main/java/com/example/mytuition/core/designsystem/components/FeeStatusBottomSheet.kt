package com.example.mytuition.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.domain.model.FeeStatus
import com.example.mytuition.core.domain.model.HomeSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeStatusBottomSheet(
    summary: HomeSummary?,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF9F9FF),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFD6D0F5))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tuition Fee Status",
                    style = MyTuitionTypography.HeadlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MyTuitionColors.TextPrimary
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBE6FF))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Fee Clay Card
            val feeStatus = summary?.feeStatus ?: FeeStatus.PAID
            val amountText = if (summary?.feeAmount != null) "₹${summary.feeAmount.toInt()}" else "₹2,500"

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x221A1A1A))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFFEEEAF8), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Monthly Tuition Fee",
                                style = MyTuitionTypography.LabelMedium.copy(
                                    color = MyTuitionColors.TextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "August 2025 • Term 1",
                                style = MyTuitionTypography.TitleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MyTuitionColors.TextPrimary
                                )
                            )
                        }

                        // Status Pill
                        val (pillBg, pillText, pillColor) = when (feeStatus) {
                            FeeStatus.PAID -> Triple(Color(0xFFE8F9EE), "Paid ✓", Color(0xFF34C759))
                            FeeStatus.PENDING -> Triple(Color(0xFFFFF6E6), "Due 10 Sep", Color(0xFFFF9500))
                            FeeStatus.OVERDUE -> Triple(Color(0xFFFFECEB), "Overdue", Color(0xFFFF3B30))
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = pillBg
                        ) {
                            Text(
                                text = pillText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0ECFB), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount Payable",
                            style = MyTuitionTypography.BodyMedium.copy(
                                color = MyTuitionColors.TextSecondary,
                                fontSize = 14.sp
                            )
                        )

                        Text(
                            text = amountText,
                            style = MyTuitionTypography.HeadlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = MyTuitionColors.PrimaryPurple
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Offline Payment Guidance Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (feeStatus == FeeStatus.PAID) Color(0xFFE8F9EE) else Color(0xFFF4F0FF))
                    .border(1.5.dp, if (feeStatus == FeeStatus.PAID) Color(0xFFBCEECE) else Color(0xFFDED3FF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (feeStatus == FeeStatus.PAID) Icons.Rounded.CheckCircle else Icons.Rounded.Receipt,
                        contentDescription = null,
                        tint = if (feeStatus == FeeStatus.PAID) Color(0xFF34C759) else MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (feeStatus == FeeStatus.PAID) "Fee Received by Teacher ✓" else "Pay Directly to Teacher",
                            style = MyTuitionTypography.TitleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (feeStatus == FeeStatus.PAID) Color(0xFF1B8738) else MyTuitionColors.PrimaryPurple
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (feeStatus == FeeStatus.PAID)
                                "Your tuition fee has been marked as received. Thank you!"
                            else
                                "Please pay your teacher directly via Cash or direct UPI. Once received, your teacher will confirm and mark your fee as paid.",
                            style = MyTuitionTypography.BodySmall.copy(
                                color = MyTuitionColors.TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Past Payment History Record
            Text(
                text = "Recent Transactions",
                style = MyTuitionTypography.TitleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color(0xFFF0ECFB), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F9EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Receipt,
                            contentDescription = null,
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tuition Fee Record",
                            style = MyTuitionTypography.BodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MyTuitionColors.TextPrimary
                            )
                        )
                        Text(
                            text = "Direct payment to Teacher",
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 11.sp,
                                color = MyTuitionColors.TextSecondary
                            )
                        )
                    }
                }

                Text(
                    text = amountText,
                    style = MyTuitionTypography.TitleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

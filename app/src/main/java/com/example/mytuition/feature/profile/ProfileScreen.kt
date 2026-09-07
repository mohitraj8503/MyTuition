package com.example.mytuition.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTuitionColors.WarmIvory)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = 48.dp, bottom = MyTuitionSpacing.BottomNavPadding),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(16.dp, CircleShape, spotColor = MyTuitionColors.PremiumPurple.copy(alpha = 0.3f))
                            .background(MyTuitionColors.PremiumPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MR",
                            style = MyTuitionTypography.Display.copy(color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mohit Raj",
                        style = MyTuitionTypography.Display.copy(fontSize = 24.sp),
                        color = MyTuitionColors.DeepNavyText
                    )
                    Text(
                        text = "Class 10-A",
                        style = MyTuitionTypography.Body,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.6f)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Settings",
                        style = MyTuitionTypography.SectionTitle,
                        color = MyTuitionColors.DeepNavyText.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.04f))
                            .background(Color.White, RoundedCornerShape(24.dp))
                    ) {
                        Column {
                            SettingRow("Notifications")
                            HorizontalDivider(color = MyTuitionColors.DeepNavyText.copy(alpha = 0.05f))
                            SettingRow("Language")
                            HorizontalDivider(color = MyTuitionColors.DeepNavyText.copy(alpha = 0.05f))
                            SettingRow("Help & Support")
                            HorizontalDivider(color = MyTuitionColors.DeepNavyText.copy(alpha = 0.05f))
                            SettingRow("Privacy Policy")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = MyTuitionColors.PremiumCoral.copy(alpha = 0.2f))
                            .background(MyTuitionColors.PremiumCoral.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(onClick = onLogout)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Logout",
                            style = MyTuitionTypography.SectionTitle.copy(color = MyTuitionColors.PremiumCoral)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MyTuitionTypography.Body,
            color = MyTuitionColors.DeepNavyText
        )
    }
}

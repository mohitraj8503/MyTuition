package com.example.mytuition.feature.profile

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.designsystem.darken

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md)
            ) {
                Text(
                    text = "My Profile",
                    style = MyTuitionTypography.HeadlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = MyTuitionSpacing.lg,
                    end = MyTuitionSpacing.lg,
                    bottom = 110.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Profile Hero Card
                item {
                    ProfileHeroCard()
                }

                // 2. Quick Stats Row (3 cards)
                item {
                    QuickStatsRow()
                }

                // 3. Settings Card
                item {
                    SettingsCard(
                        notificationsEnabled = notificationsEnabled,
                        onToggleNotifications = { notificationsEnabled = it }
                    )
                }

                // 4. Logout Button
                item {
                    val logoutSource = remember { MutableInteractionSource() }
                    val isLogoutPressed by logoutSource.collectIsPressedAsState()
                    val logoutScale by animateFloatAsState(
                        targetValue = if (isLogoutPressed) 0.96f else 1f,
                        animationSpec = MyTuitionAnimations.claySpring,
                        label = "logoutScale"
                    )

                    Box(
                        modifier = Modifier
                            .scale(logoutScale)
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = if (isLogoutPressed) 3.dp else 8.dp,
                                shape = MyTuitionShapes.PillShape,
                                spotColor = Color(0x30FF5252),
                                ambientColor = Color(0x15FF5252)
                            )
                            .clip(MyTuitionShapes.PillShape)
                            .background(Color(0xFFFFEBEE))
                            .border(2.dp, Color(0xFFFFCDD2), MyTuitionShapes.PillShape)
                            .clickable(
                                interactionSource = logoutSource,
                                indication = null,
                                onClick = onLogout
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Logout,
                                contentDescription = "Log Out",
                                tint = MyTuitionColors.StatusRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sign Out",
                                style = MyTuitionTypography.TitleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MyTuitionColors.StatusRed
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "MyTuition v2.0 • Designed with Care ✨",
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            color = MyTuitionColors.TextTertiary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(modifier: Modifier = Modifier) {
    val cardShape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = cardShape,
                ambientColor = Color(0x331A1A1A),
                spotColor = Color(0x221A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Inner bottom shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.04f))
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // MyTuition Logo (72dp with clay card) centered above user name
            MyTuitionLogo(
                size = 72.dp,
                showClayCard = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mohit Raj",
                style = MyTuitionTypography.TitleExtra.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Grade 11 • Science Stream",
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 15.sp,
                    color = MyTuitionColors.TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Student ID white clay pill
            Box(
                modifier = Modifier
                    .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x10000000))
                    .clip(MyTuitionShapes.PillShape)
                    .background(MyTuitionColors.PrimaryPurpleLight)
                    .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), MyTuitionShapes.PillShape)
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "ID: STU-2024-0891",
                    style = MyTuitionTypography.LabelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.PrimaryPurple
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickStatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = "94%",
            label = "Attendance",
            valueColor = MyTuitionColors.OnlineGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "18/20",
            label = "HW Done",
            valueColor = MyTuitionColors.PrimaryPurple,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "3.8",
            label = "GPA / Grade",
            valueColor = MyTuitionColors.DifficultyDot,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = cardShape,
                ambientColor = Color(0x1A1A1A1A),
                spotColor = Color(0x141A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
            .padding(vertical = 16.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MyTuitionTypography.HeadlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MyTuitionTypography.LabelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTuitionColors.TextSecondary
                )
            )
        }
    }
}

@Composable
private fun SettingsCard(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = Color(0x221A1A1A),
                spotColor = Color(0x181A1A1A)
            )
            .clip(cardShape)
            .background(MyTuitionColors.CardWhite)
            .border(2.dp, MyTuitionColors.CardWhite.darken(0.08f), cardShape)
    ) {
        Column {
            SettingRow(
                icon = Icons.Rounded.Person,
                title = "Personal Information",
                onClick = {}
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRowWithSwitch(
                icon = Icons.Rounded.Notifications,
                title = "Notifications",
                checked = notificationsEnabled,
                onCheckedChange = onToggleNotifications
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRow(
                icon = Icons.Rounded.School,
                title = "Tuition Center Info",
                onClick = {}
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRow(
                icon = Icons.Rounded.HelpOutline,
                title = "Help & Support",
                onClick = {}
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "settingRowScale"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clickable(
                interactionSource = source,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(MyTuitionColors.PrimaryPurpleLight)
                    .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MyTuitionTypography.BodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTuitionColors.TextPrimary
                )
            )
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MyTuitionColors.TextTertiary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SettingRowWithSwitch(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp), spotColor = MyTuitionColors.PrimaryPurple.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(MyTuitionColors.PrimaryPurpleLight)
                    .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MyTuitionColors.PrimaryPurple,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MyTuitionTypography.BodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTuitionColors.TextPrimary
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MyTuitionColors.PrimaryPurple,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MyTuitionColors.DividerColor
            )
        )
    }
}

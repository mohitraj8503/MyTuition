package com.example.mytuition.feature.profile

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytuition.core.data.repository.UserProfileData
import com.example.mytuition.core.designsystem.MyTuitionAnimations
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionShapes
import com.example.mytuition.core.designsystem.MyTuitionSpacing
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.PastelBackground
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.designsystem.darken
import com.example.mytuition.core.di.AppContainer

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(
            AppContainer.profileRepository,
            AppContainer.authRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isUpdating by viewModel.isUpdating.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showTuitionInfoDialog by remember { mutableStateOf(false) }
    var showHelpSupportDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    PastelBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MyTuitionSpacing.lg, vertical = MyTuitionSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Profile",
                        style = MyTuitionTypography.HeadlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.TextPrimary
                        )
                    )
                    Text(
                        text = "Academic & Account Settings",
                        style = MyTuitionTypography.BodyMedium.copy(
                            fontSize = 14.sp,
                            color = MyTuitionColors.TextSecondary
                        )
                    )
                }

                // Edit Profile Icon Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x18000000))
                        .clip(CircleShape)
                        .background(MyTuitionColors.CardWhite)
                        .border(1.5.dp, MyTuitionColors.CardWhite.darken(0.08f), CircleShape)
                        .clickable { showEditProfileDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Profile",
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MyTuitionColors.PrimaryPurple,
                            strokeWidth = 3.dp
                        )
                    }
                }
                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                style = MyTuitionTypography.BodyLarge,
                                color = MyTuitionColors.StatusRed
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadProfile() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    val profile = state.profile

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
                            ProfileHeroCard(
                                profile = profile,
                                onEditClick = { showEditProfileDialog = true }
                            )
                        }

                        // 2. Quick Stats Row (3 cards: Attendance, HW Done, GPA)
                        item {
                            QuickStatsRow(
                                attendance = "${profile.attendancePercent}%",
                                homeworkDone = profile.homeworkDoneText,
                                gpa = profile.gpaGrade
                            )
                        }

                        // 3. Academic Details Clay Card
                        item {
                            AcademicDetailsCard(profile = profile)
                        }

                        // 4. Settings & Options Card
                        item {
                            SettingsCard(
                                notificationsEnabled = profile.notificationsEnabled,
                                onToggleNotifications = { enabled ->
                                    viewModel.toggleNotifications(enabled)
                                    Toast.makeText(
                                        context,
                                        if (enabled) "Notifications turned ON" else "Notifications turned OFF",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onPersonalClick = { showEditProfileDialog = true },
                                onTuitionClick = { showTuitionInfoDialog = true },
                                onHelpClick = { showHelpSupportDialog = true }
                            )
                        }

                        // 5. Logout Button
                        item {
                            LogoutButton(onClick = { showLogoutConfirmDialog = true })
                        }

                        item {
                            Text(
                                text = "MyTuition v2.1 • Student Portal • Secure & Encrypted 🛡️",
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

                    // Edit Profile Dialog
                    if (showEditProfileDialog) {
                        EditProfileDialog(
                            currentProfile = profile,
                            isUpdating = isUpdating,
                            onDismiss = { showEditProfileDialog = false },
                            onSave = { name, email, phone, school ->
                                viewModel.updateProfile(name, email, phone, school) {
                                    showEditProfileDialog = false
                                    Toast.makeText(context, "Profile updated successfully! ✨", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    // Tuition Center Info Dialog
                    if (showTuitionInfoDialog) {
                        TuitionInfoDialog(
                            profile = profile,
                            onDismiss = { showTuitionInfoDialog = false }
                        )
                    }

                    // Help & Support Dialog
                    if (showHelpSupportDialog) {
                        HelpSupportDialog(
                            onDismiss = { showHelpSupportDialog = false }
                        )
                    }

                    // Logout Confirmation Dialog
                    if (showLogoutConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutConfirmDialog = false },
                            title = {
                                Text(
                                    text = "Sign Out",
                                    style = MyTuitionTypography.TitleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            text = {
                                Text(
                                    text = "Are you sure you want to sign out from your student account?",
                                    style = MyTuitionTypography.BodyMedium
                                )
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.StatusRed),
                                    onClick = {
                                        showLogoutConfirmDialog = false
                                        viewModel.logout(onLogout)
                                    }
                                ) {
                                    Text("Sign Out", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                                    Text("Cancel", color = MyTuitionColors.TextSecondary)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    profile: UserProfileData,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // MyTuition Logo
            MyTuitionLogo(
                size = 76.dp,
                showClayCard = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = profile.name,
                style = MyTuitionTypography.TitleExtra.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MyTuitionColors.TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${profile.classGrade} (Sec ${profile.section}) • Roll No. ${profile.rollNumber}",
                style = MyTuitionTypography.BodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MyTuitionColors.TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = profile.schoolName,
                style = MyTuitionTypography.BodySmall.copy(
                    fontSize = 13.sp,
                    color = MyTuitionColors.PrimaryPurple,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Student ID pill
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x10000000))
                        .clip(MyTuitionShapes.PillShape)
                        .background(MyTuitionColors.PrimaryPurpleLight)
                        .border(1.5.dp, MyTuitionColors.PrimaryPurpleLight.darken(0.08f), MyTuitionShapes.PillShape)
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "ID: ${profile.studentId}",
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }

                if (profile.isDemo) {
                    Box(
                        modifier = Modifier
                            .shadow(2.dp, MyTuitionShapes.PillShape, spotColor = Color(0x10000000))
                            .clip(MyTuitionShapes.PillShape)
                            .background(Color(0xFFFFF3E0))
                            .border(1.5.dp, Color(0xFFFFCC80), MyTuitionShapes.PillShape)
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "⚡ Demo Account",
                            style = MyTuitionTypography.LabelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    attendance: String,
    homeworkDone: String,
    gpa: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = attendance,
            label = "Attendance",
            valueColor = MyTuitionColors.OnlineGreen,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = homeworkDone,
            label = "HW Done",
            valueColor = MyTuitionColors.PrimaryPurple,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = gpa,
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
                    fontSize = 20.sp,
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
private fun AcademicDetailsCard(
    profile: UserProfileData,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 28.dp,
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MyTuitionColors.PrimaryPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.School,
                        contentDescription = null,
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Academic Information",
                    style = MyTuitionTypography.TitleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoItemRow(label = "Institute", value = profile.instituteName)
            Spacer(modifier = Modifier.height(10.dp))
            InfoItemRow(label = "Email Address", value = profile.email)
            Spacer(modifier = Modifier.height(10.dp))
            InfoItemRow(label = "Phone Number", value = profile.phone)
            Spacer(modifier = Modifier.height(10.dp))
            InfoItemRow(label = "Target Exams", value = "CBSE Board 2025 • JEE Foundation")
        }
    }
}

@Composable
private fun InfoItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MyTuitionTypography.BodySmall.copy(
                fontSize = 13.sp,
                color = MyTuitionColors.TextSecondary
            )
        )
        Text(
            text = value,
            style = MyTuitionTypography.BodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MyTuitionColors.TextPrimary
            )
        )
    }
}

@Composable
private fun SettingsCard(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onPersonalClick: () -> Unit,
    onTuitionClick: () -> Unit,
    onHelpClick: () -> Unit,
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
                title = "Edit Personal Information",
                onClick = onPersonalClick
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRowWithSwitch(
                icon = Icons.Rounded.Notifications,
                title = "Push Notifications",
                checked = notificationsEnabled,
                onCheckedChange = onToggleNotifications
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRow(
                icon = Icons.Rounded.School,
                title = "Tuition Center Details",
                onClick = onTuitionClick
            )
            HorizontalDivider(thickness = 1.dp, color = MyTuitionColors.DividerColor)

            SettingRow(
                icon = Icons.AutoMirrored.Rounded.HelpOutline,
                title = "Help, FAQs & Support",
                onClick = onHelpClick
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
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    val logoutSource = remember { MutableInteractionSource() }
    val isLogoutPressed by logoutSource.collectIsPressedAsState()
    val logoutScale by animateFloatAsState(
        targetValue = if (isLogoutPressed) 0.96f else 1f,
        animationSpec = MyTuitionAnimations.claySpring,
        label = "logoutScale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = logoutScale
                scaleY = logoutScale
            }
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
                onClick = onClick
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

@Composable
private fun EditProfileDialog(
    currentProfile: UserProfileData,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onSave: (name: String, email: String, phone: String, school: String) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var school by remember { mutableStateOf(currentProfile.schoolName) }

    Dialog(onDismissRequest = onDismiss) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Edit Profile Info",
                    style = MyTuitionTypography.TitleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("School Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, enabled = !isUpdating) {
                        Text("Cancel", color = MyTuitionColors.TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name, email, phone, school) },
                        enabled = !isUpdating && name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Changes", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TuitionInfoDialog(
    profile: UserProfileData,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MyTuitionLogo(size = 40.dp, showClayCard = true)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = profile.instituteName,
                            style = MyTuitionTypography.TitleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MyTuitionColors.TextPrimary
                            )
                        )
                        Text(
                            text = "Affiliated Coaching Institute",
                            style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                InfoItemRow(label = "📍 Address", value = profile.instituteAddress)
                Spacer(modifier = Modifier.height(10.dp))
                InfoItemRow(label = "📞 Phone", value = profile.institutePhone)
                Spacer(modifier = Modifier.height(10.dp))
                InfoItemRow(label = "✉️ Email", value = profile.instituteEmail)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp,
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Help & Support 💬",
                    style = MyTuitionTypography.TitleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MyTuitionColors.TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Have questions regarding your tuition timings, homework, or fee receipts?",
                    style = MyTuitionTypography.BodyMedium.copy(color = MyTuitionColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MyTuitionColors.PrimaryPurpleLight)
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Admin Helpdesk",
                            style = MyTuitionTypography.LabelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MyTuitionColors.PrimaryPurple
                            )
                        )
                        Text(
                            text = "Available Monday–Saturday (9:00 AM - 7:00 PM)",
                            style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextPrimary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "WhatsApp: +91 98765 00000\nEmail: help@mytuition.app",
                            style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.PrimaryPurple)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MyTuitionColors.PrimaryPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Got It", color = Color.White)
                }
            }
        }
    }
}

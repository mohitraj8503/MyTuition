package com.example.mytuition.feature.teacher.students

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.PillButton
import com.example.mytuition.core.designsystem.components.PillButtonVariant
import com.example.mytuition.core.domain.model.StudentCredentials

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialsCard(
    credentials: StudentCredentials,
    studentName: String,
    batchName: String,
    timingDisplay: String,
    feeDisplay: String,
    onResetPassword: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(false) } // Hidden by default in vault
    var showResetDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied! 📋", Toast.LENGTH_SHORT).show()
    }

    val shareText = """
Hello! Here are the login details for $studentName on MyTuition:

👤 Username: ${credentials.username}
🔑 Password: ${credentials.passwordPlain}

Batch: $batchName
Timing: $timingDisplay
Monthly Fee: $feeDisplay

— Chanakya Classes
    """.trimIndent()

    fun shareWhatsApp() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            `package` = "com.whatsapp"
        }
        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            val chooser = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }, "Share Credentials via")
            context.startActivity(chooser)
        }
    }

    ClayCard(
        modifier = modifier.fillMaxWidth(),
        cardColor = Color.White
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Login Credentials",
                        style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Text(
                    text = "Teacher Vault 🔐",
                    style = MyTuitionTypography.LabelSmall.copy(color = MyTuitionColors.TextTertiary)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Username Box
            val boxShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(boxShape)
                    .background(Color(0xFFF7F5FC))
                    .border(1.dp, Color(0xFFECE7F7), boxShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "USERNAME",
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MyTuitionColors.TextTertiary
                            )
                        )
                        Text(
                            text = credentials.username,
                            style = MyTuitionTypography.BodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF221A44)
                            )
                        )
                    }
                    IconButton(
                        onClick = { copyToClipboard("Username", credentials.username) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = "Copy Username",
                            tint = MyTuitionColors.PrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Password Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(boxShape)
                    .background(Color(0xFFF7F5FC))
                    .border(1.dp, Color(0xFFECE7F7), boxShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PASSWORD",
                            style = MyTuitionTypography.LabelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MyTuitionColors.TextTertiary
                            )
                        )
                        Text(
                            text = if (isPasswordVisible) credentials.passwordPlain else "••••••••",
                            style = MyTuitionTypography.BodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF221A44)
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { copyToClipboard("Password", credentials.passwordPlain) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy Password",
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Pills Row: [ Reset Password ↻ ]  [ Share 📤 ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PillButton(
                    text = "Reset Password ↻",
                    onClick = { showResetDialog = true },
                    modifier = Modifier.weight(1f),
                    variant = PillButtonVariant.Outline
                )
                PillButton(
                    text = "Share 📤",
                    onClick = { shareWhatsApp() },
                    modifier = Modifier.weight(1f),
                    variant = PillButtonVariant.Primary
                )
            }
        }
    }

    if (showResetDialog) {
        ModalBottomSheet(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color.White
        ) {
            var newPass by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text("Reset Password for $studentName", style = MyTuitionTypography.TitleSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Enter a new password or leave empty to auto-generate a simple one (e.g. ayush@491).", style = MyTuitionTypography.BodySmall.copy(color = MyTuitionColors.TextSecondary))

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF7F6FA))
                        .border(1.5.dp, Color(0xFFE5E2EC), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (newPass.isEmpty()) {
                        Text("New password (optional)", style = MyTuitionTypography.BodyMedium.copy(color = MyTuitionColors.TextTertiary))
                    }
                    BasicTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF221A44)),
                        cursorBrush = SolidColor(MyTuitionColors.PrimaryPurple),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                PillButton(
                    text = "Confirm & Update Password",
                    onClick = {
                        onResetPassword(newPass.trim().ifEmpty { null })
                        showResetDialog = false
                        Toast.makeText(context, "Password updated successfully! ✓", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

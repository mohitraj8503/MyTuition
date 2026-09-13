package com.example.mytuition.feature.teacher.students.register

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mytuition.core.designsystem.MyTuitionColors
import com.example.mytuition.core.designsystem.MyTuitionTypography
import com.example.mytuition.core.designsystem.components.ClayCard
import com.example.mytuition.core.designsystem.components.MyTuitionLogo
import com.example.mytuition.core.designsystem.components.PillButton
import com.example.mytuition.core.designsystem.components.PillButtonVariant
import com.example.mytuition.core.domain.model.RegistrationResult

@Composable
fun CredentialsSuccessCard(
    result: RegistrationResult,
    studentName: String,
    timingDisplay: String,
    monthlyFee: String,
    onRegeneratePassword: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPasswordVisible by remember { mutableStateOf(true) } // Visible by default for teacher

    val shareText = """
Welcome to Chanakya Classes! 🎓

$studentName has been registered.
${result.batchName} · $timingDisplay

Login details for the MyTuition app:
👤 Username: ${result.username}
🔑 Password: ${result.password}

Monthly fee: ₹$monthlyFee (due by 10th of each month)

— Mr. Rakesh Sharma, Chanakya Classes
    """.trimIndent()

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    fun shareOnWhatsApp() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            `package` = "com.whatsapp"
        }
        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            // Fallback to generic chooser if WhatsApp is not installed
            val chooser = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }, "Share Credentials via")
            context.startActivity(chooser)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top checkmark celebration badge
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF9E6))
                .border(2.dp, Color(0xFF67CF50), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Student Registered! 🎉",
            style = MyTuitionTypography.TitleExtra.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MyTuitionColors.TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$studentName · ${result.batchName}",
            style = MyTuitionTypography.BodyMedium.copy(
                fontSize = 15.sp,
                color = MyTuitionColors.TextSecondary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Credentials Clay Card
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            cardColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header inside card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LOGIN CREDENTIALS",
                        style = MyTuitionTypography.LabelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MyTuitionColors.PrimaryPurple,
                            letterSpacing = 1.sp
                        )
                    )
                    MyTuitionLogo(size = 28.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Username Box
                val boxShape = RoundedCornerShape(14.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(boxShape)
                        .background(Color(0xFFF7F5FC))
                        .border(1.5.dp, Color(0xFFEAE4F8), boxShape)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MyTuitionColors.TextTertiary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = result.username,
                                style = MyTuitionTypography.TitleSmall.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF221A44)
                                )
                            )
                        }

                        IconButton(
                            onClick = { copyToClipboard("Username", result.username) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy Username",
                                tint = MyTuitionColors.PrimaryPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Password Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(boxShape)
                        .background(Color(0xFFF7F5FC))
                        .border(1.5.dp, Color(0xFFEAE4F8), boxShape)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MyTuitionColors.TextTertiary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isPasswordVisible) result.password else "••••••••",
                                style = MyTuitionTypography.TitleSmall.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF221A44)
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = MyTuitionColors.PrimaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { copyToClipboard("Password", result.password) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy Password",
                                    tint = MyTuitionColors.PrimaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Regenerate password button
                Row(
                    modifier = Modifier
                        .clickable { onRegeneratePassword() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Regenerate",
                        tint = MyTuitionColors.PrimaryPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Regenerate another simple password",
                        style = MyTuitionTypography.LabelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MyTuitionColors.PrimaryPurple
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // WhatsApp Share Pill Button
        PillButton(
            text = "📤 Share on WhatsApp",
            onClick = { shareOnWhatsApp() },
            modifier = Modifier.fillMaxWidth(),
            variant = PillButtonVariant.Primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Done Pill Button
        PillButton(
            text = "Done ✓",
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            variant = PillButtonVariant.Outline
        )
    }
}

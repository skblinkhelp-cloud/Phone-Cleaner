package com.sk.phonecleaner_aicleaner.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.util.PreferenceManager

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    var showHiddenFiles by remember { mutableStateOf(preferenceManager.isShowHiddenFiles()) }

    var activeDialog by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // General Section
        SettingsHeader("General")
        SettingsToggleItem(
            icon = Icons.Default.Visibility,
            label = "Show hidden files",
            checked = showHiddenFiles,
            onCheckedChange = { checked ->
                showHiddenFiles = checked
                preferenceManager.setShowHiddenFiles(checked)
                Toast.makeText(context, "Show hidden files: ${if (checked) "On" else "Off"}", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Other Section
        SettingsHeader("Other")
        Surface(
            modifier = Modifier.padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            color = Color.White
        ) {
            Column {
                SettingsActionItem(Icons.Default.Star, "Rate us") {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                        context.startActivity(intent)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsActionItem(Icons.Default.Feedback, "Feedback") {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:sk.blink.help@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Feedback - Phone Cleaner - AI Cleaner")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Support Email: sk.blink.help@gmail.com", Toast.LENGTH_LONG).show()
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsActionItem(Icons.Default.Share, "Share") {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, "Check out Phone Cleaner - AI Cleaner by SK Software: https://play.google.com/store/apps/details?id=${context.packageName}")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share App via")
                    context.startActivity(shareIntent)
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsActionItem(Icons.Default.Policy, "Privacy policy") {
                    activeDialog = "PRIVACY"
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsActionItem(Icons.Default.Assignment, "Terms of services") {
                    activeDialog = "TERMS"
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsActionItem(Icons.Default.Info, "About") {
                    activeDialog = "ABOUT"
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BannerAdView()
        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialogs
    when (activeDialog) {
        "PRIVACY" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Phone Cleaner", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E88E5))
                        Text("Effective Date: September 23, 2026", fontSize = 12.sp, color = Color.Gray)
                        Text("Developer: SK Software", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Contact Email: sk.blink.help@gmail.com", fontSize = 13.sp, color = Color(0xFF1E88E5))
                        Spacer(modifier = Modifier.height(12.dp))

                        val sections = listOf(
                            "1. Information We Access" to "Phone Cleaner may access files, images, videos, audio files, documents, downloads, and other storage information on your device when you use features that require such access. This access is used for storage analysis, file management, duplicate file detection, media management, and cleaning. Phone Cleaner does not access files that are not necessary for the feature you are using.",
                            "2. How Your Files Are Handled" to "Files and media accessed by Phone Cleaner are processed locally on your device for the application's features unless a feature explicitly indicates otherwise. We do not sell your personal files, photos, videos, documents, or other private content.",
                            "3. Personal Information" to "Phone Cleaner does not require you to create an account or provide your name, phone number, address, or other personal information to use the core cleaning features.",
                            "4. Advertising (Google AdMob)" to "Phone Cleaner may display advertisements provided by third-party advertising services, such as Google Mobile Ads (AdMob). Advertising providers may collect device information, advertising IDs, and coarse location data subject to their own privacy policies. We do not sell your personal information to advertisers.",
                            "5. Analytics & Diagnostics" to "The application or its third-party services may process limited technical information necessary to operate, secure, troubleshoot, and improve the application (device type, operating system version, application version, and diagnostic information).",
                            "6. Data Sharing" to "We do not sell or rent your personal information. Information may be processed by third-party service providers only when necessary to provide services such as advertising, diagnostics, security, or application functionality.",
                            "7. Data Security" to "We take reasonable measures to protect information handled by the application. However, no electronic storage or transmission method can be guaranteed to be completely secure.",
                            "8. Data Retention & Deletion" to "Phone Cleaner does not maintain a user account or a server-side personal-data profile for its core cleaning functionality. Information stored locally on your device remains under your control and can be removed by uninstalling the application or deleting the relevant files.",
                            "9. Children's Privacy" to "Phone Cleaner is not specifically directed toward children. We do not knowingly collect personal information from children.",
                            "10. Third-Party Services" to "Third-party services used by the application may have their own privacy policies. Users should review the privacy policies of those services for information about how they handle data.",
                            "11. Changes to This Privacy Policy" to "We may update this Privacy Policy from time to time. Any changes will be posted with an updated effective date.",
                            "12. Contact Us" to "If you have questions about this Privacy Policy, contact SK Software at sk.blink.help@gmail.com.",
                            "13. Your Choices & Permissions" to "You can control application permissions at any time through your Android device settings."
                        )

                        sections.forEach { (heading, body) ->
                            Text(heading, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(body, fontSize = 13.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://skblinkhelp-cloud.github.io/Phone-Cleaner/")
                        )
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "https://skblinkhelp-cloud.github.io/Phone-Cleaner/", Toast.LENGTH_LONG).show()
                        }
                    }) {
                        Text("VIEW ONLINE", color = Color(0xFF1E88E5))
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("CLOSE")
                    }
                }
            )
        }
        "TERMS" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Terms of Services", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Software: 2026 Software", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Developer: SK Software", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "By using Phone Cleaner - AI Cleaner (2026 Software), you grant permission for local storage scanning to identify and clean junk, WhatsApp media, and duplicate files upon your explicit confirmation.",
                            fontSize = 14.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("CLOSE")
                    }
                }
            )
        }
        "ABOUT" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("About Phone Cleaner", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Phone Cleaner - AI Cleaner", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E88E5))
                        Text("Version 1.0.0 (2026 Software)", fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Developer: SK Software", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Support Email: sk.blink.help@gmail.com", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "An AI-powered device optimization suite providing system junk cleaning, RAM boosting, duplicate file removal, WhatsApp media cleanup, photo compression, and app management.",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1E88E5))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingsActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1E88E5))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 18.sp)
    }
}
package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    onContinueClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Skip Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            TextButton(onClick = onSkipClick) {
                Text("Skip", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Hi!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
        Text(
            text = "Let me guide you through your\nfirst cleanup process.",
            fontSize = 20.sp,
            color = Color.Black,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Robot Illustration Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for the robot image
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = Color(0xFF1E88E5)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Permission Info Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF5F5F5)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Robot Icon Placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1E88E5))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "To free up space and organize your storage, allow access to your photos, media, and files.",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CONTINUE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please allow required permission to scan files.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }

    if (showDialog) {
        PermissionDialog(
            onOkClick = {
                showDialog = false
                onContinueClick()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(onOkClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Placeholder
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp).background(Color(0xFF1E88E5), RoundedCornerShape(8.dp)).padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Phone Cleaner", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Gray.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Allow permission", color = Color.White, fontSize = 18.sp, modifier = Modifier.weight(1f))
                            Switch(checked = false, onCheckedChange = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Tip: Find Phone Cleaner and turn it on.",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onOkClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("OK", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    )
}
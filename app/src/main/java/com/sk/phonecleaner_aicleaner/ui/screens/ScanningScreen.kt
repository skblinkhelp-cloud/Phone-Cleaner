package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sk.phonecleaner_aicleaner.model.JunkItem
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.util.FileUtil
import com.sk.phonecleaner_aicleaner.viewmodel.StorageViewModel

@Composable
fun ScanningScreen(
    viewModel: StorageViewModel,
    onBackClick: () -> Unit,
    onCleanFinish: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val isCleaning by viewModel.isCleaning.collectAsStateWithLifecycle()
    val cleanComplete by viewModel.cleanComplete.collectAsStateWithLifecycle()
    val currentFile by viewModel.currentScanningFile.collectAsStateWithLifecycle()
    val junkItems by viewModel.junkItems.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.startScan(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E88E5))
            .statusBarsPadding()
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("Cleaner", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Header / Animation Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            contentAlignment = Alignment.Center
        ) {
            if (cleanComplete) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Task Complete", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isCleaning) {
                Box(
                    modifier = Modifier.size(150.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(80.dp))
                }
            } else {
                RadarAnimation()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isScanning) "Scanning." else "Scan Results",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentFile,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        maxLines = 1
                    )
                }
            }
        }

        // Results / Actions Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (cleanComplete) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${FileUtil.formatSize(viewModel.lastCleanedSize)} cleaned",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${viewModel.lastCleanedCount} items removed",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.resetState()
                                onCleanFinish()
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("DONE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    junkItems.forEach { item ->
                        ScanningResultItem(item)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!isScanning && !isCleaning) {
                        Button(
                            onClick = { viewModel.cleanJunk() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("CLEAN NOW", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    BannerAdView()
                }
            }
        }
    }
}

@Composable
fun RadarAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    Canvas(modifier = Modifier.size(300.dp)) {
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = size.minDimension / 2,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = size.minDimension / 3,
            style = Stroke(width = 2.dp.toPx())
        )
        
        withTransform({
            rotate(rotation, center)
        }) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = center,
                end = center.copy(y = center.y - size.minDimension / 2),
                strokeWidth = 4.dp.toPx()
            )
        }
    }
}

@Composable
fun ScanningResultItem(item: JunkItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        
        if (!item.isScanned) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.LightGray
            )
        } else {
            Text(item.sizeFormatted, fontSize = 16.sp, color = Color.Gray)
        }
    }
}
package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sk.phonecleaner_aicleaner.model.AppProcess
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.viewmodel.BoostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoostScreen(
    viewModel: BoostViewModel,
    onBackClick: () -> Unit,
    onBoostFinish: () -> Unit
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val isBoosting by viewModel.isBoosting.collectAsStateWithLifecycle()
    val boostComplete by viewModel.boostComplete.collectAsStateWithLifecycle()
    val processes by viewModel.processes.collectAsStateWithLifecycle()
    val totalMemory by viewModel.totalMemoryToBoost.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.scanProcesses(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Phone Boost", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD32F2F)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (boostComplete) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Task Complete",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Your device optimization is complete.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                } else if (isBoosting) {
                    BoostingAnimation()
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isScanning) "Analyzing..." else "$totalMemory MB",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Memory can be optimized",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Body
            if (boostComplete) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Successfully boosted ${viewModel.lastBoostedMemory} MB RAM.",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { 
                            viewModel.resetState()
                            onBoostFinish() 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("DONE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (!isBoosting) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Background Processes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(processes) { process ->
                        AppProcessItem(process)
                    }
                }

                Button(
                    onClick = { viewModel.boost(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(28.dp),
                    enabled = processes.isNotEmpty() && !isScanning
                ) {
                    Text("BOOST NOW", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                BannerAdView()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Optimizing RAM...", color = Color.Gray, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun BoostingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "Boost")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rocket"
    )

    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(100.dp)
                .offset(y = offsetY.dp)
        )
    }
}

@Composable
fun AppProcessItem(process: AppProcess) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Text(process.name.take(1), fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(process.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(process.packageName, fontSize = 11.sp, color = Color.Gray)
            }
            Text(process.memoryFormatted, fontWeight = FontWeight.Medium, color = Color(0xFFD32F2F))
        }
    }
}
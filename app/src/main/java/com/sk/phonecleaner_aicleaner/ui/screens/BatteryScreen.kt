package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import com.sk.phonecleaner_aicleaner.viewmodel.BatteryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel,
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val level by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val status by viewModel.batteryStatus.collectAsStateWithLifecycle()
    val temp by viewModel.batteryTemp.collectAsStateWithLifecycle()
    val voltage by viewModel.batteryVoltage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateBatteryInfo(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Battery Manager", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC107).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(60.dp))
                    Text("$level%", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Text(status, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    BatteryInfoRow("Status", status)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    BatteryInfoRow("Temperature", String.format("%.1f°C", temp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    BatteryInfoRow("Voltage", "${voltage}mV")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    BatteryInfoRow("Optimization", "Enabled")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    val intent = Intent(android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("BATTERY SETTINGS", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun BatteryInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}
package com.sk.phonecleaner_aicleaner.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView

@Composable
fun CleanerTab(
    onSettingsClick: () -> Unit,
    onAiClick: () -> Unit,
    onCleanStart: () -> Unit,
    onFeatureClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Professional Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5), Color(0xFF42A5F5))
                    )
                )
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Phone Cleaner",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "AI Powered Optimization",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp)
                    ) {
                        IconButton(onClick = onAiClick) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    // Multi-layered pulsing circle effect
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(10.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            .padding(10.dp)
                            .border(6.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .shadow(15.dp, CircleShape, spotColor = Color.White)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "SYSTEM JUNK",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "2.4 GB",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onCleanStart,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                elevation = ButtonDefaults.buttonElevation(8.dp),
                                shape = RoundedCornerShape(25.dp),
                                modifier = Modifier.height(45.dp).width(120.dp)
                            ) {
                                Text(
                                    "CLEAN",
                                    color = Color(0xFF1E88E5),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Feature Section with Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                "Tools & Optimization",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val features = listOf(
                FeatureItem("Boost", Icons.Default.RocketLaunch, Color(0xFFFF5252), "Speed up device"),
                FeatureItem("AI Clean", Icons.Default.AutoAwesome, Color(0xFF2196F3), "Smart cleaning"),
                FeatureItem("Delete Duplicate", Icons.Default.FileCopy, Color(0xFF4CAF50), "Remove copies"),
                FeatureItem("WA Cleaner", Icons.Default.Chat, Color(0xFF25D366), "Cleanup WhatsApp"),
                FeatureItem("Battery Manager", Icons.Default.BatteryChargingFull, Color(0xFFFFC107), "Power saving"),
                FeatureItem("Photo Compressor", Icons.Default.PhotoSizeSelectLarge, Color(0xFF9C27B0), "Save storage"),
                FeatureItem("App Manager", Icons.Default.Apps, Color(0xFF607D8B), "Uninstall apps")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(features) { feature ->
                    FeatureCard(feature) { onFeatureClick(feature.label) }
                }
            }
        }
    }
}

data class FeatureItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

@Composable
fun FeatureCard(feature: FeatureItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(feature.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = feature.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = feature.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
                Text(
                    text = feature.description,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}
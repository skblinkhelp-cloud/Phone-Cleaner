package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeScreen(
    onFinishWelcome: () -> Unit
) {
    var pageState by remember { mutableIntStateOf(1) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E88E5).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Step $pageState of 2",
                        color = Color(0xFF1E88E5),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                if (pageState == 1) {
                    TextButton(onClick = { onFinishWelcome() }) {
                        Text("Skip", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Content Switcher between Welcome Page 1 and Page 2
            AnimatedContent(
                targetState = pageState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f),
                label = "WelcomePageTransition"
            ) { targetPage ->
                if (targetPage == 1) {
                    WelcomePageContent(
                        title = "AI Smart Cleaner",
                        description = "Instantly detect and eliminate system junk, residual cache, and obsolete files with intelligent AI precision.",
                        heroColor = Color(0xFF1E88E5),
                        features = listOf(
                            WelcomeFeatureItem("AI Junk Detection", "Smart scan for junk and residual cache", Icons.Default.AutoAwesome),
                            WelcomeFeatureItem("Speed Booster", "Free up RAM for maximum performance", Icons.Default.RocketLaunch),
                            WelcomeFeatureItem("Safe Storage Cleaning", "Process files 100% locally on device", Icons.Default.Security)
                        )
                    )
                } else {
                    WelcomePageContent(
                        title = "WhatsApp & Media Manager",
                        description = "Reclaim gigabytes of storage by safely cleaning WhatsApp files, removing duplicate photos, and managing apps.",
                        heroColor = Color(0xFF25D366),
                        features = listOf(
                            WelcomeFeatureItem("WhatsApp Cleanup", "Clear voice notes, videos, and media", Icons.Default.CleaningServices),
                            WelcomeFeatureItem("MD5 Duplicate Finder", "Identify and remove exact duplicate files", Icons.Default.Folder),
                            WelcomeFeatureItem("Photo Compression", "Compress images without losing quality", Icons.Default.CheckCircle)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Indicators Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(width = if (pageState == 1) 24.dp else 10.dp, height = 10.dp)
                        .clip(CircleShape)
                        .background(if (pageState == 1) Color(0xFF1E88E5) else Color.LightGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(width = if (pageState == 2) 24.dp else 10.dp, height = 10.dp)
                        .clip(CircleShape)
                        .background(if (pageState == 2) Color(0xFF25D366) else Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = {
                    if (pageState == 1) {
                        pageState = 2
                    } else {
                        onFinishWelcome()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (pageState == 1) Color(0xFF1E88E5) else Color(0xFF25D366)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (pageState == 1) "NEXT" else "GET STARTED",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class WelcomeFeatureItem(val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun WelcomePageContent(
    title: String,
    description: String,
    heroColor: Color,
    features: List<WelcomeFeatureItem>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Card with Logo Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(heroColor.copy(alpha = 0.15f), Color.White)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "AI Cleaner",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = heroColor
                        )
                        Text(
                            text = "Optimization Suite",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Feature List
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            features.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(heroColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icon, contentDescription = null, tint = heroColor, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.subtitle, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
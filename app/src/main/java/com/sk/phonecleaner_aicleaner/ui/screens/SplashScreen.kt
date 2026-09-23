package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "SplashLogoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF1565C0),
                        Color(0xFF1E88E5)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Animated Live Style Logo Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                // Live Outer Glow Ring
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = glowAlpha * 0.2f))
                        .border(2.dp, Color.White.copy(alpha = glowAlpha), CircleShape)
                )

                // Secondary Inner Ring
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .shadow(16.dp, CircleShape, spotColor = Color.White)
                )

                // Logo Image
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Title
            Text(
                text = "Phone Cleaner",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI Powered Optimization",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            // Progress Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(180.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Initializing AI Cleaner...",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sk.phonecleaner_aicleaner.viewmodel.StorageViewModel
import kotlinx.coroutines.delay

@Composable
fun CleaningScreen(
    viewModel: StorageViewModel,
    onFinish: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.cleanJunk()
        delay(3000) // Extra delay for the animation
        onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E88E5)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "Cleaning")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "Scale"
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(100.dp * scale)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Cleaning Device...",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LinearProgressIndicator(
            modifier = Modifier.width(200.dp).height(8.dp),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}
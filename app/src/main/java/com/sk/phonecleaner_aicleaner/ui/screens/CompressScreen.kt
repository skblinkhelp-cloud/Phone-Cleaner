package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sk.phonecleaner_aicleaner.util.FileUtil
import com.sk.phonecleaner_aicleaner.viewmodel.CompressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressScreen(
    viewModel: CompressViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val originalSize by viewModel.originalSize.collectAsStateWithLifecycle()
    val compressedSize by viewModel.compressedSize.collectAsStateWithLifecycle()
    
    var selectedUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Photo Compressor", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF9C27B0)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = Color(0xFF9C27B0))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Compressing image...", color = Color.Gray)
            } else if (compressedSize > 0) {
                Icon(Icons.Default.Done, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("Compression Complete!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Original: ${FileUtil.formatSize(originalSize)}", color = Color.Gray)
                Text("Compressed: ${FileUtil.formatSize(compressedSize)}", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("CHOOSE ANOTHER")
                }
            } else {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (selectedUri == null) "Select Photo to Compress" else "Photo Selected",
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
                
                if (selectedUri != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            selectedUri?.let { viewModel.compressImage(context, it, 50) { file ->
                                android.widget.Toast.makeText(context, "Saved to ${file.absolutePath}", android.widget.Toast.LENGTH_LONG).show()
                            } }
                        },
                        modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("COMPRESS NOW (50% Quality)", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(if (selectedUri == null) "CHOOSE PHOTO" else "CHANGE PHOTO", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
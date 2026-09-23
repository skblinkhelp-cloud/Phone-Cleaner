package com.sk.phonecleaner_aicleaner.ui.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.util.FileUtil
import com.sk.phonecleaner_aicleaner.viewmodel.FilesViewModel
import com.sk.phonecleaner_aicleaner.viewmodel.StorageViewModel
import java.io.File

@Composable
fun FilesTab(
    storageViewModel: StorageViewModel,
    filesViewModel: FilesViewModel,
    onSettingsClick: () -> Unit
) {
    val stats by storageViewModel.storageStats.collectAsStateWithLifecycle()
    val isScanning by filesViewModel.isScanning.collectAsStateWithLifecycle()
    val categoryFiles by filesViewModel.categoryFiles.collectAsStateWithLifecycle()
    val selectedFiles by filesViewModel.selectedFiles.collectAsStateWithLifecycle()
    
    var currentCategory by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    BackHandler(enabled = currentCategory != null) {
        currentCategory = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        if (currentCategory == null) {
            // Dashboard Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Files", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Internal Storage Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E88E5)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Internal storage", color = Color.White, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stats.usedGB,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " | ${stats.totalGB}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { stats.usedPercentage },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Categories Grid
                val categories = listOf(
                    FileCategory("Images", FileUtil.formatSize(stats.imageBytes), Icons.Default.Image, Color(0xFFE91E63)),
                    FileCategory("Video", FileUtil.formatSize(stats.videoBytes), Icons.Default.VideoLibrary, Color(0xFF2196F3)),
                    FileCategory("Audio", FileUtil.formatSize(stats.audioBytes), Icons.Default.MusicNote, Color(0xFFFF5252)),
                    FileCategory("Document", FileUtil.formatSize(stats.documentBytes), Icons.Default.Description, Color(0xFF1E88E5)),
                    FileCategory("Apk", FileUtil.formatSize(stats.apkBytes), Icons.Default.Android, Color(0xFF4CAF50)),
                    FileCategory("Download", FileUtil.formatSize(stats.downloadBytes), Icons.Default.Download, Color(0xFF9C27B0))
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        CategoryItem(category) {
                            currentCategory = category.name
                            filesViewModel.scanCategory(category.name)
                        }
                    }
                }
            }
        } else {
            // Category Detail View
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentCategory = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    Text(
                        text = currentCategory!!,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!isScanning && categoryFiles.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Select All",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Checkbox(
                                checked = selectedFiles.size == categoryFiles.size,
                                onCheckedChange = { 
                                    if (selectedFiles.size == categoryFiles.size) {
                                        filesViewModel.deselectAll()
                                    } else {
                                        filesViewModel.selectAll()
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E88E5))
                            )
                        }
                    }
                }

                if (isScanning) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1E88E5))
                    }
                } else if (categoryFiles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No files found", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(categoryFiles) { file ->
                            FileMediaItem(
                                file = file,
                                isSelected = selectedFiles.contains(file),
                                onToggle = { filesViewModel.toggleFileSelection(file) }
                            )
                        }
                    }
                    
                    if (selectedFiles.isNotEmpty()) {
                        Button(
                            onClick = {
                                filesViewModel.deleteSelected { deleted ->
                                    android.widget.Toast.makeText(context, "Deleted ${FileUtil.formatSize(deleted)}", android.widget.Toast.LENGTH_SHORT).show()
                                    storageViewModel.updateStorageStats()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("DELETE SELECTED (${selectedFiles.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileMediaItem(file: File, isSelected: Boolean, onToggle: () -> Unit) {
    val fileExtension = remember(file.name) { file.extension.lowercase() }
    val isImage = remember(fileExtension) { fileExtension in listOf("jpg", "jpeg", "png", "webp") }
    val isVideo = remember(fileExtension) { fileExtension in listOf("mp4", "mkv", "avi") }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable { onToggle() }
    ) {
        if (isImage || isVideo) {
            AsyncImage(
                model = file.absolutePath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )
            if (isVideo) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(32.dp)
                )
            }
        } else {
            val icon = when (fileExtension) {
                "pdf" -> Icons.Default.Description
                "mp3", "wav", "m4a" -> Icons.Default.MusicNote
                "apk" -> Icons.Default.Android
                else -> Icons.Default.InsertDriveFile
            }
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.align(Alignment.Center).size(32.dp)
            )
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            modifier = Modifier.align(Alignment.TopEnd),
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
        )
    }
}

data class FileCategory(val name: String, val size: String, val icon: ImageVector, val color: Color)

@Composable
fun CategoryItem(category: FileCategory, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(category.size, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
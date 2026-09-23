package com.sk.phonecleaner_aicleaner.ui.screens

import android.app.Activity
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sk.phonecleaner_aicleaner.util.AdManager
import com.sk.phonecleaner_aicleaner.util.FileUtil
import com.sk.phonecleaner_aicleaner.viewmodel.WaCleanerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaCleanerScreen(
    viewModel: WaCleanerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val stats by viewModel.waStats.collectAsStateWithLifecycle()
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = selectedCategory != null) {
        selectedCategory = null
    }

    LaunchedEffect(Unit) {
        viewModel.scanWhatsApp()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(selectedCategory ?: "WA Cleaner", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedCategory != null) selectedCategory = null else onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF25D366)
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
            if (isScanning) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF25D366))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Scanning WhatsApp media...", color = Color.Gray)
                    }
                }
            } else if (selectedCategory != null) {
                val categoryFiles = viewModel.getFilesForCategory(selectedCategory!!)
                MediaGrid(
                    files = categoryFiles,
                    selectedFiles = selectedFiles,
                    onToggleSelect = { viewModel.toggleFileSelection(it) },
                    onDeleteSelected = {
                        viewModel.deleteSelectedFiles { deleted ->
                            android.widget.Toast.makeText(context, "Cleaned ${FileUtil.formatSize(deleted)}", android.widget.Toast.LENGTH_SHORT).show()
                            selectedCategory = null
                            if (context is Activity) {
                                AdManager.showInterstitial(context) {}
                            }
                        }
                    }
                )
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (stats.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No WhatsApp media found", color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(stats.toList()) { (name, size) ->
                                WaCategoryCard(name, FileUtil.formatSize(size), onClick = {
                                    selectedCategory = name
                                }) {
                                    viewModel.deleteCategory(name) { deleted ->
                                        android.widget.Toast.makeText(context, "Cleaned ${FileUtil.formatSize(deleted)} from $name", android.widget.Toast.LENGTH_SHORT).show()
                                        if (context is Activity) {
                                            AdManager.showInterstitial(context) {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            com.sk.phonecleaner_aicleaner.ui.components.BannerAdView()
        }
    }
}

@Composable
fun MediaGrid(
    files: List<File>, 
    selectedFiles: Set<File>,
    onToggleSelect: (File) -> Unit,
    onDeleteSelected: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(files) { file ->
                MediaItem(
                    file = file,
                    isSelected = selectedFiles.contains(file),
                    onToggle = { onToggleSelect(file) }
                )
            }
        }
        
        if (selectedFiles.isNotEmpty()) {
            Button(
                onClick = onDeleteSelected,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("DELETE SELECTED (${selectedFiles.size})", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MediaItem(file: File, isSelected: Boolean, onToggle: () -> Unit) {
    val fileExtension = remember(file.name) { file.extension.lowercase() }
    val isImage = remember(fileExtension) {
        fileExtension in listOf("jpg", "jpeg", "png", "webp")
    }
    val isVideo = remember(fileExtension) {
        fileExtension in listOf("mp4", "mkv", "avi")
    }

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
                "mp3", "wav", "m4a", "opus" -> Icons.Default.MusicNote
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

@Composable
fun WaCategoryCard(name: String, size: String, onClick: () -> Unit, onDelete: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(size, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            IconButton(onClick = { showConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete all files in $name? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onDelete()
                }) {
                    Text("DELETE", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
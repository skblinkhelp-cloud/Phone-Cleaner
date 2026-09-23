package com.sk.phonecleaner_aicleaner.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.sk.phonecleaner_aicleaner.model.DuplicateGroup
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.util.AdManager
import com.sk.phonecleaner_aicleaner.util.FileUtil
import com.sk.phonecleaner_aicleaner.viewmodel.DuplicateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateScreen(
    viewModel: DuplicateViewModel,
    onBackClick: () -> Unit
) {
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val groups by viewModel.duplicateGroups.collectAsStateWithLifecycle()
    val progress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.scanDuplicates(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Delete Duplicate", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4CAF50)
                )
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isScanning && groups.isNotEmpty()) {
                    val selectedCount = groups.sumOf { g -> g.files.count { it.isSelected } }
                    if (selectedCount > 0) {
                        Button(
                            onClick = {
                                viewModel.deleteSelected { saved ->
                                    Toast.makeText(context, "Cleaned ${FileUtil.formatSize(saved)}", Toast.LENGTH_LONG).show()
                                    if (context is Activity) {
                                        AdManager.showInterstitial(context) {}
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("DELETE SELECTED ($selectedCount)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                BannerAdView()
            }
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
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(progress, color = Color.Gray)
                    }
                }
            } else if (groups.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No duplicates found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(groups) { groupIndex, group ->
                        DuplicateGroupItem(group, groupIndex, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DuplicateGroupItem(group: DuplicateGroup, groupIndex: Int, viewModel: DuplicateViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Group ${groupIndex + 1} (${group.sizeFormatted})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(8.dp))
            group.files.forEachIndexed { fileIndex, dupFile ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = dupFile.isSelected,
                        onCheckedChange = { viewModel.toggleSelection(groupIndex, fileIndex) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
                    )
                    
                    val fileExtension = remember(dupFile.file.name) {
                        dupFile.file.extension.lowercase()
                    }
                    
                    val isImage = remember(fileExtension) {
                        fileExtension in listOf("jpg", "jpeg", "png", "webp", "gif")
                    }
                    val isVideo = remember(fileExtension) {
                        fileExtension in listOf("mp4", "mkv", "avi", "mov")
                    }

                    if (isImage || isVideo) {
                        AsyncImage(
                            model = dupFile.file.absolutePath,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = android.R.drawable.ic_menu_report_image)
                        )
                    } else {
                        val icon = when (fileExtension) {
                            "pdf" -> Icons.Default.Description
                            "mp3", "wav", "m4a" -> Icons.Default.MusicNote
                            "apk" -> Icons.Default.Android
                            "zip", "rar" -> Icons.Default.FolderZip
                            else -> Icons.Default.FileCopy
                        }
                        Icon(
                            icon, 
                            contentDescription = null, 
                            tint = Color.LightGray, 
                            modifier = Modifier.size(50.dp).padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = dupFile.file.name,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = dupFile.file.parent ?: "",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
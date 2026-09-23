package com.sk.phonecleaner_aicleaner.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sk.phonecleaner_aicleaner.model.AppInfo
import com.sk.phonecleaner_aicleaner.viewmodel.AppManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    viewModel: AppManagerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val selectedApps = apps.filter { it.isSelected }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshApps(context)
    }

    LaunchedEffect(Unit) {
        viewModel.scanApps(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("App Manager", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF607D8B)
                )
            )
        },
        bottomBar = {
            if (selectedApps.isNotEmpty()) {
                Button(
                    onClick = {
                        // Launch uninstall for the first selected app
                        // Once finished and returned, the list refreshes and they can do the next
                        val app = selectedApps.first()
                        val intent = Intent(Intent.ACTION_DELETE).apply {
                            data = Uri.parse("package:${app.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                        if (selectedApps.size > 1) {
                            android.widget.Toast.makeText(context, "Uninstalls will proceed one by one", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("UNINSTALL SELECTED (${selectedApps.size})", fontWeight = FontWeight.Bold)
                }
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
                        CircularProgressIndicator(color = Color(0xFF607D8B))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analyzing installed apps...", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps) { app ->
                        AppItem(
                            app = app,
                            onToggleSelect = { viewModel.toggleAppSelection(app.packageName) },
                            onUninstall = {
                                val intent = Intent(Intent.ACTION_DELETE).apply {
                                    data = Uri.parse("package:${app.packageName}")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(app: AppInfo, onToggleSelect: () -> Unit, onUninstall: () -> Unit) {
    val context = LocalContext.current
    val appIcon = remember(app.packageName) {
        try {
            context.packageManager.getApplicationIcon(app.packageName)
        } catch (e: Exception) {
            null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = app.isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF607D8B))
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = appIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(app.packageName, fontSize = 12.sp, color = Color.Gray)
                Text(app.sizeFormatted, fontSize = 12.sp, color = Color(0xFF607D8B), fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onUninstall) {
                Icon(Icons.Default.Delete, contentDescription = "Uninstall", tint = Color(0xFFD32F2F))
            }
        }
    }
}
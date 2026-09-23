package com.sk.phonecleaner_aicleaner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sk.phonecleaner_aicleaner.ui.components.BannerAdView
import com.sk.phonecleaner_aicleaner.ui.tabs.CleanerTab
import com.sk.phonecleaner_aicleaner.ui.tabs.FilesTab
import com.sk.phonecleaner_aicleaner.viewmodel.FilesViewModel
import com.sk.phonecleaner_aicleaner.viewmodel.StorageViewModel

@Composable
fun MainScreen(
    storageViewModel: StorageViewModel,
    filesViewModel: FilesViewModel,
    onSettingsClick: () -> Unit,
    onAiClick: () -> Unit,
    onCleanStart: () -> Unit,
    onFeatureClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                BannerAdView(modifier = Modifier.fillMaxWidth())
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.CleaningServices, contentDescription = null) },
                        label = { Text("Cleaner") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E88E5),
                            selectedTextColor = Color(0xFF1E88E5),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                        label = { Text("Files") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E88E5),
                            selectedTextColor = Color(0xFF1E88E5),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> CleanerTab(onSettingsClick, onAiClick, onCleanStart, onFeatureClick)
                1 -> FilesTab(storageViewModel, filesViewModel, onSettingsClick)
            }
        }
    }
}
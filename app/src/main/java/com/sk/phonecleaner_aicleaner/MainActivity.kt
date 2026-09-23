package com.sk.phonecleaner_aicleaner

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sk.phonecleaner_aicleaner.ui.navigation.Screen
import com.sk.phonecleaner_aicleaner.ui.screens.*
import com.sk.phonecleaner_aicleaner.ui.theme.PhoneCleanerAICleanerTheme
import com.sk.phonecleaner_aicleaner.util.AdManager
import com.sk.phonecleaner_aicleaner.util.PreferenceManager
import com.sk.phonecleaner_aicleaner.viewmodel.*

class MainActivity : ComponentActivity() {
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        AdManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            PhoneCleanerAICleanerTheme {
                val navController = rememberNavController()
                
                // Initialize ViewModels
                val storageViewModel: StorageViewModel = viewModel()
                val boostViewModel: BoostViewModel = viewModel()
                val duplicateViewModel: DuplicateViewModel = viewModel()
                val waCleanerViewModel: WaCleanerViewModel = viewModel()
                val batteryViewModel: BatteryViewModel = viewModel()
                val compressViewModel: CompressViewModel = viewModel()
                val appManagerViewModel: AppManagerViewModel = viewModel()
                val filesViewModel: FilesViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                if (preferenceManager.isPrivacyAccepted()) {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Welcome.route) {
                                        popUpTo(Screen.Splash.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Welcome.route) {
                        WelcomeScreen(
                            onFinishWelcome = {
                                navController.navigate(Screen.Permission.route) {
                                    popUpTo(Screen.Welcome.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Permission.route) {
                        PermissionScreen(
                            onContinueClick = { 
                                preferenceManager.setPrivacyAccepted(true)
                                requestStoragePermission()
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Permission.route) { inclusive = true }
                                }
                            },
                            onSkipClick = { 
                                preferenceManager.setPrivacyAccepted(true)
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Permission.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Main.route) {
                        MainScreen(
                            storageViewModel = storageViewModel,
                            filesViewModel = filesViewModel,
                            onSettingsClick = { navController.navigate(Screen.Settings.route) },
                            onAiClick = { navController.navigate(Screen.Scanning.route) },
                            onCleanStart = { navController.navigate(Screen.Scanning.route) },
                            onFeatureClick = { label ->
                                Log.d("MainActivity", "Feature clicked: $label")
                                when (label) {
                                    "Boost" -> navController.navigate(Screen.Boost.route)
                                    "AI Clean" -> navController.navigate(Screen.Scanning.route)
                                    "Delete Duplicate" -> navController.navigate(Screen.Duplicate.route)
                                    "WA Cleaner" -> navController.navigate(Screen.WaCleaner.route)
                                    "Battery Manager" -> navController.navigate(Screen.Battery.route)
                                    "Photo Compressor" -> navController.navigate(Screen.Compress.route)
                                    "App Manager" -> navController.navigate(Screen.Apps.route)
                                    else -> Toast.makeText(this@MainActivity, "Feature $label coming soon!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    composable(Screen.Scanning.route) {
                        ScanningScreen(
                            viewModel = storageViewModel,
                            onBackClick = { navController.popBackStack() },
                            onCleanFinish = {
                                AdManager.showInterstitial(this@MainActivity) {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Cleaning.route) {
                        CleaningScreen(
                            viewModel = storageViewModel,
                            onFinish = {
                                AdManager.showInterstitial(this@MainActivity) {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Boost.route) {
                        BoostScreen(
                            viewModel = boostViewModel,
                            onBackClick = { navController.popBackStack() },
                            onBoostFinish = {
                                AdManager.showInterstitial(this@MainActivity) {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Duplicate.route) {
                        DuplicateScreen(
                            viewModel = duplicateViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.WaCleaner.route) {
                        WaCleanerScreen(
                            viewModel = waCleanerViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Battery.route) {
                        BatteryScreen(
                            viewModel = batteryViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Compress.route) {
                        CompressScreen(
                            viewModel = compressViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Apps.route) {
                        AppsScreen(
                            viewModel = appManagerViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }
    }
}
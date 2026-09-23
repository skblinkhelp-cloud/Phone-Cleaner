package com.sk.phonecleaner_aicleaner.viewmodel

import android.content.Context
import android.os.Environment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.phonecleaner_aicleaner.model.JunkItem
import com.sk.phonecleaner_aicleaner.model.StorageStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class StorageViewModel : ViewModel() {
    private val _storageStats = MutableStateFlow(StorageStats())
    val storageStats: StateFlow<StorageStats> = _storageStats.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isCleaning = MutableStateFlow(false)
    val isCleaning: StateFlow<Boolean> = _isCleaning.asStateFlow()

    private val _cleanComplete = MutableStateFlow(false)
    val cleanComplete: StateFlow<Boolean> = _cleanComplete.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _currentScanningFile = MutableStateFlow("")
    val currentScanningFile: StateFlow<String> = _currentScanningFile.asStateFlow()

    private val _junkItems = MutableStateFlow<List<JunkItem>>(emptyList())
    val junkItems: StateFlow<List<JunkItem>> = _junkItems.asStateFlow()

    private val foundJunkFiles = mutableListOf<File>()
    private var _lastCleanedSize: Long = 0
    val lastCleanedSize: Long get() = _lastCleanedSize
    
    private var _lastCleanedCount: Int = 0
    val lastCleanedCount: Int get() = _lastCleanedCount

    init {
        updateStorageStats()
        setupInitialJunkItems()
    }

    private fun setupInitialJunkItems() {
        _junkItems.value = listOf(
            JunkItem("Residuals", 0, Icons.Default.Description, Color(0xFF9C27B0)),
            JunkItem("Obsolete APKs", 0, Icons.Default.FolderZip, Color(0xFF673AB7)),
            JunkItem("Ad Junk", 0, Icons.Default.Campaign, Color(0xFF2196F3)),
            JunkItem("System Cache", 0, Icons.Default.Delete, Color(0xFFE91E63))
        )
    }

    fun updateStorageStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val root = Environment.getExternalStorageDirectory()
            val totalBytes = root.totalSpace
            val freeBytes = root.freeSpace
            val usedBytes = totalBytes - freeBytes

            val stats = StorageStats(totalBytes = totalBytes, usedBytes = usedBytes)
            _storageStats.value = stats

            scanForCategories(root)
        }
    }

    private suspend fun scanForCategories(dir: File) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                if (!file.name.startsWith(".")) {
                    scanForCategories(file)
                }
            } else {
                val size = file.length()
                val name = file.name.lowercase()
                
                val currentStats = _storageStats.value
                when {
                    isImage(name) -> _storageStats.value = currentStats.copy(imageBytes = currentStats.imageBytes + size)
                    isVideo(name) -> _storageStats.value = currentStats.copy(videoBytes = currentStats.videoBytes + size)
                    isAudio(name) -> _storageStats.value = currentStats.copy(audioBytes = currentStats.audioBytes + size)
                    isDocument(name) -> _storageStats.value = currentStats.copy(documentBytes = currentStats.documentBytes + size)
                    name.endsWith(".apk") -> _storageStats.value = currentStats.copy(apkBytes = currentStats.apkBytes + size)
                    dir.parent?.lowercase()?.contains("download") == true -> _storageStats.value = currentStats.copy(downloadBytes = currentStats.downloadBytes + size)
                }
            }
        }
    }

    private fun isImage(name: String) = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
    private fun isVideo(name: String) = name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi")
    private fun isAudio(name: String) = name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".ogg") || name.endsWith(".m4a")
    private fun isDocument(name: String) = name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".txt")

    fun startScan(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _cleanComplete.value = false
            _scanProgress.value = 0f
            foundJunkFiles.clear()
            setupInitialJunkItems()
            
            scanForSystemJunk(context.cacheDir)
            context.externalCacheDir?.let { scanForSystemJunk(it) }

            val root = Environment.getExternalStorageDirectory()
            scanForSystemJunk(root)
            
            _isScanning.value = false
            _scanProgress.value = 1f
            _currentScanningFile.value = "System Junk Scan Completed"
        }
    }

    private suspend fun scanForSystemJunk(dir: File) {
        val files = dir.listFiles() ?: return
        for ((index, file) in files.withIndex()) {
            _currentScanningFile.value = file.name
            if (index % 50 == 0) delay(1)
            
            if (file.isDirectory) {
                val name = file.name.lowercase()
                if (name != "whatsapp" && name != "dcim" && name != "pictures" && name != "download" && name != "documents" && name != "movies" && name != "music") {
                    scanForSystemJunk(file)
                }
            } else {
                categorizeAsSystemJunk(file)
            }
        }
    }

    private fun categorizeAsSystemJunk(file: File) {
        val size = file.length()
        val name = file.name.lowercase()
        val path = file.absolutePath.lowercase()
        
        var isJunk = false
        var categoryName = ""

        when {
            name.endsWith(".apk") -> {
                categoryName = "Obsolete APKs"
                isJunk = true
            }
            path.contains("cache") || path.contains("temp") || name.endsWith(".tmp") -> {
                categoryName = "System Cache"
                isJunk = true
            }
            path.contains("ad") || path.contains("advert") -> {
                categoryName = "Ad Junk"
                isJunk = true
            }
            path.contains(".log") || path.contains("residual") || name == ".nomedia" -> {
                categoryName = "Residuals"
                isJunk = true
            }
        }
        
        if (isJunk) {
            foundJunkFiles.add(file)
            val currentItems = _junkItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.name == categoryName }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(
                    sizeBytes = currentItems[index].sizeBytes + size,
                    isScanned = true
                )
                _junkItems.value = currentItems
            }
        }
    }

    fun cleanJunk() {
        viewModelScope.launch(Dispatchers.IO) {
            _isCleaning.value = true
            _lastCleanedSize = 0
            _lastCleanedCount = 0
            
            val iterator = foundJunkFiles.iterator()
            while (iterator.hasNext()) {
                val file = iterator.next()
                val size = file.length()
                try {
                    if (file.exists() && file.delete()) {
                        _lastCleanedSize += size
                        _lastCleanedCount++
                    }
                } catch (e: Exception) {}
                iterator.remove()
            }
            
            delay(2000)
            setupInitialJunkItems()
            updateStorageStats()
            _isCleaning.value = false
            _cleanComplete.value = true
        }
    }

    fun resetState() {
        _cleanComplete.value = false
    }
}
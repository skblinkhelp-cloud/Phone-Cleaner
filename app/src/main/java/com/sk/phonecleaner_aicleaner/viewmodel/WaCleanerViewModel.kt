package com.sk.phonecleaner_aicleaner.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class WaCleanerViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _waStats = MutableStateFlow<Map<String, Long>>(emptyMap())
    val waStats: StateFlow<Map<String, Long>> = _waStats.asStateFlow()

    private val waMediaFiles = mutableMapOf<String, MutableList<File>>()
    private val _selectedFiles = MutableStateFlow<Set<File>>(emptySet())
    val selectedFiles: StateFlow<Set<File>> = _selectedFiles.asStateFlow()

    fun getFilesForCategory(category: String): List<File> {
        return waMediaFiles[category] ?: emptyList()
    }

    fun scanWhatsApp() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            waMediaFiles.clear()
            _selectedFiles.value = emptySet()
            
            val modernPath = File(Environment.getExternalStorageDirectory(), "Android/media/com.whatsapp/WhatsApp/Media")
            val legacyPath = File(Environment.getExternalStorageDirectory(), "WhatsApp/Media")
            
            val mediaDir = if (modernPath.exists()) modernPath else legacyPath
            
            val stats = mutableMapOf<String, Long>()
            if (mediaDir.exists()) {
                val subDirs = mediaDir.listFiles() ?: emptyArray()
                for (dir in subDirs) {
                    if (dir.isDirectory) {
                        val category = dir.name.replace("WhatsApp ", "")
                        val files = mutableListOf<File>()
                        val size = getFolderSizeAndFiles(dir, files)
                        if (size > 0) {
                            stats[category] = size
                            waMediaFiles[category] = files
                        }
                    }
                }
            }
            
            _waStats.value = stats
            _isScanning.value = false
        }
    }

    private fun getFolderSizeAndFiles(dir: File, list: MutableList<File>): Long {
        var size: Long = 0
        val files = dir.listFiles() ?: return 0
        for (file in files) {
            if (file.isDirectory) {
                size += getFolderSizeAndFiles(file, list)
            } else {
                size += file.length()
                list.add(file)
            }
        }
        return size
    }

    fun toggleFileSelection(file: File) {
        val currentSet = _selectedFiles.value.toMutableSet()
        if (currentSet.contains(file)) {
            currentSet.remove(file)
        } else {
            currentSet.add(file)
        }
        _selectedFiles.value = currentSet
    }

    fun deleteSelectedFiles(onFinish: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var totalDeleted: Long = 0
            val filesToDelete = _selectedFiles.value
            
            for (file in filesToDelete) {
                val size = file.length()
                if (file.exists() && file.delete()) {
                    totalDeleted += size
                }
            }
            
            _selectedFiles.value = emptySet()
            // Re-scan to update categories
            scanWhatsApp()

            viewModelScope.launch(Dispatchers.Main) {
                onFinish(totalDeleted)
            }
        }
    }

    fun deleteCategory(category: String, onFinish: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var totalDeleted: Long = 0
            val files = waMediaFiles[category] ?: return@launch
            
            for (file in files) {
                val size = file.length()
                if (file.exists() && file.delete()) {
                    totalDeleted += size
                }
            }
            
            val currentStats = _waStats.value.toMutableMap()
            currentStats.remove(category)
            _waStats.value = currentStats
            waMediaFiles.remove(category)

            viewModelScope.launch(Dispatchers.Main) {
                onFinish(totalDeleted)
            }
        }
    }
}
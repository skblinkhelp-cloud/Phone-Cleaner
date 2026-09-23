package com.sk.phonecleaner_aicleaner.viewmodel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FilesViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _categoryFiles = MutableStateFlow<List<File>>(emptyList())
    val categoryFiles: StateFlow<List<File>> = _categoryFiles.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<File>>(emptySet())
    val selectedFiles: StateFlow<Set<File>> = _selectedFiles.asStateFlow()

    fun scanCategory(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _selectedFiles.value = emptySet()
            val root = Environment.getExternalStorageDirectory()
            val foundFiles = mutableListOf<File>()
            
            scanDirForCategory(root, categoryName, foundFiles)
            
            _categoryFiles.value = foundFiles.sortedByDescending { it.lastModified() }
            _isScanning.value = false
        }
    }

    private fun scanDirForCategory(dir: File, category: String, foundFiles: MutableList<File>) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                if (!file.name.startsWith(".") && file.name.lowercase() != "android") {
                    scanDirForCategory(file, category, foundFiles)
                }
            } else {
                val name = file.name.lowercase()
                when (category) {
                    "Images" -> if (isImage(name)) foundFiles.add(file)
                    "Video" -> if (isVideo(name)) foundFiles.add(file)
                    "Audio" -> if (isAudio(name)) foundFiles.add(file)
                    "Document" -> if (isDocument(name)) foundFiles.add(file)
                    "Apk" -> if (name.endsWith(".apk")) foundFiles.add(file)
                    "Download" -> if (file.absolutePath.lowercase().contains("download")) foundFiles.add(file)
                }
            }
        }
    }

    private fun isImage(name: String) = name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
    private fun isVideo(name: String) = name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi")
    private fun isAudio(name: String) = name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".ogg") || name.endsWith(".m4a")
    private fun isDocument(name: String) = name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".txt")

    fun toggleFileSelection(file: File) {
        val currentSet = _selectedFiles.value.toMutableSet()
        if (currentSet.contains(file)) {
            currentSet.remove(file)
        } else {
            currentSet.add(file)
        }
        _selectedFiles.value = currentSet
    }

    fun selectAll() {
        _selectedFiles.value = _categoryFiles.value.toSet()
    }

    fun deselectAll() {
        _selectedFiles.value = emptySet()
    }

    fun deleteSelected(onFinish: (Long) -> Unit) {
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
            val currentList = _categoryFiles.value.toMutableList()
            currentList.removeAll(filesToDelete)
            _categoryFiles.value = currentList

            viewModelScope.launch(Dispatchers.Main) {
                onFinish(totalDeleted)
            }
        }
    }
}
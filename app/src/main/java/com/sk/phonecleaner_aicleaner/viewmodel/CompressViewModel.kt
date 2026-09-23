package com.sk.phonecleaner_aicleaner.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CompressViewModel : ViewModel() {
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _originalSize = MutableStateFlow<Long>(0)
    val originalSize: StateFlow<Long> = _originalSize.asStateFlow()

    private val _compressedSize = MutableStateFlow<Long>(0)
    val compressedSize: StateFlow<Long> = _compressedSize.asStateFlow()

    fun compressImage(context: Context, uri: Uri, quality: Int, onFinish: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isProcessing.value = true
            
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap != null) {
                _originalSize.value = getFileSize(context, uri)
                
                val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Compressed")
                if (!outputDir.exists()) outputDir.mkdirs()
                
                val outputFile = File(outputDir, "compressed_${System.currentTimeMillis()}.jpg")
                val out = FileOutputStream(outputFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                out.flush()
                out.close()
                
                _compressedSize.value = outputFile.length()
                
                viewModelScope.launch(Dispatchers.Main) {
                    onFinish(outputFile)
                }
            }
            
            _isProcessing.value = false
        }
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        return context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { 
            it.length 
        } ?: 0
    }
}
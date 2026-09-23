package com.sk.phonecleaner_aicleaner.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.phonecleaner_aicleaner.model.AppProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoostViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _boostComplete = MutableStateFlow(false)
    val boostComplete: StateFlow<Boolean> = _boostComplete.asStateFlow()

    private val _processes = MutableStateFlow<List<AppProcess>>(emptyList())
    val processes: StateFlow<List<AppProcess>> = _processes.asStateFlow()

    private val _totalMemoryToBoost = MutableStateFlow(0)
    val totalMemoryToBoost: StateFlow<Int> = _totalMemoryToBoost.asStateFlow()

    private var _lastBoostedMemory = 0
    val lastBoostedMemory: Int get() = _lastBoostedMemory

    fun scanProcesses(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _boostComplete.value = false
            delay(2000)
            
            val mockProcesses = listOf(
                AppProcess("Social Media App", "com.social.app", 150),
                AppProcess("Video Streamer", "com.video.stream", 280),
                AppProcess("Web Browser", "com.browser.web", 320),
                AppProcess("Gaming Center", "com.game.center", 450),
                AppProcess("Mail Client", "com.mail.client", 85),
                AppProcess("Chat Messenger", "com.chat.msg", 120),
                AppProcess("Music Player", "com.music.play", 95)
            )
            
            _processes.value = mockProcesses
            _totalMemoryToBoost.value = mockProcesses.sumOf { it.memoryUsageMb }
            _isScanning.value = false
        }
    }

    fun boost(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isBoosting.value = true
            _lastBoostedMemory = _totalMemoryToBoost.value
            
            delay(3000) 
            
            _processes.value = emptyList()
            _totalMemoryToBoost.value = 0
            _isBoosting.value = false
            _boostComplete.value = true
        }
    }

    fun resetState() {
        _boostComplete.value = false
    }
}
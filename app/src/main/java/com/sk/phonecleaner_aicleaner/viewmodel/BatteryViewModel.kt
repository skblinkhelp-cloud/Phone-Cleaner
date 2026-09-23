package com.sk.phonecleaner_aicleaner.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatteryViewModel : ViewModel() {
    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _batteryStatus = MutableStateFlow("Unknown")
    val batteryStatus: StateFlow<String> = _batteryStatus.asStateFlow()

    private val _batteryTemp = MutableStateFlow(0f)
    val batteryTemp: StateFlow<Float> = _batteryTemp.asStateFlow()

    private val _batteryVoltage = MutableStateFlow(0)
    val batteryVoltage: StateFlow<Int> = _batteryVoltage.asStateFlow()

    fun updateBatteryInfo(context: Context) {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatusIntent = context.registerReceiver(null, intentFilter)
        
        batteryStatusIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            _batteryLevel.value = (level * 100 / scale.toFloat()).toInt()
            
            _batteryTemp.value = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
            _batteryVoltage.value = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            _batteryStatus.value = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                else -> "Healthy"
            }
        }
    }
}
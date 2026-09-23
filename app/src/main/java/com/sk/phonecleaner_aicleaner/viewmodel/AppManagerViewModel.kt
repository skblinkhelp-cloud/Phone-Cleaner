package com.sk.phonecleaner_aicleaner.viewmodel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sk.phonecleaner_aicleaner.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AppManagerViewModel : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    fun scanApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            refreshApps(context)
            _isScanning.value = false
        }
    }

    fun refreshApps(context: Context) {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        
        val appList = mutableListOf<AppInfo>()
        for (app in packages) {
            if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                val name = app.loadLabel(pm).toString()
                val packageName = app.packageName
                val size = File(app.sourceDir).length()
                appList.add(AppInfo(name, packageName, size))
            }
        }
        
        _apps.value = appList.sortedByDescending { it.sizeBytes }
    }

    fun toggleAppSelection(packageName: String) {
        val currentList = _apps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(isSelected = !app.isSelected)
            } else {
                app
            }
        }
        _apps.value = currentList
    }

    fun getSelectedApps(): List<AppInfo> {
        return _apps.value.filter { it.isSelected }
    }
}
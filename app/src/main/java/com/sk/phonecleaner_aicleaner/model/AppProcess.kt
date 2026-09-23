package com.sk.phonecleaner_aicleaner.model

import androidx.compose.ui.graphics.vector.ImageVector

data class AppProcess(
    val name: String,
    val packageName: String,
    val memoryUsageMb: Int,
    val icon: ImageVector? = null,
    var isChecked: Boolean = true
) {
    val memoryFormatted: String
        get() = "$memoryUsageMb MB"
}
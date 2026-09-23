package com.sk.phonecleaner_aicleaner.model

import androidx.compose.ui.graphics.vector.ImageVector

data class JunkItem(
    val name: String,
    val sizeBytes: Long,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color,
    var isChecked: Boolean = true,
    var isScanned: Boolean = false
) {
    val sizeFormatted: String
        get() = if (sizeBytes > 1024 * 1024 * 1024) {
            String.format("%.2f GB", sizeBytes / (1024.0 * 1024 * 1024))
        } else if (sizeBytes > 1024 * 1024) {
            String.format("%.2f MB", sizeBytes / (1024.0 * 1024))
        } else {
            String.format("%.2f KB", sizeBytes / 1024.0)
        }
}
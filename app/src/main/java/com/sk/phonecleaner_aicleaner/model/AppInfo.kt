package com.sk.phonecleaner_aicleaner.model

data class AppInfo(
    val name: String,
    val packageName: String,
    val sizeBytes: Long,
    val isSelected: Boolean = false
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
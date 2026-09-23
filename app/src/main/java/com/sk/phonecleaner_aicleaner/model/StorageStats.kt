package com.sk.phonecleaner_aicleaner.model

data class StorageStats(
    val totalBytes: Long = 0,
    val usedBytes: Long = 0,
    val imageBytes: Long = 0,
    val videoBytes: Long = 0,
    val audioBytes: Long = 0,
    val documentBytes: Long = 0,
    val apkBytes: Long = 0,
    val downloadBytes: Long = 0
) {
    val usedPercentage: Float
        get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f

    val totalGB: String
        get() = String.format("%.1fGB", totalBytes / (1024.0 * 1024 * 1024))

    val usedGB: String
        get() = String.format("%.1fGB", usedBytes / (1024.0 * 1024 * 1024))
}
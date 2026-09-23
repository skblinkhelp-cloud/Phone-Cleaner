package com.sk.phonecleaner_aicleaner.util

import java.util.Locale

object FileUtil {
    fun formatSize(bytes: Long): String {
        return if (bytes < 1024) "$bytes B"
        else if (bytes < 1024 * 1024) String.format(Locale.US, "%.2f KB", bytes / 1024.0)
        else if (bytes < 1024 * 1024 * 1024) String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024))
        else String.format(Locale.US, "%.2f GB", bytes / (1024.0 * 1024 * 1024))
    }
}
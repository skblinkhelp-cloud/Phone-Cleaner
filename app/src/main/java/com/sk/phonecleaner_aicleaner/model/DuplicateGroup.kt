package com.sk.phonecleaner_aicleaner.model

import java.io.File

data class DuplicateFile(
    val file: File,
    var isSelected: Boolean = false
)

data class DuplicateGroup(
    val size: Long,
    val files: List<DuplicateFile>
) {
    val sizeFormatted: String
        get() = if (size > 1024 * 1024 * 1024) {
            String.format("%.2f GB", size / (1024.0 * 1024 * 1024))
        } else if (size > 1024 * 1024) {
            String.format("%.2f MB", size / (1024.0 * 1024))
        } else {
            String.format("%.2f KB", size / 1024.0)
        }
}
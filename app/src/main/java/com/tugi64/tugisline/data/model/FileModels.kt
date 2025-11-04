package com.tugi64.tugisline.data.model

data class DWGFile(
    val id: String,
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    val isFavorite: Boolean = false,
    val thumbnail: String? = null
)

data class RecentFile(
    val file: DWGFile,
    val lastAccessed: Long
)

data class CloudProvider(
    val id: String,
    val name: String,
    val icon: Int,
    val isConnected: Boolean = false
)

enum class CloudProviderType {
    GOOGLE_DRIVE,
    DROPBOX,
    ONEDRIVE,
    FTP,
    SFTP
}


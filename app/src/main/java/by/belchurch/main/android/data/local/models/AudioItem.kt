package by.belchurch.main.android.data.local.models

import android.graphics.Bitmap
import android.net.Uri

data class AudioItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val artist: String,
    val duration: Int,
    val title: String,
    val artWork: String,
)
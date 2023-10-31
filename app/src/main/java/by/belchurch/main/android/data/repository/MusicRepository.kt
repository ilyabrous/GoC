package by.belchurch.main.android.data.repository

import android.net.Uri
import by.belchurch.main.android.data.local.models.AudioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val musicDatabase: MusicDatabase,
) {
    suspend fun getAudioData(): List<AudioItem> = withContext(Dispatchers.IO) {
        musicDatabase.getAllSongs().map { songDTO ->
            AudioItem(
                uri = Uri.parse(songDTO.songUrl),
                displayName = songDTO.title,
                id = songDTO.mediaId.toLong(),
                artist = songDTO.subtitle,
                duration = 200,
                title = songDTO.title,
                artWork = songDTO.imageUrl,
            )
        }
    }
}
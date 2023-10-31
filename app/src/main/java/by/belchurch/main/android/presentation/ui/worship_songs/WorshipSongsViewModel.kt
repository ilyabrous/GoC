package by.belchurch.main.android.presentation.ui.worship_songs

import android.net.Uri
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import by.belchurch.main.android.data.local.models.AudioItem
import by.belchurch.main.android.data.repository.MusicRepository
import by.belchurch.main.android.media_player.service.MusicServiceHandler
import by.belchurch.main.android.utils.MediaStateEvents
import by.belchurch.main.android.utils.MusicStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject

@HiltViewModel
class WorshipSongsViewModel @Inject constructor(
    private val musicServiceHandler: MusicServiceHandler,
    private val repository: MusicRepository,
) : ViewModel() {


    var duration = mutableLongStateOf(0L)
    var progress = mutableFloatStateOf(0f)
    var progressValue = mutableStateOf("00:00")
    var isMusicPlaying = mutableStateOf(false)
    var currentSelectedMusic = mutableStateOf(AudioItem(0L, "".toUri(), "", "", 0, "",  ""))
    var musicList = mutableStateOf(listOf<AudioItem>())

    private val _worshipSongsUiState: MutableStateFlow<WorshipSongsUIState> = MutableStateFlow(
        WorshipSongsUIState.InitialWorshipSongs
    )

    init {
        getMusicData()
    }

    init {
        viewModelScope.launch {
            musicServiceHandler.musicStates.collectLatest { musicStates: MusicStates ->
                when (musicStates) {
                    MusicStates.Initial -> _worshipSongsUiState.value =
                        WorshipSongsUIState.InitialWorshipSongs
                    is MusicStates.MediaBuffering -> progressCalculation(musicStates.progress)
                    is MusicStates.MediaPlaying -> isMusicPlaying.value = musicStates.isPlaying
                    is MusicStates.MediaProgress -> progressCalculation(musicStates.progress)
                    is MusicStates.CurrentMediaPlaying -> {
                        currentSelectedMusic.value = musicList.value[musicStates.mediaItemIndex]
                    }

                    is MusicStates.MediaReady -> {
                        duration.value = musicStates.duration
                        _worshipSongsUiState.value = WorshipSongsUIState.WorshipSongsReady

                    }

                }
            }
        }
    }

    fun onEvents(worshipSongsUiEvents: WorshipSongsUiEvents) = viewModelScope.launch {
        when (worshipSongsUiEvents) {
            WorshipSongsUiEvents.Backward -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.Backward)
            WorshipSongsUiEvents.Forward -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.Forward)
            WorshipSongsUiEvents.SeekToNext -> musicServiceHandler.onMediaStateEvents(
                MediaStateEvents.SeekToNext)
            WorshipSongsUiEvents.SeekToPrevious -> musicServiceHandler.onMediaStateEvents(
                MediaStateEvents.SeekToPrevious)
            is WorshipSongsUiEvents.PlayPause -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.PlayPause
                )
            }

            is WorshipSongsUiEvents.SeekTo -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.SeekTo,
                    seekPosition = ((duration.value * worshipSongsUiEvents.position) / 100f).toLong()
                )
            }

            is WorshipSongsUiEvents.CurrentAudioChanged -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.SelectedMusicChange,
                    selectedMusicIndex = worshipSongsUiEvents.index
                )
            }

            is WorshipSongsUiEvents.UpdateProgress -> {
                musicServiceHandler.onMediaStateEvents(
                    MediaStateEvents.MediaProgress(
                        worshipSongsUiEvents.progress
                    )
                )
                progress.value = worshipSongsUiEvents.progress
            }

        }
    }

    private fun getMusicData() {
        viewModelScope.launch {
            val musicData = repository.getAudioData()
            musicList.value = musicData
            setMusicItems()
        }
    }

    private fun setMusicItems() {
        musicList.value.map { audioItem ->
            MediaItem.Builder()
                .setUri(audioItem.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audioItem.artist)
                        .setAlbumTitle(audioItem.artist)
                        .setDisplayTitle(audioItem.title)
                        .setSubtitle(audioItem.artist)
                        .setArtworkUri(Uri.parse(audioItem.artWork))
                        .build()
                )
                .build()
        }.also {
            musicServiceHandler.setMediaItemList(it)
        }
    }

    private fun progressCalculation(currentProgress: Long) {
        progress.value = if (currentProgress > 0) ((currentProgress.toFloat() / duration.value.toFloat()) * 100f)
        else 0f

        progressValue.value = formatDurationValue(currentProgress)
    }

    private fun formatDurationValue(duration: Long): String {
        val minutes = MINUTES.convert(duration, MILLISECONDS)
        val seconds = (minutes) - minutes * SECONDS.convert(1, MINUTES)

        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        viewModelScope.launch {
            musicServiceHandler.onMediaStateEvents(MediaStateEvents.Stop)
        }
        super.onCleared()
    }

}
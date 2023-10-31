package by.belchurch.main.android.media_player.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import by.belchurch.main.android.utils.MediaStateEvents
import by.belchurch.main.android.utils.MusicStates
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer
) : Player.Listener {

    private val _musicStates: MutableStateFlow<MusicStates> = MutableStateFlow(MusicStates.Initial)
    val musicStates: StateFlow<MusicStates> = _musicStates.asStateFlow()

    private lateinit var cachedMediaItems: List<MediaItem>
    private var isRadioSelected = false

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
    }

    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItemList(mediaItems: List<MediaItem>) {
        cachedMediaItems = mediaItems
        exoPlayer.setMediaItems(cachedMediaItems)
        exoPlayer.prepare()
    }

    suspend fun onMediaStateEvents(
        mediaStateEvents: MediaStateEvents,
        selectedMusicIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        when (mediaStateEvents) {
            MediaStateEvents.Backward -> exoPlayer.seekBack()
            MediaStateEvents.Forward -> exoPlayer.seekForward()
            MediaStateEvents.PlayPause -> playPauseMusic()
            MediaStateEvents.SeekTo -> exoPlayer.seekTo(seekPosition)
            MediaStateEvents.SeekToNext -> exoPlayer.seekToNext()
            MediaStateEvents.SeekToPrevious -> exoPlayer.seekToPrevious()
            MediaStateEvents.Stop -> stopProgressUpdate()
            MediaStateEvents.PlayRadio -> {
                val mediaItem = MediaItem.Builder()
                    .setUri("https://s01.radio-tochka.com:5325/radio?1698489262888")
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
                isRadioSelected = true
            }

            MediaStateEvents.SelectedMusicChange -> {
                if (isRadioSelected) {
                    exoPlayer.setMediaItems(cachedMediaItems)
                    isRadioSelected = false
                }
                when (selectedMusicIndex) {
                    exoPlayer.currentMediaItemIndex -> {
                        playPauseMusic()
                    }

                    else -> {
                        exoPlayer.seekToDefaultPosition(selectedMusicIndex)
                        _musicStates.value = MusicStates.MediaPlaying(
                            isPlaying = true
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }

            is MediaStateEvents.MediaProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * mediaStateEvents.progress).toLong()
                )
            }

        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _musicStates.value =
                MusicStates.MediaBuffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _musicStates.value = MusicStates.MediaReady(exoPlayer.duration)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _musicStates.value = MusicStates.MediaPlaying(isPlaying = isPlaying)
        _musicStates.value = MusicStates.CurrentMediaPlaying(exoPlayer.currentMediaItemIndex)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun playPauseMusic() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _musicStates.value = MusicStates.MediaPlaying(
                isPlaying = true
            )
            startProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _musicStates.value = MusicStates.MediaProgress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _musicStates.value = MusicStates.MediaPlaying(isPlaying = false)
    }
}
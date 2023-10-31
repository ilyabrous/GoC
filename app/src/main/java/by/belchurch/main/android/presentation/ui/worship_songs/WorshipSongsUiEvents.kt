package by.belchurch.main.android.presentation.ui.worship_songs

sealed class WorshipSongsUiEvents {
    object PlayPause : WorshipSongsUiEvents()
    data class CurrentAudioChanged(val index: Int) : WorshipSongsUiEvents()
    data class SeekTo(val position: Float) : WorshipSongsUiEvents()
    data class UpdateProgress(val progress: Float) : WorshipSongsUiEvents()
    object SeekToNext : WorshipSongsUiEvents()
    object SeekToPrevious : WorshipSongsUiEvents()
    object Backward : WorshipSongsUiEvents()
    object Forward : WorshipSongsUiEvents()
}

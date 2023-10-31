package by.belchurch.main.android.utils

sealed class MediaStateEvents {
    object PlayPause : MediaStateEvents()
    object SeekToNext : MediaStateEvents()
    object SeekToPrevious : MediaStateEvents()
    object SeekTo : MediaStateEvents()
    object Backward : MediaStateEvents()
    object Forward : MediaStateEvents()
    object Stop : MediaStateEvents()
    object SelectedMusicChange : MediaStateEvents()
    object PlayRadio : MediaStateEvents()

    data class MediaProgress(val progress: Float) : MediaStateEvents()

}

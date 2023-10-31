package by.belchurch.main.android.presentation.ui.worship_songs

sealed class WorshipSongsUIState{
    object InitialWorshipSongs: WorshipSongsUIState()
    object WorshipSongsReady: WorshipSongsUIState()
}

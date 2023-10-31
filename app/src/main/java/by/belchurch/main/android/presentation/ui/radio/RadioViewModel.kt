package by.belchurch.main.android.presentation.ui.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.belchurch.main.android.media_player.service.MusicServiceHandler
import by.belchurch.main.android.utils.MediaStateEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val musicServiceHandler: MusicServiceHandler,
) : ViewModel() {


    fun onEvents(radioUiEvents: RadioUiEvents) = viewModelScope.launch {
        when (radioUiEvents) {
            RadioUiEvents.StartRadio -> musicServiceHandler.onMediaStateEvents(MediaStateEvents.PlayRadio)
        }
    }

}
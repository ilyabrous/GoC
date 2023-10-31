package by.belchurch.main.android.presentation.ui.worship_songs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.belchurch.main.android.data.local.models.AudioItem
import by.belchurch.main.android.presentation.ui.widgets.MiniPlayer
import by.belchurch.main.android.presentation.ui.widgets.MusicItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    progress: Float,
    onProgressCallback: (Float) -> Unit,
    isMusicPlaying: Boolean,
    currentPlayingMusic: AudioItem,
    musicList: List<AudioItem>,
    onStartCallback: () -> Unit,
    onMusicClick: (Int) -> Unit,
    onNextCallback: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text(text = "Music App") }
            )

        },
        bottomBar = {
            MiniPlayer(
                musicItem = currentPlayingMusic,
                progress = progress,
                isMusicPlaying = isMusicPlaying,
                onProgressCallback = onProgressCallback,
                onNextCallback = onNextCallback,
                onStartCallback = onStartCallback,
            )
        }
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {

            LazyColumn(
                contentPadding = it
            ) {
                itemsIndexed(musicList) { index, item ->
                    MusicItem(item, onClickCallback = {
                        onMusicClick(index)
                    })
                }
            }
        }


    }

}




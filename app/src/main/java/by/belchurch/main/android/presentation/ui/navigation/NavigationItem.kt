package by.belchurch.main.android.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Radio : NavigationItem("radio", Icons.Filled.Radio, "Radio")
    object WorshipSongs : NavigationItem("worship_songs", Icons.Filled.QueueMusic, "Holy Songs")
    object LastSermons : NavigationItem("last_sermons", Icons.Filled.Church, "Last Sermons")
}
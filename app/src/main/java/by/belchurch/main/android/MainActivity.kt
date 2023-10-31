package by.belchurch.main.android

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import by.belchurch.main.android.media_player.service.MediaService
import by.belchurch.main.android.presentation.ui.last_sermons.LastSermonsScreen
import by.belchurch.main.android.presentation.ui.worship_songs.HomeScreen
import by.belchurch.main.android.presentation.ui.worship_songs.WorshipSongsViewModel
import by.belchurch.main.android.presentation.ui.navigation.NavigationItem
import by.belchurch.main.android.presentation.ui.radio.RadioScreen
import by.belchurch.main.android.presentation.ui.radio.RadioUiEvents
import by.belchurch.main.android.presentation.ui.radio.RadioViewModel
import by.belchurch.main.android.presentation.ui.theme.GoCAppTheme
import by.belchurch.main.android.presentation.ui.worship_songs.WorshipSongsUiEvents
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val worshipSongsViewModel: WorshipSongsViewModel by viewModels()
    private val radioViewModel: RadioViewModel by viewModels()
    private var isServiceRunning = false

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoCAppTheme {
                val isPermissionGranted = rememberPermissionState(
                    permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
                )
                val lifeCycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifeCycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            isPermissionGranted.launchPermissionRequest()
                        }
                    }
                    lifeCycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifeCycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TabsScreen()
                }

            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TabsScreen() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            content = { padding ->
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Navigation(navController = navController)
                }
            },
        )
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController, startDestination = NavigationItem.WorshipSongs.route) {
            composable(NavigationItem.Radio.route) {
                RadioScreen(
                    onStartRadio = {
                        radioViewModel.onEvents(RadioUiEvents.StartRadio)
                        startMusicService()
                    }
                )
            }
            composable(NavigationItem.WorshipSongs.route) {
                HomeScreen(
                    progress = worshipSongsViewModel.progress.value,
                    onProgressCallback = {
                        worshipSongsViewModel.onEvents(WorshipSongsUiEvents.SeekTo(it))
                    },
                    isMusicPlaying = worshipSongsViewModel.isMusicPlaying.value,
                    currentPlayingMusic = worshipSongsViewModel.currentSelectedMusic.value,
                    musicList = worshipSongsViewModel.musicList.value,
                    onStartCallback = {
                        worshipSongsViewModel.onEvents(WorshipSongsUiEvents.PlayPause)
                    },
                    onMusicClick = {
                        worshipSongsViewModel.onEvents(WorshipSongsUiEvents.CurrentAudioChanged(it))
                        startMusicService()
                    },
                    onNextCallback = {
                        worshipSongsViewModel.onEvents(WorshipSongsUiEvents.SeekToNext)
                    },
                )
            }
            composable(NavigationItem.LastSermons.route) {
                LastSermonsScreen()
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Radio,
            NavigationItem.WorshipSongs,
            NavigationItem.LastSermons,
        )
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    alwaysShowLabel = false,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }


    private fun startMusicService() {
        if (!isServiceRunning) {
            val intent = Intent(this, MediaService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }
}





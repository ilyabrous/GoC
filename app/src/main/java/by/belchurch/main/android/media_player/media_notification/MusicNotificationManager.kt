package by.belchurch.main.android.media_player.media_notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import by.belchurch.main.android.MainActivity
import by.belchurch.main.android.R
import by.belchurch.main.android.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MusicNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer
) {

    private val musicNotificationManager: NotificationManagerCompat =
        NotificationManagerCompat
            .from(context)

    init {
        createMusicNotificationChannel()
    }

    private fun createMusicNotificationChannel() {
        val musicNotificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        musicNotificationManager.createNotificationChannel(musicNotificationChannel)
    }

    @UnstableApi
    private fun buildMusicNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            Constants.NOTIFICATION_ID,
            Constants.NOTIFICATION_CHANNEL_ID
        )
            .setMediaDescriptionAdapter(
                MusicNotificationDescriptorAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .setSmallIconResourceId(R.drawable.music_icon)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setUseStopAction(true)
                it.setPriority(NotificationCompat.PRIORITY_HIGH)
                it.setPlayer(exoPlayer)
            }
    }

    @UnstableApi
    fun startMusicNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildMusicNotification(mediaSession)
        startForegroundMusicService(mediaSessionService)
    }

    private fun startForegroundMusicService(mediaSessionService: MediaSessionService) {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntentToOpenApp: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val musicNotification = Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(resultPendingIntentToOpenApp)
            .build()

        mediaSessionService.startForeground(Constants.NOTIFICATION_ID, musicNotification)
    }
}
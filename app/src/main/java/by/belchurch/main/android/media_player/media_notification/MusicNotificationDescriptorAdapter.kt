package by.belchurch.main.android.media_player.media_notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@UnstableApi
class MusicNotificationDescriptorAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence? =
        player.mediaMetadata.albumTitle ?: "Unknown"

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        Glide.with(context)
            .asBitmap()
            .load("https://firebasestorage.googleapis.com/v0/b/spotifyclone-1f4e6.appspot.com/o/logo.jpg?alt=media&token=76cc7237-3298-4d0d-a7a8-fe6db8d3b97c&_gl=1*bob7tn*_ga*MTQ1OTUyNDE5MC4xNjk3ODk0MjIw*_ga_CW55HF8NVT*MTY5ODQ5MjQ2OS4zLjEuMTY5ODQ5NDA5NS4yMS4wLjA.")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback.onBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit

            })

        return null
    }

}
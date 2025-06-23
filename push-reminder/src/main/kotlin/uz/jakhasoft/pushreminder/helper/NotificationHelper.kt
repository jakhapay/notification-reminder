package uz.jakhasoft.pushreminder.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    fun showNotification(
        id: String,
        title: String,
        message: String,
        smallIcon: Int,
        largeIcon: Bitmap?,
        bigImage: Bitmap?,
        channelId: String?,
        channelName: String?,
        color: Int,
        visibility: Int,
        category: String?,
        timestamp: Long,
        soundUri: Uri?,
        pendingIntent: PendingIntent,
    ) {
        val finalChannelId = channelId ?: "${context.applicationContext.packageName}.default_channel"
        val finalChannelName = channelName ?: "${context.applicationContext.applicationContext} Reminders"
        val finalSoundUri = soundUri ?: DEFAULT_NOTIFICATION_URI

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                finalChannelId,
                finalChannelName,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                enableLights(true)
                lightColor = Color.BLUE
                setSound(finalSoundUri, null)
                description = "Notifications Reminder"
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, finalChannelId)
            .setContentIntent(pendingIntent)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(color)
            .setWhen(timestamp)
            .setShowWhen(true)
            .setVisibility(visibility)
            .setCategory(category)

        if (largeIcon != null) builder.setLargeIcon(largeIcon)

        if (bigImage != null) {
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigImage),
            )
        } else {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }

        manager.notify(id.hashCode(), builder.build())
    }
}

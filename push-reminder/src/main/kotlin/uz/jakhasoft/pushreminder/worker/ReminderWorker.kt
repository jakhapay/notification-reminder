package uz.jakhasoft.pushreminder.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.Coil
import coil.request.ImageRequest
import uz.jakhasoft.pushreminder.helper.NotificationHelper

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(KEY_ID) ?: return Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        val smallIcon = inputData.getInt(KEY_SMALL_ICON, 0)
        if (smallIcon == 0) return Result.failure()

        val largeIconUrl = inputData.getString(KEY_LARGE_ICON)
        val bigImageUrl = inputData.getString(KEY_BIG_IMAGE)
        val channelId = inputData.getString(KEY_CHANNEL_ID)
        val channelName = inputData.getString(KEY_CHANNEL_NAME)
        val timestamp = inputData.getLong(KEY_TIMESTAMP, System.currentTimeMillis())
        val category = inputData.getString(KEY_CATEGORY)
        val visibility = inputData.getInt(KEY_VISIBILITY, NotificationCompat.VISIBILITY_PUBLIC)
        val color = inputData.getInt(KEY_COLOR, Color.BLUE)
        val soundUri = inputData.getString(KEY_SOUND_URI)?.toUri()
        val customKeys = inputData.keyValueMap
            .filterKeys { it !in RESERVED_KEYS }
            .mapValues { it.value.toString() }

        val largeIconBitmap = loadImage(largeIconUrl)
        val bigImageBitmap = loadImage(bigImageUrl)

        NotificationHelper(context).showNotification(
            id = id,
            title = title,
            message = message,
            smallIcon = smallIcon,
            largeIcon = largeIconBitmap,
            bigImage = bigImageBitmap,
            channelId = channelId,
            channelName = channelName,
            color = color,
            visibility = visibility,
            category = category,
            timestamp = timestamp,
            soundUri = soundUri,
            getDefaultLaunchIntent(customKeys)
        )

        return Result.success()
    }

    private suspend fun loadImage(url: String?): Bitmap? {
        if (url.isNullOrBlank()) return null
        return try {
            val result = Coil.imageLoader(context).execute(
                ImageRequest.Builder(context).data(url).allowHardware(false).build()
            )
            (result.drawable as? BitmapDrawable)?.bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun getDefaultLaunchIntent(customData: Map<String, String>): PendingIntent {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                customData.forEach { (key, value) ->
                    putExtra(key, value)
                }
            } ?: throw IllegalStateException("Unable to find launch intent")

        return PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val KEY_ID = "key_id"
        const val KEY_TITLE = "key_title"
        const val KEY_MESSAGE = "key_message"
        const val KEY_SMALL_ICON = "key_small_icon"
        const val KEY_LARGE_ICON = "key_large_icon"
        const val KEY_BIG_IMAGE = "key_big_image"
        const val KEY_CHANNEL_ID = "key_channel_id"
        const val KEY_CHANNEL_NAME = "key_channel_name"
        const val KEY_COLOR = "key_color"
        const val KEY_SOUND_URI = "key_sound_uri"
        const val KEY_VISIBILITY = "key_visibility"
        const val KEY_CATEGORY = "key_category"
        const val KEY_TIMESTAMP = "key_timestamp"

        val RESERVED_KEYS = setOf(
            KEY_ID, KEY_TITLE, KEY_MESSAGE, KEY_SMALL_ICON,
            KEY_LARGE_ICON, KEY_BIG_IMAGE, KEY_CHANNEL_ID,
            KEY_CHANNEL_NAME, KEY_COLOR, KEY_SOUND_URI,
            KEY_VISIBILITY, KEY_CATEGORY, KEY_TIMESTAMP
        )
    }
}

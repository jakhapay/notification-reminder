package uz.jakhasoft.pushreminder.scheduler

import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import uz.jakhasoft.pushreminder.worker.ReminderWorker
import java.util.concurrent.TimeUnit

class ReminderBuilder {

    // Required
    private var uniqueId: String? = null
    private var title: String? = null
    private var message: String? = null
    private var delay: Long? = null
    private var delayUnit: TimeUnit? = null
    private var smallIcon: Int? = null
    private var channelId: String? = null
    private var channelName: String? = null

    // Optional
    private var largeIcon: String? = null
    private var bigImage: String? = null
    private var color: Int? = null
    private var soundUri: Uri? = null
    private var visibility: Int? = null
    private var category: String? = null
    private var timestamp: Long? = null
    private val customData: MutableMap<String, String> = mutableMapOf()

    private var repeatInterval: Long? = null
    private var repeatUnit: TimeUnit? = null

    fun setId(id: String) = apply { this.uniqueId = id }
    fun setTitle(value: String) = apply { title = value }
    fun setMessage(value: String) = apply { message = value }
    fun setDelay(value: Long, unit: TimeUnit) = apply { delay = value; delayUnit = unit }
    fun setSmallIcon(iconRes: Int) = apply { smallIcon = iconRes }

    fun setLargeIconUrl(url: String) = apply { largeIcon = url }
    fun setBigImageUrl(url: String) = apply { bigImage = url }
    fun setChannelId(id: String) = apply { channelId = id }
    fun setChannelName(name: String) = apply { channelName = name }
    fun setColor(value: Int) = apply { color = value }
    fun setSound(uri: Uri) = apply { soundUri = uri }
    fun setVisibility(value: Int) = apply { visibility = value }
    fun setCategory(value: String) = apply { category = value }
    fun setTimestamp(value: Long) = apply { timestamp = value }
    fun setCustomData(map: MutableMap<String, String>) = apply { customData.putAll(map) }
    fun setRepeatInterval(value: Long, unit: TimeUnit) = apply { repeatInterval = value; repeatUnit = unit }

    fun build(): Pair<String, WorkRequest> {
        require(true)
        val id = uniqueId ?: throw IllegalStateException("Reminder ID is required")
        val finalTitle = title ?: throw IllegalStateException("Title is required.")
        val finalMessage = message ?: throw IllegalStateException("Message is required.")
        val finalDelay = delay ?: throw IllegalStateException("Delay is required.")
        val finalDelayUnit = delayUnit ?: throw IllegalStateException("Delay unit is required.")
        val finalSmallIcon = smallIcon ?: throw IllegalStateException("Small icon is required.")

        val dataBuilder = Data.Builder()
            .putString(ReminderWorker.KEY_ID, id)
            .putString(ReminderWorker.KEY_TITLE, finalTitle)
            .putString(ReminderWorker.KEY_MESSAGE, finalMessage)
            .putInt(ReminderWorker.KEY_SMALL_ICON, finalSmallIcon)
            .putString(ReminderWorker.KEY_LARGE_ICON, largeIcon)
            .putString(ReminderWorker.KEY_BIG_IMAGE, bigImage)
            .putString(ReminderWorker.KEY_CHANNEL_ID, channelId)
            .putString(ReminderWorker.KEY_CHANNEL_NAME, channelName)
            .putLong(ReminderWorker.KEY_TIMESTAMP, timestamp ?: System.currentTimeMillis())
            .putInt(ReminderWorker.KEY_VISIBILITY, visibility ?: NotificationCompat.VISIBILITY_PUBLIC)
            .putString(ReminderWorker.KEY_CATEGORY, category)

        customData.forEach { (key, value) ->
            dataBuilder.putString(key, value)
        }



        soundUri?.let { dataBuilder.putString(ReminderWorker.KEY_SOUND_URI, it.toString()) }
        color?.let { dataBuilder.putInt(ReminderWorker.KEY_COLOR, it) }

        val request = if (repeatInterval != null && repeatUnit != null) {
            PeriodicWorkRequestBuilder<ReminderWorker>(repeatInterval!!, repeatUnit!!)
                .setInitialDelay(finalDelay, finalDelayUnit)
                .setInputData(dataBuilder.build())
                .build()
        } else {
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(finalDelay, finalDelayUnit)
                .setInputData(dataBuilder.build())
                .build()
        }

        return id to request
    }

}

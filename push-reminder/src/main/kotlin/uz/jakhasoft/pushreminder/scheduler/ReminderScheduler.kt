package uz.jakhasoft.pushreminder.scheduler

import android.app.NotificationManager
import android.content.Context
import androidx.work.*

object ReminderScheduler {

    fun schedule(context: Context, builder: ReminderBuilder) {
        val (id, request) = builder.build()

        val workManager = WorkManager.getInstance(context)

        when (request) {
            is PeriodicWorkRequest -> {
                workManager.enqueueUniquePeriodicWork(
                    id,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
            }

            is OneTimeWorkRequest -> {
                workManager.enqueueUniqueWork(
                    id,
                    ExistingWorkPolicy.KEEP,
                    request
                )
            }

            else -> {
                throw IllegalStateException("Unsupported WorkRequest type")
            }
        }
    }

    fun cancel(context: Context, id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(id)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id.hashCode())
    }
}

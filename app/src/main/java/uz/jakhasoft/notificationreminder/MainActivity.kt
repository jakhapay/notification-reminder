package uz.jakhasoft.notificationreminder

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import uz.jakhasoft.notificationreminder.ui.theme.NotificationReminderTheme
import uz.jakhasoft.pushreminder.scheduler.ReminderBuilder
import uz.jakhasoft.pushreminder.scheduler.ReminderScheduler
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivityIntent", "onCreate: $intent")

        requestNotificationPermissionIfNeeded()

        enableEdgeToEdge()
        setContent {
            NotificationReminderTheme {
                Scaffold (Modifier.fillMaxSize()) {  innerPadding ->
                    TimePickerButton(Modifier.padding(innerPadding)) { selectedTime ->
                        // Schedule the notification based on selectedTime
                        val now = LocalTime.now()
                        val delayMinutes = java.time.Duration.between(now, selectedTime).toMinutes()
                            .let { if (it < 0) it + 24 * 60 else it }

                        val delayMillis = delayMinutes * 60 * 1000

                        val reminder = ReminderBuilder()
                            .setId("from_compose_timepicker")
                            .setTitle("⏰ Compose Reminder")
                            .setMessage("This was scheduled using Compose!")
                            .setDelay(delayMillis, TimeUnit.MILLISECONDS)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)

                        ReminderScheduler.schedule(this, reminder)
                    }
                }
            }
        }

    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}


@Composable
fun TimePickerButton(modifier: Modifier, onTimeSelected: (LocalTime) -> Unit) {
    val timeDialogState = rememberMaterialDialogState()

    Button(modifier = modifier, onClick = { timeDialogState.show() }) {
        Text("Pick time")
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("OK")
            negativeButton("Cancel")
        }
    ) {
        timepicker(
            is24HourClock = true,
            title = "Select time"
        ) { time ->
            onTimeSelected(time)
        }
    }
}

package com.submissionandroid.dicodingevents

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.submissionandroid.dicodingevents.repository.EventsRepository
import com.submissionandroid.dicodingevents.retrofit.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: EventsRepository // Inject the repository here
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch the nearest active event
            val nearestEvent = repository.getNearestActiveEvent().listEvents?.firstOrNull()
            if (nearestEvent != null) {
                // Show notification for the nearest event
                showNotification(nearestEvent.name.toString(), nearestEvent.beginTime.toString())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(eventName: String, eventDate: String) {
        // Create notification channel (required for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_channel_id",
                "Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, "event_channel_id")
            .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
            .setContentTitle("Upcoming Event Reminder")
            .setContentText("Event: $eventName on $eventDate")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Check if notification permission is granted (Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Log or handle permission not granted
                return
            }
        }

        // Show notification
        NotificationManagerCompat.from(applicationContext).notify(1, notification)
    }
}

package br.com.easyreminder.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.easyreminder.model.Reminder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun schedule(context: Context, reminder: Reminder) {
        val dateTime = reminder.dateTime ?: return

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = sdf.parse(dateTime) ?: return
        val delay = date.time - System.currentTimeMillis()

        if (delay <= 0) return

        val data = Data.Builder()
            .putString("title", reminder.title)
            .putString("description", reminder.description)
            .build()

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_${reminder.id}")
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancel(context: Context, reminderId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("reminder_${reminderId}")
    }
}
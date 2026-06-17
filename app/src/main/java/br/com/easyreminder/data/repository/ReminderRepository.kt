package br.com.easyreminder.data.repository

import androidx.lifecycle.LiveData
import br.com.easyreminder.data.local.ReminderDao
import br.com.easyreminder.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAll()

    suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}
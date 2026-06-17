package br.com.easyreminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.easyreminder.data.local.AppDatabase
import br.com.easyreminder.data.repository.ReminderRepository
import br.com.easyreminder.model.Reminder
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: LiveData<List<Reminder>>

    init {
        val dao = AppDatabase.getInstance(application).reminderDao()
        repository = ReminderRepository(dao)
        allReminders = repository.allReminders
    }

    fun insert(reminder: Reminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        repository.delete(reminder)
    }
}
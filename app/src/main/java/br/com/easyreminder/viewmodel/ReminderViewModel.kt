package br.com.easyreminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import br.com.easyreminder.data.local.AppDatabase
import br.com.easyreminder.data.repository.CategoryRepository
import br.com.easyreminder.data.repository.ReminderRepository
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.model.ReminderWithCategory
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderRepository: ReminderRepository
    private val categoryRepository: CategoryRepository

    val remindersWithCategory: LiveData<List<ReminderWithCategory>>

    init {
        val db = AppDatabase.getInstance(application)
        reminderRepository = ReminderRepository(db.reminderDao())
        categoryRepository = CategoryRepository(db.categoryDao())

        val mediator = MediatorLiveData<List<ReminderWithCategory>>()

        mediator.addSource(reminderRepository.allReminders) { reminders ->
            val categories = categoryRepository.allCategories.value ?: emptyList()
            mediator.value = reminders.map { reminder ->
                ReminderWithCategory(
                    reminder = reminder,
                    category = categories.find { it.id == reminder.categoryId }
                )
            }
        }

        mediator.addSource(categoryRepository.allCategories) { categories ->
            val reminders = reminderRepository.allReminders.value ?: emptyList()
            mediator.value = reminders.map { reminder ->
                ReminderWithCategory(
                    reminder = reminder,
                    category = categories.find { it.id == reminder.categoryId }
                )
            }
        }

        remindersWithCategory = mediator
    }

    fun insert(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.insert(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.update(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.delete(reminder)
    }
}
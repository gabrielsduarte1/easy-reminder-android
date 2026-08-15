package br.com.easyreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.easyreminder.data.repository.CategoryRepository
import br.com.easyreminder.data.repository.ReminderRepository
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.model.ReminderWithCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val mediator = MediatorLiveData<List<ReminderWithCategory>>()
    val remindersWithCategory: LiveData<List<ReminderWithCategory>> = mediator

    init {
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
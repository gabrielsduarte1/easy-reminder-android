package br.com.easyreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.easyreminder.data.repository.ReminderRepository
import br.com.easyreminder.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    val allReminders: LiveData<List<Reminder>> = reminderRepository.allReminders

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
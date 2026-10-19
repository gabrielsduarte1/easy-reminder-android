package br.com.easyreminder.model

data class ReminderWithCategory(
    val reminder: Reminder,
    val category: Category?
)
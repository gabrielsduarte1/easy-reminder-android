package br.com.easyreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "category")
    val category: String? = null,

    @ColumnInfo(name = "date_time")
    val dateTime: String? = null,

    @ColumnInfo(name = "is_completed", defaultValue = "0")
    val isCompleted: Boolean = false
)
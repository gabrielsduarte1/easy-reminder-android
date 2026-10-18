package br.com.easyreminder.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar

fun Context.showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
    val calendar = Calendar.getInstance()

    val dialog = DatePickerDialog(
        this,
        { _, year, month, day -> onDateSelected(year, month, day) },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dialog.datePicker.minDate = System.currentTimeMillis() - 1000
    dialog.show()
}

fun Context.showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
    val calendar = Calendar.getInstance()

    TimePickerDialog(
        this,
        { _, hour, minute -> onTimeSelected(hour, minute) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}
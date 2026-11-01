package br.com.easyreminder.model

import androidx.annotation.ColorRes

enum class ReminderStatus(
    @ColorRes val stripeColorRes: Int?,
    @ColorRes val titleColorRes: Int,
    val alpha: Float,
    val isStrikeThrough: Boolean
) {
    PENDING(
        stripeColorRes = null,
        titleColorRes = br.com.easyreminder.R.color.reminder_title,
        alpha = 1.0f,
        isStrikeThrough = false
    ),
    COMPLETED(
        stripeColorRes = br.com.easyreminder.R.color.swipe_complete,
        titleColorRes = br.com.easyreminder.R.color.reminder_completed,
        alpha = 0.6f,
        isStrikeThrough = true
    );

    companion object {
        fun from(isCompleted: Boolean): ReminderStatus =
            if (isCompleted) COMPLETED else PENDING
    }
}
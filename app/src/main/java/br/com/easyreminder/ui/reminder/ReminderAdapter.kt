package br.com.easyreminder.ui.reminder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.R
import br.com.easyreminder.databinding.ItemReminderBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.model.ReminderCategory
import br.com.easyreminder.model.ReminderStatus

class ReminderAdapter(
    private val onClick: (Reminder) -> Unit,
    private val onLongClick: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }

    inner class ReminderViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        holder.binding.textViewTitle.text = item.title
        holder.binding.textViewDescription.text = item.description

        val category = item.category?.let {
            try {
                ReminderCategory.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }

        val categoryColor = category?.colorRes?.let { ContextCompat.getColor(context, it) }
            ?: ContextCompat.getColor(context, R.color.category_default)

        val status = ReminderStatus.from(item.isCompleted)

        val stripeColor = status.stripeColorRes
            ?.let { ContextCompat.getColor(context, it) }
            ?: categoryColor

        holder.binding.viewCategoryColor.setBackgroundColor(stripeColor)

        holder.binding.textViewTitle.setTextColor(
            ContextCompat.getColor(context, status.titleColorRes)
        )

        holder.binding.textViewTitle.paintFlags = if (status.isStrikeThrough) {
            holder.binding.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemView.alpha = status.alpha

        holder.binding.textViewCategory.text = category?.let { context.getString(it.displayNameRes) }
            ?: context.getString(R.string.category_none)
        holder.binding.textViewCategory.setTextColor(categoryColor)

        val background = android.graphics.drawable.GradientDrawable()
        background.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
        background.cornerRadius = 32f
        background.setColor(categoryColor)
        background.alpha = 51
        holder.binding.textViewCategory.background = background

        if (item.dateTime != null) {
            holder.binding.textViewDateTime.visibility = android.view.View.VISIBLE
            holder.binding.textViewDateTime.text = context.getString(R.string.format_date_time, item.dateTime)
        } else {
            holder.binding.textViewDateTime.visibility = android.view.View.GONE
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }
}
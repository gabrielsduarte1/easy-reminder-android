package br.com.easyreminder.ui.reminder

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.databinding.ItemReminderBinding
import br.com.easyreminder.model.ReminderWithCategory

class ReminderAdapter(
    private val onClick: (ReminderWithCategory) -> Unit,
    private val onLongClick: (ReminderWithCategory) -> Unit
) : ListAdapter<ReminderWithCategory, ReminderAdapter.ReminderViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ReminderWithCategory>() {
        override fun areItemsTheSame(oldItem: ReminderWithCategory, newItem: ReminderWithCategory): Boolean {
            return oldItem.reminder.id == newItem.reminder.id
        }

        override fun areContentsTheSame(oldItem: ReminderWithCategory, newItem: ReminderWithCategory): Boolean {
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
        holder.binding.textViewTitle.text = item.reminder.title
        holder.binding.textViewDescription.text = item.reminder.description

        val categoryColor = item.category?.color ?: "#CCCCCC"

        if (item.reminder.isCompleted) {
            holder.binding.viewCategoryColor.setBackgroundColor(Color.parseColor("#4CAF50"))
            holder.binding.textViewTitle.paintFlags =
                holder.binding.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.textViewTitle.setTextColor(Color.parseColor("#9E9E9E"))
            holder.itemView.alpha = 0.6f
        } else {
            holder.binding.viewCategoryColor.setBackgroundColor(Color.parseColor(categoryColor))
            holder.binding.textViewTitle.paintFlags =
                holder.binding.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.textViewTitle.setTextColor(Color.BLACK)
            holder.itemView.alpha = 1.0f
        }

        holder.binding.textViewCategory.text = item.category?.name ?: "Sem categoria"
        holder.binding.textViewCategory.setTextColor(Color.parseColor(categoryColor))

        val bgColor = Color.parseColor(categoryColor)
        val background = android.graphics.drawable.GradientDrawable()
        background.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
        background.cornerRadius = 32f
        background.setColor(bgColor)
        background.alpha = 51
        holder.binding.textViewCategory.background = background

        if (item.reminder.dateTime != null) {
            holder.binding.textViewDateTime.visibility = android.view.View.VISIBLE
            holder.binding.textViewDateTime.text = "🕐 ${item.reminder.dateTime}"
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
package br.com.easyreminder.ui.reminder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.R
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

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textViewTitle)
        val description: TextView = itemView.findViewById(R.id.textViewDescription)
        val categoryColor: View = itemView.findViewById(R.id.viewCategoryColor)
        val categoryName: TextView = itemView.findViewById(R.id.textViewCategory)

        val dateTime: TextView = itemView.findViewById(R.id.textViewDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val item = getItem(position)
        holder.title.text = item.reminder.title
        holder.description.text = item.reminder.description

        val color = item.category?.color ?: "#CCCCCC"
        holder.categoryColor.setBackgroundColor(Color.parseColor(color))

        holder.categoryName.text = item.category?.name ?: "Sem categoria"
        holder.categoryName.setTextColor(Color.parseColor(color))

        val bgColor = Color.parseColor(color)
        val background = android.graphics.drawable.GradientDrawable()
        background.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
        background.cornerRadius = 32f
        background.setColor(bgColor)
        background.alpha = 51
        holder.categoryName.background = background

        if (item.reminder.dateTime != null) {
            holder.dateTime.visibility = android.view.View.VISIBLE
            holder.dateTime.text = "🕐 ${item.reminder.dateTime}"
        } else {
            holder.dateTime.visibility = android.view.View.GONE
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.setOnLongClickListener {
            onLongClick(item)
            true
        }
    }
}
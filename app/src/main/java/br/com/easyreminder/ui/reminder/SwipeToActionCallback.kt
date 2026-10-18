package br.com.easyreminder.ui.reminder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.R

class SwipeToActionCallback(
    context: Context,
    private val onSwipeLeft: (position: Int) -> Unit,
    private val onSwipeRight: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    private val paint = Paint()
    private val deleteColor = ContextCompat.getColor(context, R.color.swipe_delete)
    private val completeColor = ContextCompat.getColor(context, R.color.swipe_complete)

    private val deleteIcon: Drawable? =
        ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)

    private val completeIcon: Drawable? =
        ContextCompat.getDrawable(context, android.R.drawable.checkbox_on_background)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return

        if (direction == ItemTouchHelper.LEFT) {
            onSwipeLeft(position)
        } else {
            onSwipeRight(position)
        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        when {
            dX < 0 -> drawBackground(canvas, itemView, dX, deleteColor, deleteIcon, toLeft = true)
            dX > 0 -> drawBackground(canvas, itemView, dX, completeColor, completeIcon, toLeft = false)
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawBackground(
        canvas: Canvas,
        itemView: View,
        dX: Float,
        color: Int,
        icon: Drawable?,
        toLeft: Boolean
    ) {
        paint.color = color

        if (toLeft) {
            canvas.drawRect(
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                paint
            )
        } else {
            canvas.drawRect(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left + dX,
                itemView.bottom.toFloat(),
                paint
            )
        }

        if (icon == null) return

        val margin = (itemView.height - icon.intrinsicHeight) / 2
        val top = itemView.top + margin
        val bottom = top + icon.intrinsicHeight
        val left = if (toLeft) itemView.right - margin - icon.intrinsicWidth else itemView.left + margin
        val right = left + icon.intrinsicWidth

        icon.setBounds(left, top, right, bottom)
        icon.draw(canvas)
    }
}
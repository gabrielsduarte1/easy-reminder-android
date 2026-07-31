package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.R
import br.com.easyreminder.databinding.FragmentReminderListBinding
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler

class ReminderListFragment : Fragment() {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var adapter: ReminderAdapter

    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        adapter = ReminderAdapter(
            onClick = { item ->
                val action = ReminderListFragmentDirections
                    .actionListToDetail(item.reminder.id)
                findNavController().navigate(action)
            },
            onLongClick = { item ->
                showContextMenu(item.reminder)
            }
        )

        binding.recyclerViewReminders.adapter = adapter
        binding.recyclerViewReminders.layoutManager = LinearLayoutManager(requireContext())

        binding.fabAddReminder.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_create)
        }

        viewModel.remindersWithCategory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            if (items.isEmpty()) {
                binding.recyclerViewReminders.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerViewReminders.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
        }

        setupSwipeToDelete()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsFragment -> {
                findNavController().navigate(R.id.action_list_to_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showContextMenu(reminder: br.com.easyreminder.model.Reminder) {
        val options = arrayOf("Editar", "Excluir")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(reminder.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val action = ReminderListFragmentDirections
                            .actionListToDetail(reminder.id)
                        findNavController().navigate(action)
                    }
                    1 -> confirmDelete(reminder)
                }
            }
            .show()
    }

    private fun confirmDelete(reminder: br.com.easyreminder.model.Reminder) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Excluir lembrete")
            .setMessage("Deseja excluir \"${reminder.title}\"?")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.delete(reminder)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = androidx.recyclerview.widget.ItemTouchHelper(
            object : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(
                0, androidx.recyclerview.widget.ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.currentList[position]
                    val reminder = item.reminder

                    viewModel.delete(reminder)
                    ReminderScheduler.cancel(requireContext(), reminder.id)

                    com.google.android.material.snackbar.Snackbar.make(
                        requireView(),
                        "Lembrete excluído",
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).setAction("DESFAZER") {
                        viewModel.insert(reminder)
                        ReminderScheduler.schedule(requireContext(), reminder)
                    }.show()
                }

                override fun onChildDraw(
                    canvas: android.graphics.Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float, dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val paint = android.graphics.Paint()
                    paint.color = android.graphics.Color.parseColor("#E24B4A")
                    canvas.drawRect(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )
                    val icon = androidx.core.content.ContextCompat.getDrawable(
                        requireContext(),
                        android.R.drawable.ic_menu_delete
                    )
                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(canvas)
                    }
                    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        )
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewReminders)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
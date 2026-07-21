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
import br.com.easyreminder.viewmodel.ReminderViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReminderListFragment : Fragment() {

    private lateinit var viewModel: ReminderViewModel
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_list, container, false)
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReminders)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val layoutEmpty = view.findViewById<android.widget.LinearLayout>(R.id.layoutEmpty)

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddReminder)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_create)
        }

        viewModel.remindersWithCategory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            if (items.isEmpty()) {
                recyclerView.visibility = android.view.View.GONE
                layoutEmpty.visibility = android.view.View.VISIBLE
            } else {
                recyclerView.visibility = android.view.View.VISIBLE
                layoutEmpty.visibility = android.view.View.GONE
            }
        }
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
}
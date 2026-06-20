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

        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        adapter = ReminderAdapter(
            onClick = { reminder ->
                val action = ReminderListFragmentDirections
                    .actionListToDetail(reminder.id)
                findNavController().navigate(action)
            },
            onLongClick = { reminder ->
                showContextMenu(reminder)
            }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReminders)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddReminder)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_create)
        }

        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
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
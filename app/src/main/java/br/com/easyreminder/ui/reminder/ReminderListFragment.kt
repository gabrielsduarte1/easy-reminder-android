package br.com.easyreminder.ui.reminder

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.easyreminder.R
import br.com.easyreminder.databinding.FragmentReminderListBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderListFragment : Fragment() {

    private val viewModel: ReminderViewModel by viewModels()
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

        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.settingsFragment -> {
                        findNavController().navigate(R.id.action_list_to_settings)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = ReminderAdapter(
            onClick = { item ->
                val action = ReminderListFragmentDirections
                    .actionListToDetail(item.id)
                findNavController().navigate(action)
            },
            onLongClick = { item ->
                showContextMenu(item)
            }
        )

        binding.recyclerViewReminders.adapter = adapter
        binding.recyclerViewReminders.layoutManager = LinearLayoutManager(requireContext())

        binding.fabAddReminder.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_create)
        }

        viewModel.allReminders.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            if (items.isEmpty()) {
                binding.recyclerViewReminders.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerViewReminders.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
        }

        setupSwipeGestures()

        childFragmentManager.setFragmentResultListener(
            ReminderOptionsDialogFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            val reminderId = result.getInt(ReminderOptionsDialogFragment.RESULT_REMINDER_ID)

            when (result.getString(ReminderOptionsDialogFragment.RESULT_ACTION)) {
                ReminderOptionsDialogFragment.ACTION_EDIT -> {
                    val action = ReminderListFragmentDirections.actionListToDetail(reminderId)
                    findNavController().navigate(action)
                }
                ReminderOptionsDialogFragment.ACTION_DELETE -> {
                    val reminder = adapter.currentList.firstOrNull { it.id == reminderId }
                    reminder?.let { confirmDelete(it) }
                }
            }
        }
    }

    private fun setupSwipeGestures() {
        val callback = SwipeToActionCallback(
            context = requireContext(),
            onSwipeLeft = { position -> deleteWithUndo(adapter.currentList[position]) },
            onSwipeRight = { position -> toggleCompleted(adapter.currentList[position]) }
        )

        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerViewReminders)
    }

    private fun showContextMenu(reminder: Reminder) {
        ReminderOptionsDialogFragment
            .newInstance(reminder.title, reminder.id)
            .show(childFragmentManager, ReminderOptionsDialogFragment.TAG)
    }

    private fun confirmDelete(reminder: Reminder) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir lembrete")
            .setMessage("Deseja excluir \"${reminder.title}\"?")
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.delete(reminder)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteWithUndo(reminder: Reminder) {
        viewModel.delete(reminder)
        ReminderScheduler.cancel(requireContext(), reminder.id)

        Snackbar.make(
            requireView(),
            "Lembrete excluído",
            Snackbar.LENGTH_LONG
        ).setAction("DESFAZER") {
            viewModel.insert(reminder)
            ReminderScheduler.schedule(requireContext(), reminder)
        }.show()
    }

    private fun toggleCompleted(reminder: Reminder) {
        val updated = reminder.copy(isCompleted = !reminder.isCompleted)
        viewModel.update(updated)

        if (updated.isCompleted) {
            ReminderScheduler.cancel(requireContext(), updated.id)
        } else if (!updated.dateTime.isNullOrBlank()) {
            ReminderScheduler.schedule(requireContext(), updated)
        }

        val message = if (updated.isCompleted) "Lembrete concluído" else "Lembrete pendente novamente"

        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).setAction("DESFAZER") {
            viewModel.update(reminder)
            if (reminder.isCompleted) {
                ReminderScheduler.cancel(requireContext(), reminder.id)
            } else if (!reminder.dateTime.isNullOrBlank()) {
                ReminderScheduler.schedule(requireContext(), reminder)
            }
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
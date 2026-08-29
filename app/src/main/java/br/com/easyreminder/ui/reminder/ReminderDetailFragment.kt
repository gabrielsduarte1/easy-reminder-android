package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.databinding.FragmentReminderDetailBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.model.ReminderCategory
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderDetailFragment : Fragment() {

    private val reminderViewModel: ReminderViewModel by viewModels()
    private var reminderId: Int = -1
    private var currentReminder: Reminder? = null
    private var selectedCategory: String? = null

    private var _binding: FragmentReminderDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderId = arguments?.getInt("reminderId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipGroupCategory.removeAllViews()
        ReminderCategory.values().forEach { category ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = category.displayName
                chipIcon = androidx.core.content.ContextCompat.getDrawable(requireContext(), category.iconRes)
                isChipIconVisible = true
                isCheckable = true
                isChecked = category.name == selectedCategory

                setOnClickListener {
                    selectedCategory = if (isChecked) category.name else null
                }
            }
            binding.chipGroupCategory.addView(chip)
        }

        reminderViewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            val item = reminders.find { it.id == reminderId }
            item?.let {
                currentReminder = it
                binding.editTextTitle.setText(it.title)
                binding.editTextDescription.setText(it.description)
                selectedCategory = it.category

                for (i in 0 until binding.chipGroupCategory.childCount) {
                    val chip = binding.chipGroupCategory.getChildAt(i) as com.google.android.material.chip.Chip
                    chip.isChecked = chip.text == ReminderCategory.values()
                        .find { cat -> cat.name == it.category }?.displayName
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()

            if (title.isEmpty()) {
                binding.tilTitle.error = "O título é obrigatório"
                return@setOnClickListener
            }

            binding.tilTitle.error = null

            val updated = currentReminder?.copy(
                title = title,
                description = binding.editTextDescription.text.toString().trim(),
                category = selectedCategory
            )

            updated?.let {
                reminderViewModel.update(it)
                ReminderScheduler.cancel(requireContext(), it.id)
                ReminderScheduler.schedule(requireContext(), it)
                findNavController().navigateUp()
            }
        }

        binding.buttonDelete.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Excluir lembrete")
                .setMessage("Deseja excluir \"${currentReminder?.title}\"?")
                .setPositiveButton("Excluir") { _, _ ->
                    currentReminder?.let {
                        reminderViewModel.delete(it)
                        ReminderScheduler.cancel(requireContext(), it.id)
                        findNavController().navigateUp()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
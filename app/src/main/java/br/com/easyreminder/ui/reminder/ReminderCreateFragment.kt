package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.databinding.FragmentReminderCreateBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.model.ReminderCategory
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import br.com.easyreminder.util.showDatePicker
import br.com.easyreminder.util.showTimePicker
import br.com.easyreminder.util.showToast
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ReminderCreateFragment : Fragment() {

    private val reminderViewModel: ReminderViewModel by viewModels()

    private var _binding: FragmentReminderCreateBinding? = null
    private val binding get() = _binding!!

    private var reminderId: Int = -1
    private var currentReminder: Reminder? = null
    private var selectedCategory: String? = null
    private var prefilled = false

    private val isEditing: Boolean
        get() = reminderId != -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderId = arguments?.getInt("reminderId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextTitle.doOnTextChanged { text, _, _, _ ->
            binding.buttonSave.isEnabled = !text.isNullOrBlank()
        }

        binding.editTextDateTime.setOnClickListener {
            requireContext().showDatePicker { year, month, day ->
                requireContext().showTimePicker { hour, minute ->
                    val selectedDateTime = Calendar.getInstance().apply {
                        set(year, month, day, hour, minute, 0)
                    }

                    if (selectedDateTime.before(Calendar.getInstance())) {
                        requireContext().showToast("Escolha um horário que ainda não passou")
                        return@showTimePicker
                    }

                    val dateTime = String.format(
                        "%02d/%02d/%04d %02d:%02d",
                        day, month + 1, year, hour, minute
                    )
                    binding.editTextDateTime.setText(dateTime)
                }
            }
        }

        binding.chipGroupCategory.removeAllViews()
        ReminderCategory.values().forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category.displayName
                tag = category.name
                chipIcon = ContextCompat.getDrawable(requireContext(), category.iconRes)
                isChipIconVisible = true
                isCheckable = true

                setOnClickListener {
                    selectedCategory = if (isChecked) category.name else null
                }
            }
            binding.chipGroupCategory.addView(chip)
        }

        if (isEditing) {
            setupEditMode()
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val dateTime = binding.editTextDateTime.text.toString().trim().ifEmpty { null }

            if (isEditing) {
                val updated = currentReminder?.copy(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    dateTime = dateTime
                )

                updated?.let {
                    reminderViewModel.update(it)
                    ReminderScheduler.cancel(requireContext(), it.id)
                    if (!it.isCompleted) {
                        ReminderScheduler.schedule(requireContext(), it)
                    }
                    findNavController().navigateUp()
                }
            } else {
                val reminder = Reminder(
                    title = title,
                    description = description,
                    category = selectedCategory,
                    dateTime = dateTime
                )

                reminderViewModel.insert(reminder)
                ReminderScheduler.schedule(requireContext(), reminder)
                findNavController().navigateUp()
            }
        }
    }

    private fun setupEditMode() {
        binding.buttonSave.text = "Salvar alterações"
        binding.buttonDelete.visibility = View.VISIBLE

        reminderViewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            val reminder = reminders.find { it.id == reminderId } ?: return@observe
            currentReminder = reminder

            if (prefilled) return@observe
            prefilled = true

            binding.editTextTitle.setText(reminder.title)
            binding.editTextDescription.setText(reminder.description)
            binding.editTextDateTime.setText(reminder.dateTime.orEmpty())
            selectedCategory = reminder.category

            for (i in 0 until binding.chipGroupCategory.childCount) {
                val chip = binding.chipGroupCategory.getChildAt(i) as Chip
                chip.isChecked = chip.tag == reminder.category
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
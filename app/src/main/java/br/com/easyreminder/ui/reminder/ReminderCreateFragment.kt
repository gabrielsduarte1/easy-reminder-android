package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.databinding.FragmentReminderCreateBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.viewmodel.CategoryViewModel
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderCreateFragment : Fragment() {

    private val reminderViewModel: ReminderViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private var _binding: FragmentReminderCreateBinding? = null
    private val binding get() = _binding!!

    private var selectedCategoryId: Int? = null

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
            val calendar = java.util.Calendar.getInstance()

            val datePickerDialog = android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    android.app.TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            val selectedDateTime = java.util.Calendar.getInstance().apply {
                                set(year, month, day, hour, minute, 0)
                            }

                            if (selectedDateTime.before(java.util.Calendar.getInstance())) {
                                android.widget.Toast.makeText(
                                    requireContext(),
                                    "Escolha um horário que ainda não passou",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@TimePickerDialog
                            }

                            val dateTime = String.format(
                                "%02d/%02d/%04d %02d:%02d",
                                day, month + 1, year, hour, minute
                            )
                            binding.editTextDateTime.setText(dateTime)
                        },
                        calendar.get(java.util.Calendar.HOUR_OF_DAY),
                        calendar.get(java.util.Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

            datePickerDialog.show()
        }

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            binding.autoCompleteCategory.setAdapter(adapter)
            binding.autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].id
            }
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.tilTitle.error = "O título é obrigatório"
                return@setOnClickListener
            }

            binding.tilTitle.error = null

            val reminder = Reminder(
                title = title,
                description = description,
                categoryId = selectedCategoryId,
                dateTime = binding.editTextDateTime.text.toString().trim().ifEmpty { null }
            )

            reminderViewModel.insert(reminder)
            ReminderScheduler.schedule(requireContext(), reminder)
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
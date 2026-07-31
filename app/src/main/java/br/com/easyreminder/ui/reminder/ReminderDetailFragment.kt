package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.databinding.FragmentReminderDetailBinding
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.viewmodel.CategoryViewModel
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler

class ReminderDetailFragment : Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var reminderId: Int = -1
    private var currentReminder: Reminder? = null

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

        reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        var selectedCategoryId: Int? = null

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames)
            binding.autoCompleteCategory.setAdapter(adapter)

            binding.autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].id
            }
        }

        reminderViewModel.remindersWithCategory.observe(viewLifecycleOwner) { items ->
            val item = items.find { it.reminder.id == reminderId }
            item?.let {
                currentReminder = it.reminder
                binding.editTextTitle.setText(it.reminder.title)
                binding.editTextDescription.setText(it.reminder.description)
                selectedCategoryId = it.reminder.categoryId
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
                categoryId = selectedCategoryId
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
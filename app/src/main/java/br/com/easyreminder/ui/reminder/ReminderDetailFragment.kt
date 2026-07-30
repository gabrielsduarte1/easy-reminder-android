package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.R
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.ui.components.ComponentButton
import br.com.easyreminder.viewmodel.CategoryViewModel
import br.com.easyreminder.viewmodel.ReminderViewModel
import br.com.easyreminder.worker.ReminderScheduler
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReminderDetailFragment : Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var reminderId: Int = -1
    private var currentReminder: Reminder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderId = arguments?.getInt("reminderId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val tilTitle = view.findViewById<TextInputLayout>(R.id.tilTitle)
        val editTextTitle = view.findViewById<TextInputEditText>(R.id.editTextTitle)
        val editTextDescription = view.findViewById<TextInputEditText>(R.id.editTextDescription)
        val autoCompleteCategory = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategory)
        val buttonSave = view.findViewById<ComponentButton>(R.id.buttonSave)
        val buttonDelete = view.findViewById<MaterialButton>(R.id.buttonDelete)

        var selectedCategoryId: Int? = null

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames)
            autoCompleteCategory.setAdapter(adapter)

            autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].id
            }
        }

        reminderViewModel.remindersWithCategory.observe(viewLifecycleOwner) { items ->
            val item = items.find { it.reminder.id == reminderId }
            item?.let {
                currentReminder = it.reminder
                editTextTitle.setText(it.reminder.title)
                editTextDescription.setText(it.reminder.description)
                selectedCategoryId = it.reminder.categoryId
            }
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()

            if (title.isEmpty()) {
                tilTitle.error = "O título é obrigatório"
                return@setOnClickListener
            }

            tilTitle.error = null

            val updated = currentReminder?.copy(
                title = title,
                description = editTextDescription.text.toString().trim(),
                categoryId = selectedCategoryId
            )

            updated?.let {
                reminderViewModel.update(it)
                ReminderScheduler.cancel(requireContext(), it.id)
                ReminderScheduler.schedule(requireContext(), it)
                findNavController().navigateUp()
            }
        }

        buttonDelete.setOnClickListener {
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
}
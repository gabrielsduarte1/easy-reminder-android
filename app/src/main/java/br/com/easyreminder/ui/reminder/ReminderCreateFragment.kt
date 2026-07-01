package br.com.easyreminder.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.easyreminder.R
import br.com.easyreminder.model.Reminder
import br.com.easyreminder.viewmodel.CategoryViewModel
import br.com.easyreminder.viewmodel.ReminderViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ReminderCreateFragment : Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var tilTitle: TextInputLayout
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var autoCompleteCategory: AutoCompleteTextView
    private lateinit var buttonSave: MaterialButton

    private var selectedCategoryId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminderViewModel = ViewModelProvider(this)[ReminderViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        tilTitle = view.findViewById(R.id.tilTitle)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory)
        buttonSave = view.findViewById(R.id.buttonSave)

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            autoCompleteCategory.setAdapter(adapter)
            autoCompleteCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].id
            }
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (title.isEmpty()) {
                tilTitle.error = "O título é obrigatório"
                return@setOnClickListener
            }

            tilTitle.error = null

            val reminder = Reminder(
                title = title,
                description = description,
                categoryId = selectedCategoryId
            )

            reminderViewModel.insert(reminder)
            findNavController().navigateUp()
        }
    }
}
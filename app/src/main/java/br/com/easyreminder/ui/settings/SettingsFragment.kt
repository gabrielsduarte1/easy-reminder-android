package br.com.easyreminder.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.easyreminder.R
import br.com.easyreminder.model.Category
import br.com.easyreminder.viewmodel.CategoryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = CategoryAdapter { category ->
            showEditCategoryDialog(category)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewCategories)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        val buttonAddCategory = view.findViewById<MaterialButton>(R.id.buttonAddCategory)
        buttonAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        val switchDarkTheme = view.findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        switchDarkTheme.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Nome da categoria"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Nova categoria")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    val colors = listOf("#FF5252", "#448AFF", "#69F0AE", "#FFD740", "#E040FB")
                    val randomColor = colors.random()
                    categoryViewModel.insert(Category(name = name, color = randomColor))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val editText = EditText(requireContext())
        editText.setText(category.name)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Editar categoria")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    categoryViewModel.update(category.copy(name = name))
                }
            }
            .setNeutralButton("Excluir") { _, _ ->
                categoryViewModel.delete(category)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
package br.com.easyreminder.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.easyreminder.databinding.FragmentSettingsBinding
import br.com.easyreminder.model.Category
import br.com.easyreminder.viewmodel.CategoryViewModel

class SettingsFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = CategoryAdapter { category ->
            showEditCategoryDialog(category)
        }

        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())

        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        binding.buttonAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        val currentMode = AppCompatDelegate.getDefaultNightMode()
        binding.switchDarkTheme.isChecked = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            requireContext()
                .getSharedPreferences("settings", android.content.Context.MODE_PRIVATE)
                .edit()
                .putBoolean("dark_mode", isChecked)
                .apply()
        }
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Nome da categoria"

        var selectedColor = "#FF5252"

        val colors = listOf(
            "#FF5252", "#448AFF", "#69F0AE", "#FFD740",
            "#E040FB", "#FF6D00", "#F48FB1", "#4DD0E1"
        )

        val colorNames = listOf(
            "🔴 Vermelho",
            "🔵 Azul",
            "🟢 Verde",
            "🟡 Amarelo",
            "🟣 Roxo",
            "🟠 Laranja",
            "🩷 Rosa",
            "🩵 Ciano"
        ).toTypedArray()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Nova categoria")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    categoryViewModel.insert(Category(name = name, color = selectedColor))
                }
            }
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Escolher cor") { _, _ -> }
            .show()
            .also { dialog ->
                dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Escolha uma cor")
                        .setItems(colorNames) { _, index ->
                            selectedColor = colors[index]
                            dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL)
                                .setTextColor(android.graphics.Color.parseColor(selectedColor))
                        }
                        .show()
                }
            }
    }

    private fun showEditCategoryDialog(category: Category) {
        val editText = EditText(requireContext())
        editText.setText(category.name)

        var selectedColor = category.color

        val colors = listOf(
            "#FF5252", "#448AFF", "#69F0AE", "#FFD740",
            "#E040FB", "#FF6D00", "#F48FB1", "#4DD0E1"
        )

        val colorNames = listOf(
            "🔴 Vermelho",
            "🔵 Azul",
            "🟢 Verde",
            "🟡 Amarelo",
            "🟣 Roxo",
            "🟠 Laranja",
            "🩷 Rosa",
            "🩵 Ciano"
        ).toTypedArray()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Editar categoria")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    categoryViewModel.update(category.copy(name = name, color = selectedColor))
                }
            }
            .setNeutralButton("Escolher cor") { _, _ -> }
            .setNegativeButton("Excluir") { _, _ ->
                categoryViewModel.delete(category)
            }
            .show()
            .also { dialog ->
                dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL)
                    .setTextColor(android.graphics.Color.parseColor(selectedColor))
                dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Escolha uma cor")
                        .setItems(colorNames) { _, index ->
                            selectedColor = colors[index]
                            dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL)
                                .setTextColor(android.graphics.Color.parseColor(selectedColor))
                        }
                        .show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
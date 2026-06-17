package br.com.easyreminder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import br.com.easyreminder.data.local.AppDatabase
import br.com.easyreminder.data.repository.CategoryRepository
import br.com.easyreminder.model.Category
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>

    init {
        val dao = AppDatabase.getInstance(application).categoryDao()
        repository = CategoryRepository(dao)
        allCategories = repository.allCategories
    }

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }
}
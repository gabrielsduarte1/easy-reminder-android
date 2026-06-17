package br.com.easyreminder.data.repository

import androidx.lifecycle.LiveData
import br.com.easyreminder.data.local.CategoryDao
import br.com.easyreminder.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: LiveData<List<Category>> = categoryDao.getAll()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }
}
package br.com.easyreminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.easyreminder.data.local.AppDatabase
import br.com.easyreminder.data.local.CategoryDao
import br.com.easyreminder.model.Category
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: CategoryDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.categoryDao()
    }

    @Test
    fun inserirCategoriaDeveRetornarNaLista() = runTest {
        val category = Category(name = "Trabalho", color = "#FF5252")
        dao.insert(category)

        val categories = mutableListOf<Category>()
        dao.getAll().observeForever { categories.addAll(it) }

        assertTrue(categories.any { it.name == "Trabalho" })
    }

    @Test
    fun deletarCategoriaDeveRemoverDoBanco() = runTest {
        val category = Category(name = "Pessoal", color = "#448AFF")
        dao.insert(category)

        var categories = mutableListOf<Category>()
        dao.getAll().observeForever { categories = it.toMutableList() }

        val inserted = categories.first { it.name == "Pessoal" }
        dao.delete(inserted)

        dao.getAll().observeForever { categories = it.toMutableList() }
        assertTrue(categories.none { it.name == "Pessoal" })
    }

    @Test
    fun atualizarCategoriaDeveMudarNomeECor() = runTest {
        val category = Category(name = "Estudos", color = "#69F0AE")
        dao.insert(category)

        var categories = mutableListOf<Category>()
        dao.getAll().observeForever { categories = it.toMutableList() }

        val inserted = categories.first { it.name == "Estudos" }
        dao.update(inserted.copy(name = "Estudos e Cursos", color = "#FFD740"))

        dao.getAll().observeForever { categories = it.toMutableList() }
        assertTrue(categories.any { it.name == "Estudos e Cursos" && it.color == "#FFD740" })
    }

    @Test
    fun bancoVazioDeveRetornarListaVazia() = runTest {
        val categories = mutableListOf<Category>()
        dao.getAll().observeForever { categories.addAll(it) }
        assertTrue(categories.isEmpty())
    }

    @Test
    fun listaDeveEstarOrdenadaPorNomeAlfabeticamente() = runTest {
        dao.insert(Category(name = "Zebra", color = "#FF5252"))
        dao.insert(Category(name = "Abelha", color = "#448AFF"))
        dao.insert(Category(name = "Morango", color = "#69F0AE"))

        var categories = mutableListOf<Category>()
        dao.getAll().observeForever { categories = it.toMutableList() }

        val names = categories.map { it.name }
        assertTrue(names == listOf("Abelha", "Morango", "Zebra"))
    }
    }
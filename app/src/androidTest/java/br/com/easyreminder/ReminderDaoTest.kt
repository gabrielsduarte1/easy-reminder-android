package br.com.easyreminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.easyreminder.data.local.AppDatabase
import br.com.easyreminder.data.local.ReminderDao
import br.com.easyreminder.model.Reminder
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: ReminderDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.reminderDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun inserirLembreteDeveRetornarNaLista() = runTest {
        val reminder = Reminder(title = "Estudar Kotlin")
        dao.insert(reminder)

        val reminders = mutableListOf<Reminder>()
        dao.getAll().observeForever { reminders.addAll(it) }

        assertTrue(reminders.any { it.title == "Estudar Kotlin" })
    }

    @Test
    fun deletarLembreteDeveRemoverDoBanco() = runTest {
        val reminder = Reminder(title = "Academia")
        dao.insert(reminder)

        var reminders = mutableListOf<Reminder>()
        dao.getAll().observeForever { reminders = it.toMutableList() }

        val inserted = reminders.first { it.title == "Academia" }
        dao.delete(inserted)

        dao.getAll().observeForever { reminders = it.toMutableList() }
        assertTrue(reminders.none { it.title == "Academia" })
    }

    @Test
    fun atualizarLembreteDeveMudarTitulo() = runTest {
        val reminder = Reminder(title = "Reuniao")
        dao.insert(reminder)

        var reminders = mutableListOf<Reminder>()
        dao.getAll().observeForever { reminders = it.toMutableList() }

        val inserted = reminders.first { it.title == "Reuniao" }
        dao.update(inserted.copy(title = "Reunião com cliente"))

        dao.getAll().observeForever { reminders = it.toMutableList() }
        assertTrue(reminders.any { it.title == "Reunião com cliente" })
    }

    @Test
    fun bancoVazioDeveRetornarListaVazia() = runTest {
        val reminders = mutableListOf<Reminder>()
        dao.getAll().observeForever { reminders.addAll(it) }
        assertTrue(reminders.isEmpty())
    }
}
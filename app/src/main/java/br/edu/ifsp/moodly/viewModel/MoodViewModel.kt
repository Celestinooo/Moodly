package br.edu.ifsp.moodly.viewModel
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import br.edu.ifsp.moodly.controller.MoodDatabaseHelper
import br.edu.ifsp.moodly.model.MoodEntry
import kotlinx.coroutines.*

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = MoodDatabaseHelper(application)

    private val _moods = MutableLiveData<List<MoodEntry>>()
    val moods: LiveData<List<MoodEntry>> get() = _moods

    private val viewModelJob = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    init {
        loadMoods()
    }

    fun loadMoods() {
        ioScope.launch {
            val list = dbHelper.getAllMoods()
            withContext(Dispatchers.Main) {
                _moods.value = list
            }
        }
    }

    fun addMood(entry: MoodEntry) {
        ioScope.launch {
            dbHelper.insertMood(entry)
            loadMoods()
        }
    }

    fun updateMood(entry: MoodEntry) {
        ioScope.launch {
            dbHelper.updateMood(entry)
            loadMoods()
        }
    }

    fun deleteMood(id: Long) {
        ioScope.launch {
            dbHelper.deleteMood(id)
            loadMoods()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

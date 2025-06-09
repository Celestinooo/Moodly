package br.edu.ifsp.moodly.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.moodly.R
import br.edu.ifsp.moodly.databinding.ActivityMainBinding
import br.edu.ifsp.moodly.model.MoodEntry
import br.edu.ifsp.moodly.model.MoodState
import br.edu.ifsp.moodly.viewModel.MoodViewModel
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MoodViewModel by viewModels()

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialMock()

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HistoryFragment())
            .commit()

        supportFragmentManager.addOnBackStackChangedListener {
            updateAddButtonVisibility()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.e("Menu","Options selected")
        return when (item.itemId) {
            R.id.action_add -> {
                Log.e("Menu","Options selected add")
                val editFragment = MoodEditFragment.newInstance(null)
                editFragment.onSave = { entry ->
                    viewModel.addMood(entry)
                }
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, editFragment)
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateAddButtonVisibility() {
        val isHistoryVisible = supportFragmentManager.findFragmentById(R.id.fragmentContainer) is HistoryFragment
        menu?.findItem(R.id.action_add)?.isVisible = isHistoryVisible
    }

    private fun initialMock() {
        val todayDate: String = java.time.LocalDate.now().toString()
        val todayDateFormatted: String = todayDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModel.addMood(MoodEntry(
            date = todayDateFormatted,
            mood = MoodState.BEM,
            note = "Estou bem hoje"
        ))
    }

}
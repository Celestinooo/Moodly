package br.edu.ifsp.moodly.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import br.edu.ifsp.moodly.R
import br.edu.ifsp.moodly.databinding.FragmentMoodEditBinding
import br.edu.ifsp.moodly.model.MoodEntry
import br.edu.ifsp.moodly.model.MoodState

class MoodEditFragment : Fragment() {

    private var _binding: FragmentMoodEditBinding? = null
    private val binding get() = _binding!!

    private var moodEntry: MoodEntry? = null

    var onSave: ((MoodEntry) -> Unit)? = null

    companion object {
        private const val ARG_MOOD = "arg_mood"

        fun newInstance(moodEntry: MoodEntry?): MoodEditFragment {
            val fragment = MoodEditFragment()
            moodEntry?.let {
                val bundle = Bundle()
                bundle.putSerializable(ARG_MOOD, it)
                fragment.arguments = bundle
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moodEntry = arguments?.getSerializable(ARG_MOOD) as? MoodEntry
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moods = MoodState.entries.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item_white, moods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerMoods.adapter = adapter
        if(moodEntry == null) {
            binding.textHeader.text = "Adicionar Mood"
            binding.spinnerMoods.setSelection(moods.size - 1)
        } else {
            binding.textHeader.text = "Editar Mood"
        }

        moodEntry?.let {
            binding.spinnerMoods.setSelection(MoodState.entries.indexOf(it.mood))
            binding.editNote.setText(it.note)
        }

        binding.buttonSave.setOnClickListener {
            val selectedMood = MoodState.valueOf(binding.spinnerMoods.selectedItem as String)
            val note = binding.editNote.text.toString()
            val date = moodEntry?.date ?: java.time.LocalDate.now().toString()
            val id = moodEntry?.id ?: 0

            val entry = MoodEntry(id, date, selectedMood, note)
            onSave?.invoke(entry)
            parentFragmentManager.popBackStack()
        }

        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
                v.clearFocus()
            }
            false
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

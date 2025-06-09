package br.edu.ifsp.moodly.view
import android.R
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.moodly.databinding.FragmentHistoryBinding
import br.edu.ifsp.moodly.viewModel.MoodViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MoodAdapter
    private val viewModel: MoodViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MoodAdapter(
            onEditClick = {
                val editFragment = MoodEditFragment.newInstance(it)
                editFragment.onSave = { entry ->
                    viewModel.updateMood(entry)
                }
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                    .replace(br.edu.ifsp.moodly.R.id.fragmentContainer, editFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = {
                showDeleteConfirmation {
                    viewModel.deleteMood(it.id)
                }
            },
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.moods.observe(viewLifecycleOwner) { moods ->
            adapter.updateItems(moods)
        }
    }

    private fun showDeleteConfirmation(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Tem certeza que deseja deletar este item?")
            .setPositiveButton("Deletar") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package br.edu.ifsp.moodly.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.moodly.databinding.MoodItemBinding
import br.edu.ifsp.moodly.model.MoodEntry

class MoodAdapter(
    private val onDeleteClick: (MoodEntry) -> Unit,
    private val onEditClick: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    private val items = mutableListOf<MoodEntry>()

    inner class MoodViewHolder(val binding: MoodItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = MoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            textDate.text = item.date
            textMood.text = "Mood do dia: ${item.mood}"
            textNote.text = item.note
            buttonDelete.setOnClickListener { onDeleteClick(item) }
            buttonEdit.setOnClickListener { onEditClick(item) }
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<MoodEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

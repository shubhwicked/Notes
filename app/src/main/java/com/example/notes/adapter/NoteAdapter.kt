package com.example.notes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.notes.databinding.ItemNotesBinding
import com.example.notes.model.NoteModel
import java.util.*
import kotlin.collections.ArrayList


class NoteAdapter(private val listener: NotesItemClickListener) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val allNotes = ArrayList<NoteModel>()

    private var lastPosition = -1
    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
                       //to make duration random number between [0,501)
            YoYo.with(Techniques.Pulse).duration(Random().nextInt(501).toLong()).playOn(viewToAnimate)
            lastPosition = position
        }
    }

    inner class NoteViewHolder(private val binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val curNote = allNotes[position]
            binding.text.text = curNote.text
            binding.title.text = curNote.title

            binding.editNoteLayout.setOnClickListener {
                listener.onItemClicked("edit", allNotes[position])
            }

            binding.deleteNoteLayout.setOnClickListener {
                listener.onItemClicked("delete", allNotes[position])
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(position)
        // call Animation function
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }


    fun updateList(newList: List<NoteModel>) {
        allNotes.clear()
        allNotes.addAll(newList)

        notifyDataSetChanged()
    }
}

interface NotesItemClickListener {
    fun onItemClicked(buttonID: String, note: NoteModel)
}
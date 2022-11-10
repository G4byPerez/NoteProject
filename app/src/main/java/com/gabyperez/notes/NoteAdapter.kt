package com.gabyperez.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.model.Note

class NoteAdapter (var notes: List<Note>): RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        var noteType : TextView
        var description : TextView
        init{
            noteType = v.findViewById(R.id.txtTipo)
            description = v.findViewById(R.id.txtDescripcion)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)

        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = notes[position]
        holder.noteType.text = "Nota"
        holder.description.text = p.title
    }


    override fun getItemCount(): Int {
        return notes.size
    }

}
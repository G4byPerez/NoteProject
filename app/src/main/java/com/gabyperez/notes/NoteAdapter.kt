package com.gabyperez.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.model.Note

class NoteAdapter (var notes: List<Note>): RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        var noteType : TextView
        var title : TextView
        var delete: ImageView
        var edit: ImageView

        init{
            noteType = v.findViewById(R.id.txtTipo)
            title = v.findViewById(R.id.txtDescripcion)
            delete = v.findViewById(R.id.delete)
            edit = v.findViewById(R.id.edit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)

        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = notes[position]
        holder.noteType.text = "Nota"
        holder.title.text = p.title

        //delete
        holder.delete.setOnClickListener {
            NoteDatabase.getDatabase(holder.title.context).NoteDao().deleteNote(p)
            val notes  = NoteDatabase.getDatabase(holder.title.context).NoteDao().getAllNotes()
            this.notes = notes
            this.notifyItemRemoved(position)
        }

        //edit
        holder.edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id",p.id.toString())
            bundle.putString("title", p.title)
            bundle.putString("description", p.description)
            bundle.putString("type", p.type.toString())
            it.findNavController().navigate(R.id.action_home2_to_createNote,bundle)
        }
    }


    override fun getItemCount(): Int {
        return notes.size
    }

}
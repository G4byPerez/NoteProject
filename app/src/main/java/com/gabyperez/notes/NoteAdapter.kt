package com.gabyperez.notes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.model.Note
import java.util.*

class NoteAdapter ( var notes: List<Note>): RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        var noteType : TextView
        var title : TextView
        var description: TextView
        var delete: ImageView
        var edit: ImageView
        var card: CardView

        init{
            noteType = v.findViewById(R.id.txtTipo)
            title = v.findViewById(R.id.txtTitle)
            description = v.findViewById(R.id.txtDescripcion)
            delete = v.findViewById(R.id.delete)
            edit = v.findViewById(R.id.edit)
            card = v.findViewById(R.id.cardView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(v)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = notes[position]

        holder.title.text = p.title
        holder.description.text = p.description

        if (p.type == 1){
            holder.noteType.text = "Nota"
            holder.card.setCardBackgroundColor(Color.parseColor("#DBF4FF"))
        } else {
            holder.noteType.text = "Tarea"
            //holder.card.setCardBackgroundColor(Color.parseColor("#FDDBE7"))
        }

        //delete
        holder.delete.setOnClickListener {
            NoteDatabase.getDatabase(holder.title.context).NoteDao().deleteNote(p)
            val notes  = NoteDatabase.getDatabase(holder.title.context).NoteDao().getAll()
            this.notes = notes
            this.notifyItemRemoved(position)
        }

        //edit
        holder.card.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id",p.id.toString())
            bundle.putString("title", p.title)
            bundle.putString("description", p.description)
            bundle.putString("type", p.type.toString())
            bundle.putString("dateEnd", p.dateEnd)
            bundle.putString("hourEnd", p.hourEnd)
            //Navigation
            if (p.type == 1){
                it.findNavController().navigate(R.id.action_home2_to_createNote,bundle)
            } else {
                it.findNavController().navigate(R.id.action_home2_to_createTask,bundle)
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

}


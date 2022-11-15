package com.gabyperez.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.model.Note
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class Home : Fragment() {

    lateinit var notes : List<Note>
    lateinit var grupoBotones : FloatingActionsMenu
    lateinit var note : com.getbase.floatingactionbutton.FloatingActionButton
    lateinit var task: com.getbase.floatingactionbutton.FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.listNotes)

        //view note
        lifecycleScope.launch {
            notes = NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao()
                .getAllNotes()
        }

        rv.adapter = NoteAdapter(notes)
        rv.layoutManager = LinearLayoutManager(this@Home.requireContext())

        grupoBotones=root.findViewById(R.id.grupoFlotante)
        note = root.findViewById(R.id.btnNota)
        task = root.findViewById(R.id.btnTarea)
        //Navigation
        note.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_home2_to_createNote)
        }

        //Navigation
        task.setOnClickListener {
            //no cambie a la pantalla que corresponde
            view?.findNavController()?.navigate(R.id.action_home2_to_createTask)
        }

        return root.rootView
    }
}
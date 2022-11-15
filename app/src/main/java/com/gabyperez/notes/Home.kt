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
import kotlinx.coroutines.launch

class Home : Fragment() {

    lateinit var notes : List<Note>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.listNotes)

        //view note
        lifecycleScope.launch {
            notes = NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
        }

        rv.adapter = NoteAdapter(notes)
        rv.layoutManager = LinearLayoutManager(this@Home.requireContext())

        //Navigation
        root.findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.btnNota).setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_home2_to_createNote)
        }

        root.findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.btnTarea).setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_home2_to_createTask)
        }

        return root.rootView
    }
}
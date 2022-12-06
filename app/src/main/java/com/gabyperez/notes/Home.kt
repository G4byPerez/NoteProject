package com.gabyperez.notes

import android.content.ClipData.Item
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
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
            notes =
                NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
        }

        rv.adapter = NoteAdapter(notes)
        rv.layoutManager = LinearLayoutManager(this@Home.requireContext())

        //search
        var search = root.findViewById<EditText>(R.id.search)
        root.findViewById<EditText>(R.id.search).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch{
                    if(search.text.toString().length>0){
                        var filtro = "%"+search.text.toString().toUpperCase()+"%"
                        notes =  NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getByTitleDescription(filtro,filtro,2)
                        rv.adapter = NoteAdapter(notes)
                        rv.adapter!!.notifyDataSetChanged()
                        NoteAdapter(notes).notifyDataSetChanged()

                    }else{
                        notes =  NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAllNotes()
                        rv.adapter = NoteAdapter(notes)
                        rv.adapter!!.notifyDataSetChanged()
                      NoteAdapter(notes).notifyDataSetChanged()

                    }


                }
            }
        })

        //Navigation
        root.findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.btnNota)
            .setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_home2_to_createNote)
            }

        root.findViewById<com.getbase.floatingactionbutton.FloatingActionButton>(R.id.btnTarea)
            .setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_home2_to_createTask)
            }

        return root.rootView
    }
}
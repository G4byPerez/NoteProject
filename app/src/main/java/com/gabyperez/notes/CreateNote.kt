package com.gabyperez.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentCreateNoteBinding
import com.gabyperez.notes.model.Note
import kotlinx.coroutines.launch

class CreateNote : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCreateNoteBinding>(inflater,
            R.layout.fragment_create_note,container,false)

        //edit
        var id = -1
        if(arguments?.getString("title") != null) {
            binding.title.setText(arguments?.getString("title"))
            binding.description.setText(arguments?.getString("description"))
            id = arguments?.getString("id")!!.toInt()
        }

        binding.save.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.title.text.toString())
            bundle.putString("description", binding.description.text.toString())
            parentFragmentManager.setFragmentResult("key",bundle)

            //Insert
            lifecycleScope.launch{
                if (id == -1){
                    //insert new note --> type=1
                    val newNote = Note(
                        1,
                        binding.title.text.toString(),
                        binding.description.text.toString(),
                        "",
                        "",
                        "",
                        false)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().insert(newNote)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
                } else {
                    //update note
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().updateNote(binding.title.text.toString(),binding.description.text.toString(),id)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
                }

            }
            //Navigation
            it.findNavController().navigate(R.id.action_createNote_to_home2)
        }

        binding.btnCancel.setOnClickListener {
            //Navigation
            it.findNavController().navigate(R.id.action_createNote_to_home2)
        }

        return binding.root
    }
}
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

        binding.save.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.title.text.toString())
            bundle.putString("description", binding.description.text.toString())
            parentFragmentManager.setFragmentResult("key",bundle)

            //Insert
            lifecycleScope.launch{
                val newNote = Note( binding.title.text.toString(),binding.description.text.toString(),2,"","",false)
                NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().insert(newNote)
                NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAllNotes()
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
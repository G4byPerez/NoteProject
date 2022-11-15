package com.gabyperez.notes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentCreateTaskBinding
import com.gabyperez.notes.model.Note
import kotlinx.coroutines.launch

class CreateTask : Fragment() {

    private lateinit var fecha: EditText
    private lateinit var hora: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCreateTaskBinding>(inflater,
            R.layout.fragment_create_task,container,false)

        fecha = binding.txtDate
        hora = binding.txtHour

        //edit
        var id = -1
        if(arguments?.getString("title") != null) {
            binding.titleTask.setText(arguments?.getString("title"))
            binding.descriptionTask.setText(arguments?.getString("description"))
            id = arguments?.getString("id")!!.toInt()
        }

        //Navigation
        binding.cancelTask.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_home2)
        }

        binding.saveTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.titleTask.text.toString())
            bundle.putString("description", binding.descriptionTask.text.toString())
            parentFragmentManager.setFragmentResult("key",bundle)

            //Insert
            lifecycleScope.launch{
                if (id == -1){
                    //insert new task --> type=2
                    val newNote = Note(
                        2,
                        binding.titleTask.text.toString(),
                        binding.descriptionTask.text.toString(),
                        "",
                        "",
                        "",
                        false)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().insert(newNote)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
                } else {
                    //update note
                    //Change
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().updateNote(binding.titleTask.text.toString(),binding.descriptionTask.text.toString(),id)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
                }
            }
            //Navigation
            it.findNavController().navigate(R.id.action_createTask_to_home2)
        }

        //Date and hour
        binding.date.setOnClickListener {
            showDatePickerDialog()
        }

        binding.hour.setOnClickListener {
            showTimePikerDialog()
        }

        return binding.root
    }

    private fun showTimePikerDialog() {
        val newFragment = TimePicker {onTimeSelected(it)}
        //newFragment.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        hora.setText(time)
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker {day, month, year -> onDateSelected(day, month, year)}
        //newFragment.show(supportFragmentManager, "datePicker")
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        fecha.setText("$day/$month/$year")
    }
}
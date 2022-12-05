package com.gabyperez.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.databinding.FragmentPhotoBinding
import com.gabyperez.notes.databinding.FragmentRemindersBinding
import com.gabyperez.notes.model.Reminder


class fragment_Reminders : Fragment() {

    private lateinit var binding:FragmentRemindersBinding
    lateinit var title :String
    lateinit var description :String
    lateinit var newReminder: Reminder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRemindersBinding.inflate(layoutInflater)
        var id = -1
        var completed = false
        var tempListReminders: List<Reminder> = emptyList()
        val rv = binding.root?.findViewById<RecyclerView>(R.id.listReminders)
        var reminders: MutableList<Reminder> = mutableListOf<Reminder>()


        setFragmentResultListener("key") { requestKey, bundle ->
            title = bundle.getString("title").toString()
            description = bundle.getString("description").toString()

        }
        return binding.root
    }

    }
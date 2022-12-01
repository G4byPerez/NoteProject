package com.gabyperez.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.model.Multimedia
import kotlinx.coroutines.launch

class ViewMultimediaFragment : Fragment() {

    lateinit var multimedia: List<Multimedia>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_view_multimedia, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.listMultimedia)

        lifecycleScope.launch {
            val id =  arguments?.getString("id")!!.toInt()
            multimedia = NoteDatabase.getDatabase(requireActivity().applicationContext).MultimediaDao().getMultimedia(id)
        }

        rv.adapter = MultimediaAdapter(multimedia)
        rv.layoutManager = LinearLayoutManager(this@ViewMultimediaFragment.requireContext())

        return root.rootView
    }

}
package com.gabyperez.notes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentCreateTaskBinding
import com.gabyperez.notes.model.Note
import com.getbase.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*

class CreateTask : Fragment() {

    private lateinit var fecha: EditText
    private lateinit var hora: EditText
    private lateinit var btnAudio: FloatingActionButton

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCreateTaskBinding>(
            inflater,
            R.layout.fragment_create_task, container, false
        )

        fecha = binding.txtDate
        hora = binding.txtHour

        //edit
        var id = -1
        if (arguments?.getString("title") != null) {
            binding.titleTask.setText(arguments?.getString("title"))
            binding.descriptionTask.setText(arguments?.getString("description"))
            binding.txtDate.setText(arguments?.getString("dateEnd"))
            binding.txtHour.setText(arguments?.getString("hourEnd"))
            id = arguments?.getString("id")!!.toInt()
        }

        //Navigation
        binding.cancelTask.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_home2)
        }

        //Grupo de botones
        btnAudio = binding.btnAudioT
        btnAudio.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_audio)
        }



        binding.saveTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.titleTask.text.toString())
            bundle.putString("description", binding.descriptionTask.text.toString())
            parentFragmentManager.setFragmentResult("key", bundle)

            //Insert
            lifecycleScope.launch {
                if (id == -1) {
                    //insert new task --> type=2
                    val newTask = Note(
                        2,
                        binding.titleTask.text.toString(),
                        binding.descriptionTask.text.toString(),
                        "",
                        fecha.text.toString(),
                        hora.text.toString(),
                        false
                    )
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao()
                        .insert(newTask)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao()
                        .getAll()
                    //Channel
                    createNotificationChannel()
                    //Notification
                    scheduleNotificaction(binding.titleTask.text.toString())
                } else {
                    //update note
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao()
                        .updateTask(
                            binding.titleTask.text.toString(),
                            binding.descriptionTask.text.toString(),
                            fecha.text.toString(),
                            hora.text.toString(),
                            false,
                            id
                        )
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao()
                        .getAll()
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
        val newFragment = TimePicker { onTimeSelected(it) }
        activity?.let { newFragment.show(it.supportFragmentManager, "timePicker") }
    }

    private fun onTimeSelected(time: String) {
        hora.setText(time)
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker") }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        fecha.setText("$day/$month/$year")
    }

    // Notificacion
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificaction(titulo: String) {
        val intent = Intent(context, MiReceiverParaAlarma::class.java)
        val title = titulo
        val message = "Tienes esta tarea pendiente"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
       // val time = getTime()
       // val time = 180000
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            //time,
            180000,
            pendingIntent
        )
    }



    private fun getTime(): Long{
     val minute = hora.text.toString().substring(3,4).toInt()
        val hour = hora.text.toString().substring(0,1).toInt()
        val day = fecha.text.toString().substring(0,1).toInt()
        val month = fecha.text.toString().substring(3,4).toInt()
        val year = fecha.text.toString().substring(6,9).toInt()

        val calendar = Calendar.getInstance()
        calendar.set(2022,11, 29, 23, 0)
        return calendar.timeInMillis
    }

}
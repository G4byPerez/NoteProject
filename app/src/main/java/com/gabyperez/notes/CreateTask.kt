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
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentCreateTaskBinding
import com.gabyperez.notes.model.Note
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateTask : Fragment() {

    private lateinit var fecha: EditText
    private lateinit var hora: EditText

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

        var id = -1
        val bundle = Bundle()
        val currentTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //edit
        if(arguments?.getString("title") != null) {
            binding.titleTask.setText(arguments?.getString("title"))
            binding.descriptionTask.setText(arguments?.getString("description"))
            binding.txtDate.setText(arguments?.getString("dateEnd"))
            binding.txtHour.setText(arguments?.getString("hourEnd"))
            id = arguments?.getString("id")!!.toInt()
            bundle.putString("id", id.toString())
            val completed = arguments?.getInt("completed")

            binding.checkBox.isChecked = completed == 0
            if (binding.checkBox.isChecked) {
                binding.date.visibility = View.VISIBLE
                binding.hour.visibility = View.VISIBLE
                fecha.visibility = View.VISIBLE
                hora.visibility = View.VISIBLE
            }
        }

        //CheckBox
        var flag = 0
        binding.checkBox.setOnClickListener{
            if (binding.checkBox.isChecked) {
                binding.date.visibility = View.VISIBLE
                binding.hour.visibility = View.VISIBLE
                fecha.visibility = View.VISIBLE
                hora.visibility = View.VISIBLE
                flag = 1
            } else {
                binding.date.visibility = View.INVISIBLE
                binding.date.text = ""
                binding.hour.visibility = View.INVISIBLE
                binding.hour.text = ("")
                fecha.visibility = View.INVISIBLE
                hora.visibility = View.INVISIBLE
                flag = 0
            }
        }

        //Navigation
        binding.btnFotoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_photoFragment, bundle)
        }

        binding.btnVideoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_videoFragment, bundle)
        }

        binding.btnAudioT.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_audio, bundle)
        }

        binding.multimediaTask.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_viewMultimediaFragment, bundle)
        }

        binding.cancelTask.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_home2)
        }

        binding.saveTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.titleTask.text.toString())
            bundle.putString("description", binding.descriptionTask.text.toString())
            parentFragmentManager.setFragmentResult("key",bundle)

            //Notification
            createNotificationChannel()
            scheduleNotificaction(binding.titleTask.text.toString())

            //Insert
            lifecycleScope.launch{
                if (id == -1){
                    //insert new task --> type=2
                    val newTask = Note(
                        2,
                        binding.titleTask.text.toString(),
                        binding.descriptionTask.text.toString(),
                        currentTime,
                        fecha.text.toString(),
                        hora.text.toString(),
                        flag)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().insert(newTask)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().getAll()
                } else {
                    //update note
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().
                    updateTask(
                        binding.titleTask.text.toString(),
                        binding.descriptionTask.text.toString(),
                        currentTime,
                        fecha.text.toString(),
                        hora.text.toString(),
                        flag,
                        id)
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
        val message = "Tienes esta tarea pendiente"
        intent.putExtra(titleExtra, titulo)
        intent.putExtra(messageExtra, message)

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            0
        )

       // val time = getTime()
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //time,
            SystemClock.elapsedRealtime() + 10 * 1000,
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
package com.gabyperez.notes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.gabyperez.notes.model.Reminder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateTask : Fragment() {

    private lateinit var fecha: EditText
    private lateinit var hora: EditText
    private var idNote: Int = -1
    private  var idReminder: Int = -1
    private var diaAux = 0
    private var mesAux = 0
    private var yearAux = 0
    private var horaAux = 0
    private var minuteAux = 0

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

        val bundle = Bundle()
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        //edit
        if(arguments?.getString("title") != null) {
            binding.titleTask.setText(arguments?.getString("title"))
            binding.descriptionTask.setText(arguments?.getString("description"))
            binding.txtDate.setText(arguments?.getString("dateEnd"))
            binding.txtHour.setText(arguments?.getString("hourEnd"))
            idNote = arguments?.getString("id")!!.toInt()
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

        binding.btnReminderT.setOnClickListener{
            it.findNavController().navigate(R.id.action_createTask_to_fragment_Reminders, bundle)
        }
        binding.saveTask.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", binding.titleTask.text.toString())
            bundle.putString("description", binding.descriptionTask.text.toString())
            parentFragmentManager.setFragmentResult("key",bundle)

            //Notification
            // scheduleNotification(binding.titleTask.text.toString())
            //Insert Notification
           /* lifecycleScope.launch{
                if (idReminder == -1){
                    val newReminder = Reminder(
                        idNote,
                       "$diaAux/$mesAux/$yearAux",
                        "$horaAux:$minuteAux"
                        )
                    NoteDatabase.getDatabase(requireActivity().applicationContext).RerminderDAO().insert(newReminder)
                }
            }*/

            //Insert
            lifecycleScope.launch{
                if (idNote == -1){
                    val newTask = Note(
                        2,
                        binding.titleTask.text.toString(),
                        binding.descriptionTask.text.toString(),
                        currentTime,
                        fecha.text.toString(),
                        hora.text.toString(),
                        flag)
                    NoteDatabase.getDatabase(requireActivity().applicationContext).NoteDao().insert(newTask)
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
        val newFragment = TimePicker { hour, minute -> onTimeSelected(hour, minute) }
        activity?.let { newFragment.show(it.supportFragmentManager, "timePicker") }
    }

    @SuppressLint("SetTextI18n")
    private fun onTimeSelected(hour:Int, minute:Int) {
        hora.setText("$hour:$minute")
        this.horaAux = hour
        this.minuteAux = minute
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker") }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val aux=month+1
        fecha.setText("$day/$aux/$year")

        this.diaAux = day
        this.mesAux = month
        this.yearAux = year
    }

    //Notification
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(titulo: String) {
        getTime(titulo)
    }


    private fun startAlarm(calendar: Calendar, titulo: String) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MiReceiverParaAlarma::class.java)
        val message = "Tienes esta tarea pendiente"
        intent.putExtra(titleExtra, titulo)
        intent.putExtra(mensajeExtra, message)
        val pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun getTime(titulo: String){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,horaAux)
        calendar.set(Calendar.MINUTE,minuteAux)
        calendar.set(Calendar.SECOND,0)
        startAlarm(calendar, titulo)
    }





}
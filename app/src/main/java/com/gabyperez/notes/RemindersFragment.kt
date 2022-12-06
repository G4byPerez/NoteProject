package com.gabyperez.notes

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.databinding.FragmentRemindersBinding
import com.gabyperez.notes.model.Reminder
import kotlinx.coroutines.launch
import java.util.*


class RemindersFragment : Fragment() {

    private lateinit var binding:FragmentRemindersBinding
    lateinit var title :String
    lateinit var description :String
    private lateinit var fecha: EditText
    private lateinit var hora: EditText
    private var diaAux = 0
    private var mesAux = 0
    private var yearAux = 0
    private var horaAux = 0
    private var minuteAux = 0
    lateinit var rv : RecyclerView
    private var reminders : MutableList<Reminder> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRemindersBinding.inflate(layoutInflater)
        fecha = binding.txtFecha
        hora = binding.txtHoraR

        rv = binding.listReminders

        //view reminder
        lifecycleScope.launch {
            reminders =
                NoteDatabase.getDatabase(requireActivity().applicationContext).RerminderDAO().
                getAllReminders(arguments?.getString("id")!!.toInt())
        }

        rv.adapter = ReminderAdapter(reminders)
        rv.layoutManager = LinearLayoutManager(this@RemindersFragment.requireContext())


        //Date and hour
        binding.btnFecha.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnHourReminder.setOnClickListener {
            showTimePikerDialog()
        }

        binding.btnGuardarR.setOnClickListener{

            val note = NoteDatabase.getDatabase(requireActivity().applicationContext).
            NoteDao().getById(arguments?.getString("id")!!.toInt())

            //Notification
            scheduleNotification(note.title)
            //Insert Notification
            lifecycleScope.launch{
                    val newReminder = Reminder(
                        arguments?.getString("id")!!.toInt(),
                        "$diaAux/$mesAux/$yearAux",
                        "$horaAux:$minuteAux"
                    )
                    NoteDatabase.getDatabase(requireActivity().applicationContext).RerminderDAO().insert(newReminder)
            }

        }
        return binding.root
    }

    //Notification
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(titulo: String) {
        getTime(titulo)
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
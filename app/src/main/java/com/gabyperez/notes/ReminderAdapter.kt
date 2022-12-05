package com.gabyperez.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gabyperez.notes.data.NoteDatabase
import com.gabyperez.notes.model.Reminder

class ReminderAdapter(var reminders: List<Reminder>): RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {
    class ViewHolder(v : View) : RecyclerView.ViewHolder(v){
        var time : TextView
        var date : TextView
        var btn_delete : ImageView

        init{
            time = v.findViewById(R.id.reminder_time)
            date = v.findViewById(R.id.reminder_date)
            btn_delete = v.findViewById(R.id.btn_delete2)

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = reminders[position]
        var id = p.noteID
        holder.time.text = p.time
        holder.date.text = p.date
        holder.btn_delete.setOnClickListener{view : View ->
            NoteDatabase.getDatabase(holder.time.context).RerminderDAO().deleteReminder(p)
            var reminders  = NoteDatabase.getDatabase(holder.time.context).RerminderDAO().getAllReminders(id)
            this.reminders = reminders
            this.notifyItemRemoved(position)
        }

    }

    override fun getItemCount(): Int {
        return reminders.size
    }

}
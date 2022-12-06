package com.gabyperez.notes

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


var notificationID = 1
val titleExtra ="TitleExtra"
const val mensajeExtra = "messageExtra"

class MiReceiverParaAlarma : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent ) {
        val notificationUtils = NotificationUtils(context)
        val activityIntent = Intent(context, MainActivity()::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
        0
        )
        val notification = notificationUtils.getNotificationBuilder(titleExtra)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentIntent(activityPendingIntent)
            .build()
        notificationUtils.getManager().notify(notificationID++, notification)
    }
}
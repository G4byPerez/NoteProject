package com.gabyperez.notes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


var notificationID = 1
val titleExtra ="TitleExtra"
const val mensajeExtra = "messageExtra"

class MiReceiverParaAlarma : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder(titleExtra)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .build()
        notificationUtils.getManager().notify(notificationID++, notification)
    }
}
package org.hyperskill.stopwatch

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class RemainderReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver", "LaunchActivityFromNotification")
    override fun onReceive(context: Context, intent: Intent) {
        val intentCreated = Intent(context, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intentCreated, PendingIntent.FLAG_IMMUTABLE)
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(context, "org.hyperskill")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Alarm is on")
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE  or Notification.FLAG_INSISTENT
        mNotificationManager.notify(393939, notification)
    }
}
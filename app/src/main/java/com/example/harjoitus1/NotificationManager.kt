package com.example.harjoitus1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager

class NotificationHelper(private val context: Context) {

    var builder = NotificationCompat.Builder(context, "1")
        .setSmallIcon(R.drawable.notifications_active_24)
        .setContentTitle("Test title")
        .setContentText("Test content")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val descriptionText = context.getString(R.string.app_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(title: String, message: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notifications_active_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        } else {
            println("Not granted")
        }
    }

    fun sendProfNotification(title: String, message: String) {
        val ACTION_UNDO = "undo"
        val CHANNEL_ID = "2"
        val notificationId = 1

        val undoIntent = Intent(context, MyBroadCastReceiver::class.java).apply {
            action = ACTION_UNDO
            putExtra("notification_id", notificationId)
        }

        val undoPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, undoIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notifications_active_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setAutoCancel(true)
                .addAction(R.drawable.baseline_undo_24, "RESET", undoPendingIntent)

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        } else {
            println("Not granted")
        }
    }

}

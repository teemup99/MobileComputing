package com.example.harjoitus1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "undo") {
            val db = UserRepository(context)
            CoroutineScope(Dispatchers.IO).launch {
                db.saveUser(User(uid = 1, userName = "Cirno", profilePicUri = null))

                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.cancel(1)

                restartApp(context)
            }

            Toast.makeText(context, "Profile reverted to default", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        System.exit(0)
    }
}

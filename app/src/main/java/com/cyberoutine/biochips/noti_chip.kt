package com.cyberoutine.biochips

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.WorkSource
import android.webkit.ServiceWorkerClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.contentValuesOf
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

class noti_chip(val context: Activity, parameters: WorkerParameters): Worker(context, parameters)  {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        CoroutineScope(Dispatchers.IO).launch (start = CoroutineStart.LAZY) {
            val mk = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val pref = EncryptedSharedPreferences.create(context, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

            while (true) {
                var del = 10000
                if (LocalTime.now().hour == 11){
                    del = 500
                }
                if (LocalTime.now().hour == 12 && LocalTime.now().minute == 1 && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    val noti = NotificationCompat.Builder(context)
                        .setContentTitle("You have task")
                        .setContentText("You have ${pref.getInt("size", 0)} tasks to do")
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .build()


                    NotificationManagerCompat.from(context).notify("Noti_chip", 1, noti)
                    del = 10000
                } else {
                    ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                    Toast.makeText(context, "The notification biochip is disabled", Toast.LENGTH_SHORT).show()
                }
                delay(del.toLong())
            }
        }

        return Result.success()
    }
}
package com.cyberoutine.biochips

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.WorkSource
import android.util.Log
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
import com.cyberoutine.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class noti_chip (val context: Context, parameters: WorkerParameters): Worker(context, parameters)  {
    override fun doWork(): Result {

            val mk = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val pref = EncryptedSharedPreferences.create(context, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

            var del = 10000

            while (true) {
                Log.e("worker", "activo")
                val time = pref.getString("time", "12:12")!!.split(":")
                if (LocalTime.now().hour == time[0].toInt() - 1 || LocalTime.now().hour == time[0].toInt()){
                    del = 500
                }
                if (LocalTime.now().hour == time[0].toInt() && LocalTime.now().minute == time[1].toInt() && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

                    val regex = Regex("-task")
                    val noText = pref.getString("note", "")?.replace(regex, pref.getInt("size", 0).toString())

                    val noti = NotificationCompat.Builder(context)
                        .setChannelId("Noti_chip")
                        .setContentTitle("Reminder")
                        .setContentText(noText)
                        .setSmallIcon(R.mipmap.cyber_logo_round)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .build()


                    NotificationManagerCompat.from(context).notify(1, noti)
                    del = 10000
                    break
                }

                Thread.sleep(del.toLong())
            }
        return Result.success()
    }
}
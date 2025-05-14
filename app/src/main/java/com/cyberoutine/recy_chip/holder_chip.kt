package com.cyberoutine.recy_chip

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cyberoutine.R
import com.cyberoutine.db.chip_data
import com.cyberoutine.db.db_chip
import com.cyberoutine.db.db_chip.Companion.chip_list
import com.cyberoutine.update_chip
import java.util.jar.Manifest
import javax.crypto.KeyGenerator

class holder_chip(view: View, val context: Activity): RecyclerView.ViewHolder(view) {

    val price_chip = view.findViewById<TextView>(R.id.price)
    val name_chip = view.findViewById<TextView>(R.id.name_chip)
    val description_chip = view.findViewById<TextView>(R.id.description_chip)
    val buy = view.findViewById<AppCompatButton>(R.id.buy_button)

    @RequiresApi(Build.VERSION_CODES.O)
    fun elemento(chipData: chip_data){
        price_chip.text = "€$ ${chipData.price}"
        name_chip.text = chipData.name
        description_chip.text = chipData.description



        if (chipData.dispo == 1){
            buy.visibility = View.VISIBLE
        }else {
            buy.visibility = View.INVISIBLE
        }

        buy.setOnClickListener {
            val mk = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            val pref = EncryptedSharedPreferences.create(context, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

            if (pref.getInt("money", 0) >= chipData.price.toInt()) {
                if (chipData.position == 0){
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(context, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                    }
                    pref.edit().putBoolean(chipData.name, true).commit()
                    val notifi_manmager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val canal = NotificationChannel("Noti_chip", "chanel", NotificationManager.IMPORTANCE_HIGH)

                    notifi_manmager.createNotificationChannel(canal)

                }
                if (chipData.position == 4){
                    val keyg = KeyGenParameterSpec.Builder("key", KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                    val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                    kg.init(keyg)
                    kg.generateKey()
                }

                pref.edit().putBoolean(chipData.name, true).commit()
                pref.edit().putInt("money", pref.getInt("money", 0) - chipData.price.toInt()).commit()

                val db = db_chip(context)

                db.update(chipData.position, 0)

                update_chip = true

                buy.visibility = View.INVISIBLE
                Toast.makeText(context, "The improvement has been implemented", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(buy.context, "You are missing ${chipData.price.toInt() - pref.getInt("money", 0)} eurodollars", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.cyberoutine

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.shapes.Shape
import android.media.audiofx.Virtualizer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.cyberoutine.biochips.noti_chip
import com.cyberoutine.db.db_user
import com.cyberoutine.db.db_user.Companion.points_spec_list
import com.cyberoutine.recy_hability.adapter_hability
import com.google.android.material.imageview.ShapeableImageView
import java.sql.Time
import java.time.LocalTime

class CharacterActivity : AppCompatActivity() {
    private lateinit var time_dialog: TimePickerDialog
    private lateinit var dialog: Dialog
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseKtx")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_character)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val mk = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val pref = EncryptedSharedPreferences.create(this, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        val image_cha = findViewById<ImageView>(R.id.image)
        val name_cha = findViewById<TextView>(R.id.name)
        val clock = findViewById<ShapeableImageView>(R.id.clock)
        val recy = findViewById<RecyclerView>(R.id.recy_hability)

        if (!pref.getBoolean("NeuroNoti-F0", false)){
            clock.visibility = View.INVISIBLE
        }

        clock.setOnClickListener {
            dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_time_picker, null)

            val time_visor = view.findViewById<TextView>(R.id.time_visor)
            val time_edit = view.findViewById<ShapeableImageView>(R.id.edit_time)

            val note = view.findViewById<EditText>(R.id.note)

            val button_program = view.findViewById<AppCompatButton>(R.id.program)

            time_visor.text = pref.getString("time", "${LocalTime.now().hour}:${LocalTime.now().minute}")
            note.setText(pref.getString("note", ""))
            time_edit.setOnClickListener {
                val time = time_visor.text.toString().split(":")
                time_dialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener{_, hour, minute ->
                    time_visor.text = "$hour:$minute"
                    pref.edit().putString("time", "$hour:$minute").commit()
                    time_dialog.dismiss()
                },
                    time[0].toInt(), time[1].toInt(), true)

                time_dialog.show()
            }

            button_program.setOnClickListener {
                if (note.text.isNotEmpty() && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){

                    try {
                        WorkManager.getInstance(this).cancelAllWorkByTag("noti_work")
                    }catch(e: Exception){
                        Log.e("nothing work", e.toString())
                    }

                    pref.edit().putString("note", note.text.toString()).commit()
                    val query = OneTimeWorkRequestBuilder<noti_chip>()
                        .addTag("noti_work")
                        .build()

                    WorkManager.getInstance(this).enqueue(query)
                    Toast.makeText(this, "The notification has been scheduled", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()


                }else {
                    Toast.makeText(this, "You should add a reminder to the notification.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS))
                }
            }

            dialog.setContentView(view)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()


        }

        val (image, name) = characters[pref.getInt("character", 0)]

        image_cha.setImageResource(image)
        name_cha.text = name

        val db = db_user(this)

        if (db.extraccion()){
        }

        recy.adapter = adapter_hability(points_spec_list)
        recy.layoutManager = object: LinearLayoutManager(this) {
            override fun canScrollHorizontally(): Boolean = false
            override fun canScrollVertically(): Boolean = false
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
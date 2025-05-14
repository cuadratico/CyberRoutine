package com.cyberoutine

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cyberoutine.db.db_chip
import com.cyberoutine.db.db_chip.Companion.chip_list
import com.cyberoutine.recy_chip.adapter_chip
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var update_chip = false
class chipActivity : AppCompatActivity() {
    private lateinit var adapter: adapter_chip
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chip)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val money_global = findViewById<TextView>(R.id.money_global)
        val recy = findViewById<RecyclerView>(R.id.recy_chips)
        val info = findViewById<ShapeableImageView>(R.id.info)

        val db = db_chip(this)
        db.extraccion()
        adapter = adapter_chip(chip_list, this)
        recy.adapter = adapter
        recy.layoutManager = LinearLayoutManager(this)

        val mk = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val pref = EncryptedSharedPreferences.create(applicationContext, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        money_global.text = pref.getInt("money", 0).toString()


        info.setOnClickListener {
            val dialog_info = AlertDialog.Builder(this).apply {
                setTitle("Do you want to contribute an idea for a new Biochip?")
                setPositiveButton ("Yes"){_, _ ->  startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/QVNp8U4zLf78nGEj7"))) }
                setNegativeButton ("No"){_, _ ->  }
            }
            dialog_info.show()
        }

        lifecycleScope.launch (Dispatchers.IO){

            while (true){

                if (update_chip){
                    withContext (Dispatchers.Main){
                        money_global.text = pref.getInt("money", 0).toString()
                    }
                    update_chip = false
                }
                delay(500)
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, taskActivity::class.java))
        finish()
    }
}
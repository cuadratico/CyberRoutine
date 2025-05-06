package com.cyberoutine

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cyberoutine.db.db_user
import com.cyberoutine.db.db_user.Companion.points_spec_list
import com.cyberoutine.recy_hability.adapter_hability

class CharacterActivity : AppCompatActivity() {
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
        val recy = findViewById<RecyclerView>(R.id.recy_hability)

        val (image, name) = characters[pref.getInt("character", 0)]

        image_cha.setImageResource(image)
        name_cha.text = name

        val db = db_user(this)

        if (db.extraccion()){
        }

        recy.adapter = adapter_hability(points_spec_list)
        recy.layoutManager = LinearLayoutManager(this)


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
package com.cyberoutine

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.imageview.ShapeableImageView

class SelectorActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.selector)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        val mk = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val pref = EncryptedSharedPreferences.create(this, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        val edit = findViewById<ShapeableImageView>(R.id.edit_character)
        val name_character = findViewById<TextView>(R.id.name_character)
        val image_character = findViewById<ImageView>(R.id.image_character)
        val continue_button = findViewById<AppCompatButton>(R.id.button_continue)

        val image_1 = findViewById<ShapeableImageView>(R.id.image_1)
        val image_2 = findViewById<ShapeableImageView>(R.id.image_2)
        val image_3 = findViewById<ShapeableImageView>(R.id.image_3)

        val (image, name, extra_points, visibility) = characters[pref.getInt("character", 0)]

        image_character.setImageResource(image)
        name_character.text = name
        image_1.setImageResource(extra_points[0])
        image_2.setImageResource(extra_points[1])
        image_3.visibility = visibility


        continue_button.setOnClickListener {
            pref.edit().putBoolean("start", true).commit()
            startActivity(Intent(this, taskActivity::class.java))
            finish()
        }

        edit.setOnClickListener {

            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_selector_character, null)

            val close = view.findViewById<ImageView>(R.id.close)
            val david = view.findViewById<ConstraintLayout>(R.id.david_selection)
            val lucy = view.findViewById<ConstraintLayout>(R.id.lucy_selection)

            close.setOnClickListener {
                dialog.dismiss()
            }

            david.setOnClickListener {
                pref.edit().putInt("character", 1).commit()
                dialog.dismiss()
                recreate()
            }

            lucy.setOnClickListener {
                pref.edit().putInt("character", 0).commit()
                dialog.dismiss()
                recreate()
            }


            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
    }
}

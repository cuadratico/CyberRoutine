package com.cyberoutine.recy_task

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.cyberoutine.R
import com.cyberoutine.db.db_task
import com.cyberoutine.db.db_user
import com.cyberoutine.habilidades
import com.cyberoutine.update_recy
import com.google.android.material.imageview.ShapeableImageView

class holder_task(view: View): RecyclerView.ViewHolder(view) {

    val task_complet = view.findViewById<CheckBox>(R.id.task_complete)
    val task = view.findViewById<TextView>(R.id.task)
    val delete = view.findViewById<AppCompatButton>(R.id.delete_button)
    val fondo = view.findViewById<ConstraintLayout>(R.id.fondo)
    val points_hability = view.findViewById<TextView>(R.id.puntuacion)
    val image_hability = view.findViewById<ImageView>(R.id.image_puntuacion)
    val money_hability = view.findViewById<TextView>(R.id.money_task)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    fun elemtno(dataTask: data_task){
        val mk = MasterKey.Builder(money_hability.context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val pref = EncryptedSharedPreferences.create(money_hability.context, "ap", mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        var puntos_total = habilidades[dataTask.habilidad]!!.points
        if (pref.getBoolean("Neur0-Clock", false)){
            puntos_total = puntos_total * 2
        }

        task_complet.isChecked = false
        task.text = dataTask.task
        points_hability.text = "+$puntos_total"
        image_hability.setImageResource(habilidades[dataTask.habilidad]!!.imagen)
        money_hability.text = "+${dataTask.money} €$"

        val context = task.context
        val db_task = db_task(context)

        task_complet.setOnClickListener {

            pref.edit().putInt("money", dataTask.money + pref.getInt("money", 0)).commit()
            db_task.delete(dataTask.position, pref)
            val db_user = db_user(context)



            db_user.update(dataTask.habilidad, puntos_total)
            update_recy = true
        }

        fondo.setOnClickListener {
            val edit_dialog = Dialog(context)
            val edit_view = LayoutInflater.from(context).inflate(R.layout.dialog_task, null)

            val edit_information = edit_view.findViewById<TextView>(R.id.information)
            edit_information.text = "Edit your task"
            val edit_hability = edit_view.findViewById<ShapeableImageView>(R.id.habilidad_selector)
            edit_hability.visibility = View.INVISIBLE
            val edit_task = edit_view.findViewById<EditText>(R.id.task)
            edit_task.setText(dataTask.task)
            val edit_button_task = edit_view.findViewById<AppCompatButton>(R.id.add_task)
            edit_button_task.text = "Edit the task"

            edit_button_task.setOnClickListener {
                db_task.update(dataTask.position, edit_task.text.toString())
                edit_dialog.dismiss()
            }

            edit_dialog.setContentView(edit_view)
            edit_dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            edit_dialog.show()
        }

        delete.setOnClickListener {
            db_task.delete(dataTask.position, pref)
            update_recy = true
        }
    }
}
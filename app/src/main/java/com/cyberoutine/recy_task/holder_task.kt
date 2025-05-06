package com.cyberoutine.recy_task

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
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

class holder_task(view: View): RecyclerView.ViewHolder(view) {

    val task_complet = view.findViewById<CheckBox>(R.id.task_complete)
    val task = view.findViewById<TextView>(R.id.task)
    val delete = view.findViewById<AppCompatButton>(R.id.delete_button)
    val fondo = view.findViewById<ConstraintLayout>(R.id.fondo)
    val points_hability = view.findViewById<TextView>(R.id.puntuacion)
    val image_hability = view.findViewById<ImageView>(R.id.image_puntuacion)
    val money_hability = view.findViewById<TextView>(R.id.money_task)

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
            val clip_manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("task", task.text.toString())
            clip_manager.setPrimaryClip(clip)
        }

        delete.setOnClickListener {
            db_task.delete(dataTask.position, pref)
            update_recy = true
        }
    }
}
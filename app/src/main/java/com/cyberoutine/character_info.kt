package com.cyberoutine

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.view.View

data class hability_data (val imagen: Int, var points: Int)
data class character_info(val image: Int, val name: String, val extra_points: List<Int>, val vsibility: Int)

val characters = listOf(character_info(R.drawable.lucyna_kushinada, "Lucy Kushinada", listOf(R.drawable.cyberpunk_hbilidad_tecnica, R.drawable.cyberpunk_inteligencia), View.VISIBLE), character_info(R.drawable.david_martinez, "David Martinez", listOf(R.drawable.cyberpunk_fuerza, R.drawable.cyberpunk_reflejos), View.INVISIBLE))


val habilidades = mapOf("Force" to hability_data(R.drawable.cyberpunk_fuerza, 1), "Technical skill" to hability_data(R.drawable.cyberpunk_hbilidad_tecnica, 1), "Intelligence" to hability_data(R.drawable.cyberpunk_inteligencia, 1), "Highlights" to hability_data(R.drawable.cyberpunk_reflejos, 1), "Temper" to hability_data(R.drawable.cyberpunk_temple, 1))


fun hability_points(pref: SharedPreferences){

    for ((_, data) in habilidades){
        data.points = 1
    }

    if (pref.getInt("character", 0) == 0){
        habilidades["Technical skill"]?.points = 2
        habilidades["Intelligence"]?.points = 2
        habilidades["Temper"]?.points = 2
    }else {
        habilidades["Force"]?.points = 2
        habilidades["Highlights"]?.points = 2
    }
}

val list_habilidad = arrayOf("Force", "Technical skill", "Intelligence", "Highlights", "Temper")
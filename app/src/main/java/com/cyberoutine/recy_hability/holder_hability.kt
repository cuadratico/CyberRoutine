package com.cyberoutine.recy_hability

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyberoutine.R
import com.cyberoutine.db.points_spec

class holder_hability(view: View): RecyclerView.ViewHolder(view) {

    val image_hability = view.findViewById<ImageView>(R.id.image_hability)
    val name_hability = view.findViewById<TextView>(R.id.name_hability)
    val points_hability = view.findViewById<TextView>(R.id.points_hability)

    fun elemento (pointsSpec: points_spec){
        image_hability.setImageResource(pointsSpec.image)
        name_hability.text = pointsSpec.name
        points_hability.text = pointsSpec.points.toString()
    }
}
package com.cyberoutine.recy_hability

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyberoutine.R
import com.cyberoutine.db.db_user
import com.cyberoutine.db.db_user.Companion.points_spec_list
import com.cyberoutine.db.points_spec

class adapter_hability(list: List<points_spec>): RecyclerView.Adapter<holder_hability>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder_hability {
        return holder_hability(LayoutInflater.from(parent.context).inflate(R.layout.recy_hability, null))
    }

    override fun onBindViewHolder(holder: holder_hability, position: Int) {
        return holder.elemento(points_spec_list[position])
    }

    override fun getItemCount(): Int {
        return points_spec_list.size
    }
}
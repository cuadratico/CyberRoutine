package com.cyberoutine.recy_chip

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.cyberoutine.R
import com.cyberoutine.db.chip_data
import com.cyberoutine.db.db_chip.Companion.chip_list

class adapter_chip(var list: List<chip_data>, val context: Activity): RecyclerView.Adapter<holder_chip>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder_chip {
        return holder_chip(LayoutInflater.from(parent.context).inflate(R.layout.recy_chip, null), context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: holder_chip, position: Int) {
        return holder.elemento(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(){
        this.list = chip_list

        notifyDataSetChanged()
    }
}
package com.cyberoutine.recy_task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyberoutine.R
import com.cyberoutine.update_recy

class adapter_task(var lista: List<data_task>): RecyclerView.Adapter<holder_task>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder_task {
        return holder_task(LayoutInflater.from(parent.context).inflate(R.layout.recy_task, null))
    }

    override fun onBindViewHolder(holder: holder_task, position: Int) {
        holder.elemtno(lista[position])
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun update (){
        this.lista = task_list

        notifyDataSetChanged()
    }
}
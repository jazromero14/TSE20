package com.udbstudents.tseapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.ActaAndMunicipio

class MyAdapter(private val events: List<ActaAndMunicipio>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        val event = events[position]

        holder.municipioName.text = event.municipio.nombre
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val municipioName = view.findViewById<TextView>(R.id.municipioName)
    }
}
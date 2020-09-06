package com.udbstudents.tseapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.ActaAndMunicipio
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(private val events: MutableList<ActaAndMunicipio>) :





    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var emptyList : MutableList<ActaAndMunicipio> = mutableListOf()
    private var emptylistFull: MutableList<ActaAndMunicipio> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    fun getFilter(): Filter? {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<ActaAndMunicipio> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(emptylistFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                for (item in emptylistFull) {
                    if (item.municipio.nombre!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {

            events.clear()
            events.addAll(results.values as MutableList<ActaAndMunicipio>)
            notifyDataSetChanged()
        }
    }
    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        val event = events[position]

        holder.actaIndice.text =event.actaIndece.toString()
        holder.municipioName.text = event.municipio.nombre
        holder.partidoUno.text = event.acta.idPartido_uno
        holder.partidodos.text = event.acta.idPartido_dos
        holder.partidotres.text = event.acta.idPartido_tres
        holder.partidocuatro.text = event.acta.idPartido_cuatro
        holder.votosUno.text = event.acta.votos_partUno.toString()
        holder.votosDos.text = event.acta.votos_partDos.toString()
        holder.votosTres.text = event.acta.votos_partTres.toString()
        holder.votosCuatro.text = event.acta.votos_partCuatro.toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val actaIndice = view.findViewById<TextView>(R.id.event_index_txt)
        val municipioName = view.findViewById<TextView>(R.id.municipioName)
        val partidoUno = view.findViewById<TextView>(R.id.partidounoitem)
        val partidodos = view.findViewById<TextView>(R.id.partidoDositem)
        val partidotres = view.findViewById<TextView>(R.id.partidoTresitem)
        val partidocuatro = view.findViewById<TextView>(R.id.partidoCuatroitem)
        val votosUno = view.findViewById<TextView>(R.id.partidounoName)
        val votosDos = view.findViewById<TextView>(R.id.partidoDosName)
        val votosTres = view.findViewById<TextView>(R.id.partidoTresName)
        val votosCuatro = view.findViewById<TextView>(R.id.partidoCuatroName)
    }
}
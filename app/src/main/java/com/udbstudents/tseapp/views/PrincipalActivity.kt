package com.udbstudents.tseapp.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.adapters.MyAdapter
import com.udbstudents.tseapp.models.ActaAndMunicipio
import com.udbstudents.tseapp.models.Actas
import com.udbstudents.tseapp.models.Municipio
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mFirestore: FirebaseFirestore
    private var municipiosLista: MutableList<Municipio> = mutableListOf()
    private var listaActasRecyclerView: MutableList<ActaAndMunicipio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)


        mFirestore = FirebaseFirestore.getInstance()


        mFirestore.collection("Municipios").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val municipio = it.result!!.toObjects(Municipio::class.java)
                    municipio.forEach { value ->
                        municipiosLista.add(value)
                    }
                }
                getAllActas(municipiosLista)
            }
    }

    private fun getAllActas(municipios: MutableList<Municipio>) {
        mFirestore.collection("Actas").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val actasModel = it.result!!.toObjects(Actas::class.java)
                    for (acta in actasModel) {
                        for (municipio in municipios) {
                            if (acta.idMunicipio == municipio.idMunicipio) {
                                val actaAndMunicipio = ActaAndMunicipio(acta, municipio)
                                listaActasRecyclerView.add(actaAndMunicipio)
                            }
                        }
                    }
                    inflateRecyclerView(listaActasRecyclerView)
                    Log.e("FUNCIONA", "Listado de actas$actasModel")
                } else {
                    Log.e("NO FUNCIONA", "")
                }
            }
    }

    private fun inflateRecyclerView(list: MutableList<ActaAndMunicipio>) {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = MyAdapter(list)
        my_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            viewAdapter.notifyDataSetChanged()
        }
    }
}
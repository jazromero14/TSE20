package com.udbstudents.tseapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.adapters.MyAdapter
import com.udbstudents.tseapp.models.*
import kotlinx.android.synthetic.main.activity_principal.*
import java.util.ArrayList

class SecondActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var municipiosLista: MutableList<Municipio> = mutableListOf()
    private var listaActasRecyclerView: MutableList<ActaAndMunicipio> = mutableListOf()
    private var listVotosUno : MutableList<Partido> = mutableListOf()
    private var listVotosDos : MutableList<Partido> = mutableListOf()
    private var listVotosTres : MutableList<Partido> = mutableListOf()
    private var listVotosCuatro : MutableList<Partido> = mutableListOf()
    private lateinit var municipioSelected : String
    private lateinit var actasTotal : String
    private lateinit var event_total: TextView
    private lateinit var  editPatidoUnoName: TextView
    private lateinit var  editPatidoDosName: TextView
    private lateinit var  editPatidoTresName: TextView
    private lateinit var  editPatidoCuatroName: TextView



    private lateinit var spinnerMunicipio : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second )
        setSupportActionBar(mainToolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        event_total = findViewById<TextView>(R.id.event_total)

        editPatidoUnoName = findViewById<TextView>(R.id.partidounoName)
        editPatidoDosName = findViewById<TextView>(R.id.partidoDosName)
        editPatidoTresName = findViewById<TextView>(R.id.partidoTresName)
        editPatidoCuatroName = findViewById<TextView>(R.id.partidoCuatroName)
        actasTotal = ""
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        spinnerMunicipio = findViewById(R.id.planets_spinner)
        getMunicipios()
    }


    private fun getMunicipios(){
        var municipioLis : MutableList<Municipio> = arrayListOf()
        mFirestore.collection("Municipios").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val municipio = it.result!!.toObjects(Municipio::class.java)
                    municipio.forEach { value ->
                        municipiosLista.add(value)
                        municipioLis.add(value)
                    }
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, municipiosLista).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                            spinnerMunicipio.adapter = adapter

                        spinnerMunicipio.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val eventCaptured = parent!!.getItemAtPosition(position).toString()

                                for (item in municipiosLista){
                                    if (item.nombre == eventCaptured){
                                         municipioSelected = item.idMunicipio.toString()
                                        getAllActas(municipiosLista, municipioSelected)
                                    }
                                }
                            }
                        }
                    }
                    }


            }

    }


    private fun getAllActas(municipios: MutableList<Municipio>, idMunicipioSelected : String) {
        listaActasRecyclerView.clear()
        mFirestore.collection("Actas").whereEqualTo("idMunicipio", idMunicipioSelected).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val actasModel = it.result!!.toObjects(Actas::class.java)
                    for (acta in actasModel) {
                        for (municipio in municipios) {
                            if (acta.idMunicipio == municipio.idMunicipio) {
                                val actaIndex = actasModel.indexOf(acta)
                                val actaAndMunicipio = ActaAndMunicipio(actaIndex, acta, municipio)
                                listaActasRecyclerView.add(actaAndMunicipio)
                                actasTotal = listaActasRecyclerView.size.toString()
                                val partOneName = actaAndMunicipio.acta.idPartido_uno
                                val partTwoName = actaAndMunicipio.acta.idPartido_uno
                                val partThreeName = actaAndMunicipio.acta.idPartido_uno
                                val partFourName = actaAndMunicipio.acta.idPartido_uno
                                editPatidoUnoName.text = partOneName
                                editPatidoDosName.text = partTwoName
                                editPatidoTresName.text = partThreeName
                                editPatidoCuatroName.text = partFourName
                                event_total.text = actasTotal
                                val partidoUno = Partido( actaAndMunicipio.acta.votos_partUno)
                                val partidoDos = Partido(actaAndMunicipio.acta.votos_partDos)
                                val partidoTres = Partido(actaAndMunicipio.acta.votos_partTres)
                                val partidoCuatro = Partido(actaAndMunicipio.acta.votos_partCuatro)
                                listVotosUno.add(partidoUno)
                                listVotosDos.add(partidoDos)
                                listVotosTres.add(partidoTres)
                                listVotosCuatro.add(partidoCuatro)
                                getallVostos(listVotosUno, listVotosDos, listVotosTres, listVotosCuatro)

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

    private fun getallVostos(listVotosUno: MutableList<Partido>, listVotosDos: MutableList<Partido>, listVotosTres: MutableList<Partido>, listVotosCuatro: MutableList<Partido>) {

        for (partido in listVotosUno){
            val votoUno = partido.votos
            val sumaVotosUno =+votoUno!!
            val totalvotosUno = sumaVotosUno

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {

                true
            }
            R.id.action_sign_up -> {
                mFirestore.collection("TokenUsuario").get().addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val documents = it.result!!.documents.iterator()
                        for (idDocument in documents) {
                            if (idDocument["idUsuario"] == mAuth.currentUser?.uid) {
                                mFirestore.collection("TokenUsuario").document(idDocument.id)
                                    .delete()
                                    .addOnCompleteListener(this) { tokenDelete ->
                                        if (tokenDelete.isSuccessful) {
                                            Log.i("TokenDelete", tokenDelete.result.toString())
                                            mAuth.signOut()
                                            startActivity(Intent(this, MainActivity::class.java))
                                            finish()
                                        }
                                    }.addOnFailureListener { tokenDeleteError ->
                                        Log.e("ErrorDelToken", tokenDeleteError.message!!)
                                        mAuth.signOut()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                            }
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
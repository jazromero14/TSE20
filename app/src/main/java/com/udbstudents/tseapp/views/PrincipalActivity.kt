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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.adapters.MyAdapter
import com.udbstudents.tseapp.models.*
import com.udbstudents.tseapp.utils.FN
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var municipiosLista: MutableList<Municipio> = mutableListOf()

    private var departamentosLista: MutableList<Departamento> = mutableListOf()
    private var listaActasRecyclerView: MutableList<ActaAndMunicipio> = mutableListOf()
    private var listVotosUno : MutableList<Partido> = mutableListOf()
    private var listVotosDos : MutableList<Partido> = mutableListOf()
    private var listVotosTres : MutableList<Partido> = mutableListOf()
    private var listVotosCuatro : MutableList<Partido> = mutableListOf()
    private lateinit var departamentoSelected : String
    private lateinit var actasTotal : String
    private lateinit var event_total: TextView
    private lateinit var  editPatidoUnoName: TextView
    private lateinit var  editPatidoDosName: TextView
    private lateinit var  editPatidoTresName: TextView
    private lateinit var  editPatidoCuatroName: TextView
    private lateinit var  consolidadoCard : CardView

    private lateinit var  editPatidoUnoTotal: TextView
    private lateinit var  editPatidoDosTotal: TextView
    private lateinit var  editPatidoTresTotal: TextView
    private lateinit var  editPatidoCuatroTotal: TextView
    private var votosTotalUno : Int = 0
    private var votosTotalDos : Int = 0
    private var votosTotalTres : Int = 0
    private var votosTotalCuatro : Int = 0


    private lateinit var spinnerDepartamento : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        setSupportActionBar(mainToolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        event_total = findViewById<TextView>(R.id.event_total)
        consolidadoCard = findViewById(R.id.consolidadoCard)

        editPatidoUnoName = findViewById<TextView>(R.id.partidounoitem)
        editPatidoDosName = findViewById<TextView>(R.id.partidoDositem)
        editPatidoTresName = findViewById<TextView>(R.id.partidoTresitem)
        editPatidoCuatroName = findViewById<TextView>(R.id.partidoCuatroitem)


        editPatidoUnoTotal = findViewById<TextView>(R.id.partidounoName)
        editPatidoDosTotal = findViewById<TextView>(R.id.partidoDosName)
        editPatidoTresTotal = findViewById<TextView>(R.id.partidoTresName)
        editPatidoCuatroTotal = findViewById<TextView>(R.id.partidoCuatroName)

        actasTotal = ""
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        spinnerDepartamento = findViewById(R.id.spinner)
        getMunicipios()
    }


    private fun getMunicipios(){

        var departamentLis : MutableList<Departamento> = arrayListOf()
        var municipioLis : MutableList<Municipio> = arrayListOf()
        mFirestore.collection("Departamentos").get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val departamento = it.result!!.toObjects(Departamento::class.java)
                    departamento.forEach { value ->
                        departamentosLista.add(value)
                        departamentLis.add(value)
                    }
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, departamentosLista).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        spinnerDepartamento.adapter = adapter

                        spinnerDepartamento.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener{
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

                                for (item in departamentosLista){
                                    if (item.nombre == eventCaptured){
                                        departamentoSelected = item.idDeptamento.toString()
                                        listVotosUno.clear()
                                        listVotosDos.clear()
                                        listVotosTres.clear()
                                        listVotosCuatro.clear()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        mFirestore.collection("Municipios").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val municipio = it.result!!.toObjects(Municipio::class.java)
                    municipio.forEach { value ->
                        municipiosLista.add(value)
                        municipioLis.add(value)

                        getAllActas( municipiosLista, departamentosLista, departamentoSelected)
                    }
                }


            }

    }


    private fun getAllActas(municipios: MutableList<Municipio>,departamentos: MutableList<Departamento>, idDepartamentoSelected: String) {
        listaActasRecyclerView.clear()

        mFirestore.collection("Actas").whereEqualTo("idDepartamento", idDepartamentoSelected).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val actasModel = it.result!!.toObjects(Actas::class.java)
                    for (acta in actasModel) {
                        for (municipio in municipios) {
                            if (acta.idMunicipio == municipio.idMunicipio) {
                                consolidadoCard.visibility = View.VISIBLE
                                val actaIndex = actasModel.indexOf(acta)
                                val actaAndMunicipio = ActaAndMunicipio(actaIndex, acta, municipio)
                                listaActasRecyclerView.add(actaAndMunicipio)
                                actasTotal = "Actas procesadas: " + listaActasRecyclerView.size.toString()


                                //sacando nombres de partidos de DB

                                val partOneName = actaAndMunicipio.acta.idPartido_uno
                                val partTwoName = actaAndMunicipio.acta.idPartido_dos
                                val partThreeName = actaAndMunicipio.acta.idPartido_tres
                                val partFourName = actaAndMunicipio.acta.idPartido_cuatro
                                editPatidoUnoName.text = partOneName
                                editPatidoDosName.text = partTwoName
                                editPatidoTresName.text = partThreeName
                                editPatidoCuatroName.text = partFourName
                                event_total.text = actasTotal

                                //sacando votos de cada partido de DB
                                val partidoUno = Partido(actaAndMunicipio.acta.votos_partUno)
                                val partidoDos = Partido(actaAndMunicipio.acta.votos_partDos)
                                val partidoTres = Partido(actaAndMunicipio.acta.votos_partTres)
                                val partidoCuatro = Partido(actaAndMunicipio.acta.votos_partCuatro)
                                listVotosUno.add(partidoUno)
                                listVotosDos.add(partidoDos)
                                listVotosTres.add(partidoTres)
                                listVotosCuatro.add(partidoCuatro)
                                getallVostos(
                                    listVotosUno,
                                    listVotosDos,
                                    listVotosTres,
                                    listVotosCuatro
                                )

                            }
                        }
                    }
                    inflateRecyclerView(listaActasRecyclerView)
                    Log.e("FUNCIONA", "Listado de actas$actasModel")
                } else {

                    FN.alertProcesses(this, "El municipio seleccionado no posee ninguna acta todavía, intenta con otro")
                    consolidadoCard.visibility = View.INVISIBLE

                    Log.e("NO FUNCIONA", "")
                }
            }
    }

    private fun getallVostos(
        listVoUno: MutableList<Partido>,
        listVoDos: MutableList<Partido>,
        listVoTres: MutableList<Partido>,
        listVoCuatro: MutableList<Partido>
    ) {
        votosTotalUno = 0
        votosTotalDos = 0
        votosTotalTres = 0
        votosTotalCuatro = 0
        for (partido in listVoUno) {
            votosTotalUno += partido.votos!!
        }
        for (partidoDos in listVoDos){
            votosTotalDos += partidoDos.votos!!
        }

        for (partidoTres in listVoTres){
            votosTotalTres += partidoTres.votos!!
        }

        for (partidoCuatro in listVoCuatro){
            votosTotalCuatro += partidoCuatro.votos!!
        }

        val totalvotosUno = votosTotalUno
        val totalvotosDos = votosTotalDos
        val totalvotosTres = votosTotalTres
        val totalvotosCuatro = votosTotalCuatro

        editPatidoUnoTotal.text = totalvotosUno.toString()
        editPatidoDosTotal.text = totalvotosDos.toString()
        editPatidoTresTotal.text = totalvotosTres.toString()
        editPatidoCuatroTotal.text = totalvotosCuatro.toString()
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
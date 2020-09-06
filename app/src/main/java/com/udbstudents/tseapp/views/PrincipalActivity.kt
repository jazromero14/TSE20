package com.udbstudents.tseapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var mAuth: FirebaseAuth
    private var municipiosLista: MutableList<Municipio> = mutableListOf()
    private var listaActasRecyclerView: MutableList<ActaAndMunicipio> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second )
        setSupportActionBar(mainToolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mAuth = FirebaseAuth.getInstance()
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
                                val actaIndex = actasModel.indexOf(acta)
                                val actaAndMunicipio = ActaAndMunicipio(actaIndex, acta, municipio)
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE;

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                (viewAdapter as MyAdapter).getFilter()?.filter(newText)
                return false
            }
        })
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
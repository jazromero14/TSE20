package com.udbstudents.tseapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.Actas
import com.udbstudents.tseapp.models.Municipio
import com.udbstudents.tseapp.models.TokenUser

class PrincipalActivity : AppCompatActivity() {

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var municipiosLista: MutableList<Municipio>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)


        mFirestore = FirebaseFirestore.getInstance()


        mFirestore.collection("Municipios").get()
            .addOnCompleteListener{
                if (it.isSuccessful){

                }
            }
        getAllActas()
    }

    private fun getAllActas() {
        mFirestore.collection("Actas").get()
            .addOnCompleteListener {
            if (it.isSuccessful){
                val actasModel = it.result!!.toObjects(Actas::class.java)
                Log.e("FUNCIONA", "Listado de actas$actasModel")
            }else{
                Log.e("NO FUNCIONA", "")
            }
        }



    }
}
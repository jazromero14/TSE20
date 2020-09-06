package com.udbstudents.tseapp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.Departamento
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity() {


    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private var departamentoLista: MutableList<Departamento> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        setSupportActionBar(mainToolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        mFirestore.collection("Departamentos").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val departamento = it.result!!.toObjects(Departamento::class.java)
                    departamento.forEach { item ->
                        departamentoLista.add(item)
                    }
                }
                departamentoLista
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
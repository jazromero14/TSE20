package com.udbstudents.tseapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.TokenUser
import com.udbstudents.tseapp.utils.FN
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()

        btn_login.setOnClickListener {
            progressBarMain.visibility = View.VISIBLE
            btn_login.visibility = View.INVISIBLE
            if (edit_usuario.text.toString().trim().isNotEmpty() && edit_pass.text.toString().trim().isNotEmpty()) {
                logIn(edit_usuario.text.toString().trim(), edit_pass.text.toString().trim())
            } else {
                FN.alertProcesses(this, getString(R.string.empty_fields))
                progressBarMain.visibility = View.INVISIBLE
                btn_login.visibility = View.VISIBLE
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(this, SingUpActivity::class.java))
            finish()
        }
    }

    private fun logIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            progressBarMain.visibility = View.INVISIBLE
            if (it.isSuccessful) {
                val user = mAuth.currentUser
                if (user != null) {
                    getTokenFromCloudMessagingService(user)
                } else {
                    updateUI(null)
                    FN.alertProcesses(this, getString(R.string.message_error_login))
                    progressBarMain.visibility = View.INVISIBLE
                    btn_login.visibility = View.VISIBLE
                }
            } else {
                updateUI(null)
                FN.alertProcesses(this, getString(R.string.message_error_login))
                progressBarMain.visibility = View.INVISIBLE
                btn_login.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, PrincipalActivity::class.java))
            finish()
        } else {
            Log.i("Error Session", getString(R.string.warning_message))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        } else {
            updateUI(null)
        }
    }

    private fun getTokenFromCloudMessagingService(userAuth: FirebaseUser?) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("token", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            } else {
                val token = task.result?.token
                val user = userAuth?.uid
                val device = "Android"

                val tokenUser = TokenUser(token, device, user)
                FirebaseFirestore.getInstance().collection("TokenUsuario").whereEqualTo("deviceToken", token).get().addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val tokenUserModel = it.result!!.toObjects(TokenUser::class.java)
                        if (tokenUserModel.size == 0) {
                            FirebaseFirestore.getInstance().collection("TokenUsuario").document().set(tokenUser)
                            updateUI(userAuth)
                        }
                    } else {
                        Log.i("token_exist", token!!)
                    }
                }.addOnFailureListener(this){
                    Log.e("ErrorTokenInserted", it.message!!)
                }
            }
        })
    }

}

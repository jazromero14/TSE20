package com.udbstudents.tseapp.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.udbstudents.tseapp.R
import com.udbstudents.tseapp.models.TokenUser
import com.udbstudents.tseapp.models.User
import com.udbstudents.tseapp.utils.FN
import kotlinx.android.synthetic.main.activity_sing_up.*

class SingUpActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var isValid: Boolean = false
    private var isValidPassword: Boolean = false
    private var regexPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&])[A-Za-z\\d\$@\$!%*?&]{8,15}$".toRegex()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mAuth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            progress_bar_sign_up.visibility = View.VISIBLE
            btn_sign_up.visibility = View.INVISIBLE
            if (validateEmptyFields() && validateValidPatterns() && validatePasswordRegex()) {
                signUp(edit_email.text.toString().trim(), edit_password.text.toString().trim())
            } else {
                progress_bar_sign_up.visibility = View.GONE
                FN.alertProcesses(this, getString(R.string.warning_message))
                btn_sign_up.visibility = View.VISIBLE
            }
        }
    }


    private fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val user = mAuth.currentUser
                val userModel = User(
                    edit_name.text.toString().trim(),
                    edit_surname.text.toString().trim(),
                    edit_email.text.toString().trim()
                )
                FirebaseFirestore.getInstance()
                    .collection("Usuarios")
                    .document(user!!.uid)
                    .set(userModel)

                user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(edit_name.text.toString().trim()).build())
                getTokenFromCloudMessagingService(user)
                progress_bar_sign_up.visibility = View.GONE
            } else {
                updateUI(null)
                progress_bar_sign_up.visibility = View.GONE
                btn_sign_up.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            if (it.message!!.contains("The email address is already in use by another account.")) {
                FN.alertProcesses(this, getString(R.string.user_exists))
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

    private fun validateEmptyFields(): Boolean {
        when {
            TextUtils.isEmpty(edit_name.text.toString().trim()) -> {
                layout_name.error = "Campo ${edit_name.hint} está vacío"
                isValid = false
            }
            TextUtils.isEmpty(edit_surname.text.toString().trim()) -> {
                layout_surname.error = "Campo ${edit_surname.hint} está vacío"
                isValid = false
            }
            TextUtils.isEmpty(edit_email.text.toString().trim()) -> {
                layout_email.error = "Campo ${edit_email.hint} está vacío"
                isValid = false
            }
            TextUtils.isEmpty(edit_password.text.toString().trim()) -> {
                layout_password.error = "Campo ${edit_password.hint} está vacío"
                isValid = false
            }
            TextUtils.isEmpty(edit_confirm_password.text.toString().trim()) -> {
                layout_confirm_password.error = "Campo ${edit_confirm_password.hint} está vacío"
                isValid = false
            }
            else -> {
                isValid = true
            }
        }
        return isValid
    }

    private fun validateValidPatterns(): Boolean {
        return if (edit_password.text.toString().trim() == edit_confirm_password.text.toString().trim() && Patterns.EMAIL_ADDRESS.matcher(
                edit_email.text.toString().trim()
            ).matches()
        ) {
            true
        } else {
            layout_password.error = getString(R.string.no_match_pass)
            layout_confirm_password.error = getString(R.string.no_match_pass)
            false
        }
    }

    private fun validatePasswordRegex(): Boolean {
        val listPasswords = mutableListOf(
            edit_password.text.toString().trim(),
            edit_confirm_password.text.toString().trim()
        )
        listPasswords.forEach {
            if (regexPattern.containsMatchIn(it)) {
                isValidPassword = true
            } else {
                layout_password.error = getString(R.string.no_regex_matches)
                layout_confirm_password.error = getString(R.string.no_regex_matches)
                isValidPassword = false
            }
        }
        return isValidPassword
    }

    private fun getTokenFromCloudMessagingService(userAuth: FirebaseUser?) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result?.token
                val user = userAuth?.uid
                val device = "Android"

                val tokenUser = TokenUser(token, device, user)
                FirebaseFirestore.getInstance().collection("TokenUsuario")
                    .whereEqualTo("deviceToken", token).get().addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val tokenUserModel = it.result!!.toObjects(TokenUser::class.java)
                            if (tokenUserModel.size == 0) {
                                FirebaseFirestore.getInstance().collection("TokenUsuario")
                                    .document()
                                    .set(tokenUser)
                                updateUI(userAuth)
                            }
                        } else {
                            Log.i("token_exist", token!!)
                        }
                    }
            })
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


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

 /*   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                image_preview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("Usuarios")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }
    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addUploadRecordToDb(downloadUri.toString())
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{
                Log.e("ErrorUpload", it.message)
            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }*/
}

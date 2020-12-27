package com.ege.coronato.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ege.coronato.Model.User
import com.ege.coronato.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        val database = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerConfirmBtn.setOnClickListener {
            if (registerMailTxt.text.toString().isNullOrEmpty() || registerPwdTxt.text.toString()
                            .isNullOrEmpty() || registerNameTxt.text.toString().isNullOrEmpty())
                Toast.makeText(applicationContext,"Alanlar boş bırakılamaz.",Toast.LENGTH_SHORT).show()
            else {
                val username = registerNameTxt.text.toString()
                val email = registerMailTxt.text.toString()
                val pwd = registerPwdTxt.text.toString()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                auth.createUserWithEmailAndPassword(
                        email,
                        pwd)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val userId = user?.uid
                                val userStore = hashMapOf(
                                        "username" to username,
                                        "email" to email
                                )
                                if (userId != null) {
                                    database.collection("users").document(userId)
                                            .set(userStore)
                                            .addOnSuccessListener { Log.d("STS", "DocumentSnapshot successfully written!") }
                                            .addOnFailureListener { e -> Log.w("STR", "Error writing document", e) }
                                }
                                Toast.makeText(applicationContext,"Kayıt başarılı. Kullanıcı oluşturuldu.",Toast.LENGTH_SHORT).show()
                                updateUI(user)
                                val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                                startActivity(welcomeIntent)
                            } else {
                                Log.w("CRR", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(applicationContext,"Kayıt başarısız. Mail adresinin kullanımda olmadığından ve şifrenizin en az 6 karakterden oluştuğundan emin olun.",Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

    }

    fun registerBackClicked(view: View) {
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
    }

}
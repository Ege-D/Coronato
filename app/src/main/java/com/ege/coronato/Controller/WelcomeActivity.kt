package com.ege.coronato.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ege.coronato.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        auth = FirebaseAuth.getInstance()
        val username = intent.getStringExtra("Username")
        database = Firebase.firestore
        if(auth.currentUser != null) {
            welcomeUserNameTxt.text = username
            welcomeUserNameTxt.visibility = View.VISIBLE
            welcomeSignOutTxt.text = "Çıkış yap"
        } else {
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeSignOutTxt.text = "Giriş yap"
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun welcomeExitClicked(view: View) {
        if (auth.currentUser != null) {
            Firebase.auth.signOut()
            welcomeSignOutTxt.text = "Giriş yap"
            welcomeUserNameTxt.visibility = View.INVISIBLE
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
        }
    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeSignOutTxt.text = "Giriş yap"
        } else {
            welcomeSignOutTxt.text = "Çıkış yap"
            var username: String
            val db = currentUser.uid.let { database.collection("users").document(it) }
            db.get().addOnSuccessListener {
                username = it.get("username") as String
                welcomeUserNameTxt.text = username
                welcomeUserNameTxt.visibility = View.VISIBLE
            }
        }
    }
}


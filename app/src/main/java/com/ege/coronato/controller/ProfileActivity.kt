package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ege.coronato.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_welcome.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()
        database = Firebase.firestore
    }

    fun profileBackClicked (view: View) {
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeIntent)
    }

    private fun updateUI (currentUser: FirebaseUser?) {
        var username : String
        /*if (currentUser != null) {
            val collection = currentUser.uid.let {
                database.collection("users").document(it)
            }
            collection.get().addOnSuccessListener {
                username = it.get("username") as String
                profileNameTxt.text = username
            }
        }*/
        if (currentUser != null) {
            database.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            username = document.get("username") as String
                            profileNameTxt.text = username
                        } else {
                            database.collection("business").document(currentUser.uid).get()
                                .addOnSuccessListener { document ->
                                    username = document.get("username") as String
                                    profileNameTxt.text = username
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("PRR", "Error getting document: ", exception)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("PRR", "Error getting document: ", exception)
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        updateUI(user)
    }

}
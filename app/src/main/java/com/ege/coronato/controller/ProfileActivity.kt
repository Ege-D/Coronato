package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.ege.coronato.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_business.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_welcome.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    var inspectorID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        storage = Firebase.storage
        if (intent.hasExtra("inspectorID")) {
            inspectorID = intent.getStringExtra("inspectorID").toString()
        }
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
                                        if (document.exists()) {
                                            username = document.get("username") as String
                                            profileNameTxt.text = username
                                        } else {
                                            database.collection("inspectors").document(currentUser.uid).get()
                                                    .addOnSuccessListener { document ->
                                                        if (document.exists()) {
                                                            username = document.get("username") as String
                                                            profileNameTxt.text = username
                                                            val photo = document.get("photo") as String
                                                            storage.reference.child("images/$photo").downloadUrl.addOnSuccessListener { url ->
                                                                Glide.with(this)
                                                                        .load(url)
                                                                        .override(125, 125)
                                                                        .centerCrop()
                                                                        .into(profileAvatar)
                                                            }
                                                        }
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
                    .addOnFailureListener { exception ->
                        Log.w("PRR", "Error getting document: ", exception)
                    }
        }
    }

    override fun onStart() {
        super.onStart()
        if (inspectorID.isEmpty()) {
            val user = auth.currentUser
            updateUI(user)
        } else {
            database.collection("inspectors").document(inspectorID).get()
                .addOnSuccessListener { document ->
                    profileNameTxt.text = document.get("username") as String
                    val photo = document.get("photo") as String
                    storage.reference.child("images/$photo").downloadUrl.addOnSuccessListener { url ->
                        Glide.with(this)
                            .load(url)
                            .override(125, 125)
                            .centerCrop()
                            .into(profileAvatar)
                    }
                }
        }
    }

}
package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ege.coronato.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcome.*
import android.graphics.BitmapFactory
import android.util.Log

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
            welcomeProfileTxt.visibility = View.VISIBLE
            welcomeSignOutTxt.text = "Çıkış yap"
        } else {
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeProfileTxt.visibility = View.INVISIBLE
            welcomeSignOutTxt.text = "Giriş yap"
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun bus1Clicked(view:View) {
        val businessIntent = Intent(this, BusinessActivity::class.java)
        businessIntent.putExtra("businessID", "foGBIPplqyVz2t74FrniVwhB7cs2")
        startActivity(businessIntent)
    }

    fun bus2Clicked(view: View){
        val businessIntent = Intent(this, BusinessActivity::class.java)
        businessIntent.putExtra("businessID", "FNp4fvEiLddMv9GxQx0m8GPnpn02")
        startActivity(businessIntent)
    }

    fun bus3Clicked(view:View){
        val businessIntent = Intent(this, BusinessActivity::class.java)
        businessIntent.putExtra("businessID", "NPjYtdWQO8UeOFZN6u6BAmZpYHW2")
        startActivity(businessIntent)
    }

    fun ins1Clicked(view: View){
        val inspectorProfileIntent = Intent(this, ProfileActivity::class.java)
        inspectorProfileIntent.putExtra("inspectorID", "3zYee7tlBCfPspXv448QZnr4kem1")
        startActivity(inspectorProfileIntent)
    }

    fun ins2Clicked(view: View){
        val inspectorProfileIntent = Intent(this, ProfileActivity::class.java)
        inspectorProfileIntent.putExtra("inspectorID", "hxdF7ZW4xQYIrw1V6adH877YDHi1")
        startActivity(inspectorProfileIntent)
    }

    fun ins3Clicked(view: View){
        val inspectorProfileIntent = Intent(this, ProfileActivity::class.java)
        inspectorProfileIntent.putExtra("inspectorID", "GcCWNoZRKldlrVUFiOnRytOjbU52")
        startActivity(inspectorProfileIntent)
    }

    fun welcomeExitClicked(view: View) {
        if (auth.currentUser != null) {
            Firebase.auth.signOut()
            welcomeSignOutTxt.text = "Giriş yap"
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeProfileTxt.visibility = View.INVISIBLE
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
        }
    }

    fun profileClicked(view: View) {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    fun araClicked(view:View) {
        if (welcomeAraTxt.text.toString().isNotEmpty()) {
            val araIntent = Intent(this, SearchActivity::class.java)
            araIntent.putExtra("query", welcomeAraTxt.text.toString())
            startActivity(araIntent)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeProfileTxt.visibility = View.INVISIBLE
            welcomeSignOutTxt.text = "Giriş yap"
        } else {
            welcomeSignOutTxt.text = "Çıkış yap"
            var username: String
            val db = currentUser.uid.let { database.collection("users").document(it) }
            db.get().addOnSuccessListener {
                if (it.get("username") != null) {
                    username = it.get("username") as String
                    welcomeUserNameTxt.text = username
                    welcomeUserNameTxt.visibility = View.VISIBLE
                    welcomeProfileTxt.visibility = View.VISIBLE
                } else {
                    database.collection("business").document(currentUser.uid).get()
                        .addOnSuccessListener { document ->
                            Log.d("HEY", currentUser.uid)
                            if (document.exists()) {
                                username = document.get("username") as String
                                welcomeUserNameTxt.text = username
                                welcomeUserNameTxt.visibility = View.VISIBLE
                                welcomeProfileTxt.visibility = View.VISIBLE
                            } else {
                                database.collection("inspectors").document(currentUser.uid).get()
                                        .addOnSuccessListener { documentins ->
                                            if (documentins.exists()) {
                                                username = documentins.get("username") as String
                                                welcomeUserNameTxt.text = username
                                                welcomeUserNameTxt.visibility = View.VISIBLE
                                                welcomeProfileTxt.visibility = View.VISIBLE
                                            }
                                        }
                            }
                        }
                }
            }
        }
    }
}


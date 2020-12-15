package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ege.coronato.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    private var user = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val username = intent.getStringExtra("Username")
        user = intent.getBooleanExtra("User", false)
        if(user) {
            welcomeUserNameTxt.text = username
            welcomeUserNameTxt.visibility = View.VISIBLE
            welcomeSignOutTxt.text = "Çıkış yap"
        } else {
            welcomeUserNameTxt.visibility = View.INVISIBLE
            welcomeSignOutTxt.text = "Giriş yap"
        }
    }
    fun welcomeExitClicked(view: View) {
        if (user) {
            Firebase.auth.signOut()
            user = false
            welcomeSignOutTxt.text = "Giriş yap"
            welcomeUserNameTxt.visibility = View.INVISIBLE
        } else {
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
        }
    }
}


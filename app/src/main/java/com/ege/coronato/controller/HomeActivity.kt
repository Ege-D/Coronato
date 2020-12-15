package com.ege.coronato.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ege.coronato.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loginBtn.visibility = View.INVISIBLE
        userNameTxt.visibility = View.INVISIBLE
        pwdTxt.visibility = View.INVISIBLE
    }

    fun loginUserClicked(view: View) {
        loginUserBtn.visibility = View.INVISIBLE
        loginGoogleBtn.visibility = View.INVISIBLE
        registerBtn.visibility = View.INVISIBLE
        loginBtn.visibility = View.VISIBLE
        userNameTxt.visibility = View.VISIBLE
        pwdTxt.visibility = View.VISIBLE
    }

    fun registerBtnClicked(view: View) {}

    fun loginBtnClicked(view: View) {}
}


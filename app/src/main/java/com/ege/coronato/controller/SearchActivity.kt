package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ege.coronato.R
import com.ege.coronato.adapters.SearchAdapter
import com.ege.coronato.models.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_business.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var searchAdapter: SearchAdapter
    lateinit var database: FirebaseFirestore
    var results = ArrayList<Result>()
    private lateinit var query: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        linearLayoutManager = LinearLayoutManager(this)
        searchResultList.layoutManager = linearLayoutManager
        searchAdapter = SearchAdapter(this, results)
        searchResultList.adapter = searchAdapter
        query = intent.getStringExtra("query").toString()
        database = Firebase.firestore
    }

    override fun onStart() {
        super.onStart()
        results.clear()
        searchAdapter.notifyDataSetChanged()
        database.collection("business").whereArrayContains("businessSearch", query).get()
            .addOnSuccessListener { documents->
                for(document in documents) {
                    runOnUiThread {
                        results.add(Result(document.getString("username"), document.id))
                        searchAdapter.notifyItemInserted((results.size - 1))
                    }
                }

            }
    }

    fun backClicked(view: View){
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeIntent)
    }
}
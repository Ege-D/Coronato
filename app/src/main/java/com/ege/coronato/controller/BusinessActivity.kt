package com.ege.coronato.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ege.coronato.R
import com.ege.coronato.adapters.PostAdapter
import com.ege.coronato.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_business.*
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class BusinessActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var postAdapter: PostAdapter
    lateinit var data: Post
    lateinit var database: FirebaseFirestore
    var posts = ArrayList<Post>()
    private lateinit var currentBusinessID: String
    lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        var splash = ""
        data = Post("", "", 123123123)
        currentBusinessID = intent.getStringExtra("businessID").toString()
        auth = Firebase.auth
        database = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        storage = Firebase.storage
        linearLayoutManager = LinearLayoutManager(this)
        userPostList.layoutManager = linearLayoutManager
        postAdapter = PostAdapter(this, posts)
        userPostList.adapter = postAdapter
        database.collection("business").document(currentBusinessID).get().addOnSuccessListener { document ->
            businessName.text = document.get("username").toString()
            businessAdres.text = document.get("adres").toString()
            businessDesc.text = document.get("desc").toString()
            splash = document.get("splash").toString()
            storage.reference.child("images/$splash").downloadUrl.addOnSuccessListener {url ->
                Glide.with(this)
                        .load(url)
                        .override(400, 300)
                        .centerCrop()
                        .into(businessSplash)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        posts.clear()
        database.collection("comments").whereEqualTo("businessID", currentBusinessID).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("PUT", "Listen failed.", e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                data.title = doc.getString("name")
                data.body = doc.getString("comment")
                data.timeStamp = doc.getString("time")?.toLong()
                posts.add(data)
                postAdapter.notifyItemInserted(posts.size-1)
            }
            postAdapter.notifyDataSetChanged()
        }
    }

    fun businessBackClicked (view: View) {
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeIntent)
    }
}
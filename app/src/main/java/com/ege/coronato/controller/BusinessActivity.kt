package com.ege.coronato.controller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ege.coronato.R
import com.ege.coronato.adapters.PostAdapter
import com.ege.coronato.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_business.*
import com.bumptech.glide.Glide
import com.ege.coronato.adapters.PointAdapter
import com.ege.coronato.models.Point
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class BusinessActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var linearLayoutManagerScore: LinearLayoutManager
    private lateinit var alertDialog: AlertDialog
    lateinit var postAdapter: PostAdapter
    lateinit var pointAdapter: PointAdapter
    lateinit var database: FirebaseFirestore
    var posts = ArrayList<Post>()
    var points = ArrayList<Point>()
    var showPoint = false
    private lateinit var currentBusinessID: String
    lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        var splash = ""
        currentBusinessID = intent.getStringExtra("businessID").toString()
        auth = Firebase.auth
        database = Firebase.firestore
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        if (auth.currentUser != null) {
            auth.currentUser?.let { database.collection("business").document(it.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            businessCommentBtn.visibility = View.INVISIBLE
                            commentSendTxt.visibility = View.INVISIBLE
                        } else {
                            businessCommentBtn.visibility = View.VISIBLE
                            commentSendTxt.visibility = View.VISIBLE
                            database.collection("inspectors").document(it.uid).get()
                                    .addOnSuccessListener  { documentins ->
                                        if (documentins.exists()) {
                                            healthScoreTitle.visibility = View.INVISIBLE
                                            healthScoreVal.visibility = View.INVISIBLE
                                            pointSee.visibility = View.INVISIBLE
                                            givePointBtn.visibility = View.VISIBLE
                                            database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").get()
                                                .addOnSuccessListener { documentScore ->
                                                    if (documentScore.exists()) {
                                                        insScore.text = documentScore.getString("score")
                                                    }
                                                }
                                            insScore.visibility = View.VISIBLE
                                        }

                                    }
                        }
                    }
            }
        } else {
            businessCommentBtn.visibility = View.INVISIBLE
            commentSendTxt.visibility = View.INVISIBLE
        }
        storage = Firebase.storage
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManagerScore = LinearLayoutManager(this)
        commentList.layoutManager = linearLayoutManager
        busScoreList.layoutManager = linearLayoutManagerScore
        postAdapter = PostAdapter(this, posts)
        pointAdapter = PointAdapter(this, points)
        commentList.adapter = postAdapter
        busScoreList.adapter = pointAdapter
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
        updateScore()

    }

    override fun onStart() {
        super.onStart()
        posts.clear()
        points.clear()
        postAdapter.notifyDataSetChanged()
        pointAdapter.notifyDataSetChanged()
        database.collection("comments").whereEqualTo("businessID", currentBusinessID).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("PUT", "Listen failed.", e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                runOnUiThread {
                    posts.add(
                        Post(
                            doc.getString("name"),
                            doc.getString("comment"),
                            doc.getString("time")?.toLong()
                        )
                    )
                    postAdapter.notifyItemInserted((posts.size - 1))
                }
            }

        }
        database.collection("scores").whereEqualTo("businessID", currentBusinessID).addSnapshotListener { value, e ->
            if (e != null) {
                Log.w("PUT", "Listen failed.", e)
                return@addSnapshotListener
            }
            for (doc in value!!) {
                runOnUiThread {
                    points.add(
                            Point(
                                    doc.getString("name"),
                                    doc.getString("score"),
                                    doc.getString("time")?.toLong()
                            )
                    )
                    pointAdapter.notifyItemInserted(points.size - 1)
                }
            }
        }
    }

    private fun updateScore () {
        var score: Float = 0.0F
        database.collection("scores").whereEqualTo("businessID", currentBusinessID).get().addOnSuccessListener { documents ->
            var scoreString: String
            var i = 0
            for (document in documents) {
                scoreString = document.getString("score").toString()
                score += scoreString.toFloat()
                i++
            }
            score /= i
            healthScoreVal.text = score.toString()
        }
    }

    fun businessBackClicked (view: View) {
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeIntent)
    }

    fun commentSendClicked (view: View) {
        if (commentSendTxt.text.toString().isEmpty()) {
            Toast.makeText(baseContext, "Yorum kısmı boş bırakılamaz.",
                    Toast.LENGTH_SHORT).show()
        } else {
            val comment = commentSendTxt.text.toString()
            commentSendTxt.text.clear()
            auth.currentUser?.let { database.collection("users").document(it.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val username = document.getString("username").toString()
                            val data = hashMapOf(
                                    "businessID" to currentBusinessID,
                                    "comment" to comment,
                                    "name" to username,
                                    "time" to System.currentTimeMillis().toString()
                            )
                            posts.clear()
                            postAdapter.notifyDataSetChanged()
                            database.collection("comments").add(data)
                        } else {
                            database.collection("inspectors").document(it.uid).get()
                                    .addOnSuccessListener { documentins ->
                                        if (documentins.exists()) {
                                            val username = documentins.getString("username").toString()
                                            val data = hashMapOf(
                                                    "businessID" to currentBusinessID,
                                                    "comment" to comment,
                                                    "name" to "$username (denetleyici)",
                                                    "time" to System.currentTimeMillis().toString()
                                            )
                                            posts.clear()
                                            postAdapter.notifyDataSetChanged()
                                            database.collection("comments").add(data)
                                        }
                                    }
                        }
                    }
                    }

        }
    }

    fun saglikClicked (view: View) {
        if (showPoint) {
            pointSee.text = "Puanları görmek için tıklayın."
            busScoreList.visibility = View.INVISIBLE
            businessDesc.visibility = View.VISIBLE
            busDescTitleTxt.visibility = View.VISIBLE
            showPoint = false
        } else {
            pointSee.text = "Puanları gizlemek için tıklayın."
            busScoreList.visibility = View.VISIBLE
            businessDesc.visibility = View.INVISIBLE
            busDescTitleTxt.visibility = View.INVISIBLE
            showPoint = true
        }
    }

    fun showCustomDialog (context: Context) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_custom_view, null)
        val Btn1: Button = dialogView.findViewById(R.id.Btn1)
        val Btn2: Button = dialogView.findViewById(R.id.Btn2)
        val Btn3: Button = dialogView.findViewById(R.id.Btn3)
        val Btn4: Button = dialogView.findViewById(R.id.Btn4)
        val Btn5: Button = dialogView.findViewById(R.id.Btn5)
        Btn1.setOnClickListener {
            auth.currentUser?.let { database.collection("inspectors").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username").toString()
                        val score = hashMapOf(
                            "businessID" to currentBusinessID,
                            "name" to username,
                            "score" to "1.0",
                            "time" to System.currentTimeMillis().toString()
                        )
                        points.clear()
                        pointAdapter.notifyDataSetChanged()
                        database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").set(score)
                        insScore.text = "1.0"
                    }
                }}
            alertDialog.dismiss()
        }
        Btn2.setOnClickListener {
            auth.currentUser?.let { database.collection("inspectors").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username").toString()
                        val score = hashMapOf(
                            "businessID" to currentBusinessID,
                            "name" to username,
                            "score" to "2.0",
                            "time" to System.currentTimeMillis().toString()
                        )
                        points.clear()
                        pointAdapter.notifyDataSetChanged()
                        database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").set(score)
                        insScore.text = "2.0"
                    }
                }}
            alertDialog.dismiss()
        }
        Btn3.setOnClickListener {
            auth.currentUser?.let { database.collection("inspectors").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username").toString()
                        val score = hashMapOf(
                            "businessID" to currentBusinessID,
                            "name" to username,
                            "score" to "3.0",
                            "time" to System.currentTimeMillis().toString()
                        )
                        points.clear()
                        pointAdapter.notifyDataSetChanged()
                        database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").set(score)
                        insScore.text = "3.0"
                    }
                }}
            alertDialog.dismiss()
        }
        Btn4.setOnClickListener {
            auth.currentUser?.let { database.collection("inspectors").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username").toString()
                        val score = hashMapOf(
                            "businessID" to currentBusinessID,
                            "name" to username,
                            "score" to "4.0",
                            "time" to System.currentTimeMillis().toString()
                        )
                        points.clear()
                        pointAdapter.notifyDataSetChanged()
                        database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").set(score)
                        insScore.text = "4.0"
                    }
                }}
            alertDialog.dismiss()
        }
        Btn5.setOnClickListener {
            auth.currentUser?.let { database.collection("inspectors").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username").toString()
                        val score = hashMapOf(
                            "businessID" to currentBusinessID,
                            "name" to username,
                            "score" to "5.0",
                            "time" to System.currentTimeMillis().toString()
                        )
                        points.clear()
                        pointAdapter.notifyDataSetChanged()
                        database.collection("scores").document("${auth.currentUser!!.uid}_${currentBusinessID}").set(score)
                        insScore.text = "5.0"
                    }
                }}
            alertDialog.dismiss()
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogBuilder.setOnDismissListener {}
        dialogBuilder.setView(dialogView)
        alertDialog = dialogBuilder.create();
        alertDialog.show()
    }

    fun givePointClicked (view: View) {
        showCustomDialog(this)
    }
}
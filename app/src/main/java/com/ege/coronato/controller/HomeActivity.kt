package com.ege.coronato.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ege.coronato.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        database = Firebase.firestore
        loginBtn.visibility = View.INVISIBLE
        homeBackBtn.visibility = View.INVISIBLE
        userNameTxt.visibility = View.GONE
        pwdTxt.visibility = View.GONE
        loginGoogleBtn.visibility = View.VISIBLE
        registerBtn.visibility = View.VISIBLE
        loginUserBtn.visibility = View.VISIBLE
        noLoginBtn.visibility = View.VISIBLE
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso);
        loginGoogleBtn.setOnClickListener(GoogleSignInListener(googleSignInClient))
        auth = Firebase.auth
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun loginUserClicked(view: View) {
        loginUserBtn.visibility = View.INVISIBLE
        loginGoogleBtn.visibility = View.INVISIBLE
        registerBtn.visibility = View.INVISIBLE
        noLoginBtn.visibility = View.INVISIBLE
        loginBtn.visibility = View.VISIBLE
        homeBackBtn.visibility = View.VISIBLE
        userNameTxt.visibility = View.VISIBLE
        pwdTxt.visibility = View.VISIBLE
    }

    fun noLoginClicked(view: View) {
        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
        startActivity(welcomeIntent)
    }

    fun registerBtnClicked(view: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    fun loginBtnClicked(view: View) {
        if (userNameTxt.text.toString().isEmpty() || pwdTxt.text.toString().isEmpty()) {
            Toast.makeText(baseContext, "Giriş yapılamadı.",
                    Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(userNameTxt.text.toString(), pwdTxt.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUI(user)
                            val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                            startActivity(welcomeIntent)
                        } else {
                            Log.w("LRR", "signInWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Giriş yapılamadı.",
                                    Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }

                    }
        }
    }

    fun geriClicked(view: View) {
        loginBtn.visibility = View.INVISIBLE
        homeBackBtn.visibility = View.INVISIBLE
        userNameTxt.visibility = View.GONE
        pwdTxt.visibility = View.GONE
        loginGoogleBtn.visibility = View.VISIBLE
        registerBtn.visibility = View.VISIBLE
        loginUserBtn.visibility = View.VISIBLE
        noLoginBtn.visibility = View.VISIBLE
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val welcomeIntent = Intent(this, WelcomeActivity::class.java)
            startActivity(welcomeIntent)
        }
    }

    inner class GoogleSignInListener(private val googleSignInClient : GoogleSignInClient) : View.OnClickListener {
        override fun onClick (view: View) {
            when (view.id) {
                R.id.loginGoogleBtn -> signIn()
            }

        }
        private fun signIn() {
            val signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("SCS:", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERR:", "Google sign in failed", e)
                // ...
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SCS", "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        Snackbar.make(homeLayout, "Authentication Success.", Snackbar.LENGTH_SHORT).show()
                        //textView.text = user.displayName.toString()
                        val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                        val username = user.displayName.toString()
                        val userId = user.uid
                        val email = user.email
                        val userStore = hashMapOf(
                            "username" to username,
                            "email" to email
                        )
                        database.collection("users").document(userId)
                            .set(userStore)
                            .addOnSuccessListener { Log.d("STS", "DocumentSnapshot successfully written!") }
                            .addOnFailureListener { e -> Log.w("STR", "Error writing document", e) }
                        welcomeIntent.putExtra("Username", username)
                        startActivity(welcomeIntent)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ERR:", "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(homeLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    //textView.text = "Hello World"
                    loginGoogleBtn.visibility = View.VISIBLE
                    registerBtn.visibility = View.VISIBLE
                    loginUserBtn.visibility = View.VISIBLE
                    noLoginBtn.visibility = View.VISIBLE
                    loginBtn.visibility = View.INVISIBLE
                    homeBackBtn.visibility = View.INVISIBLE
                    userNameTxt.visibility = View.GONE
                    pwdTxt.visibility = View.GONE

                }

                // ...
            }
    }
}


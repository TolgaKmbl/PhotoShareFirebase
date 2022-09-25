package com.tolgakumbul.photosharefirebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null) {
            changeActivity()
        }

    }

    fun login(view:View){
        val email = findViewById<EditText>(R.id.emailText)
        val password = findViewById<EditText>(R.id.passwordText)

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser?.email.toString()
                    Toast.makeText(this, "Welcome $currentUser",
                        Toast.LENGTH_LONG).show()
                    changeActivity()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage,
                    Toast.LENGTH_LONG).show()
            }
    }

    fun signIn(view:View){
        val email = findViewById<EditText>(R.id.emailText)
        val password = findViewById<EditText>(R.id.passwordText)

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    changeActivity()
                }/* else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_LONG).show()
                }*/
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.localizedMessage,
                    Toast.LENGTH_LONG).show()
            }
    }

    private fun changeActivity() {
        val intent = Intent(this, PhotoFeedActivity::class.java)
        startActivity(intent)
        finish() //onDestroy() çağırabilmek için
    }
}
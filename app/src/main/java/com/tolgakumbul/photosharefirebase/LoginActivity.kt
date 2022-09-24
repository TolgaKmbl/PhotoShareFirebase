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
    }

    fun login(view:View){

    }

    fun signIn(view:View){
        val email = findViewById<EditText>(R.id.emailText)
        val password = findViewById<EditText>(R.id.passwordText)

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //val user = auth.currentUser
                    val intent = Intent(this, PhotoFeedActivity::class.java)
                    startActivity(intent)
                    finish() //onDestroy() çağırabilmek için
                }/* else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_LONG).show()
                }*/
            }
            .addOnFailureListener { exception ->
                Toast.makeText(baseContext, exception.localizedMessage,
                    Toast.LENGTH_LONG).show()
            }
    }
}
package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var usernameEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    val database = Firebase.database("https://snapchatclone-8bef3-default-rtdb.asia-southeast1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }

    fun loginClicked(view: View) {
        // Check if we can login the user
        mAuth.signInWithEmailAndPassword(usernameEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    //Sign up the user
                    mAuth.createUserWithEmailAndPassword(usernameEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
                                database.reference.child("users").child(task.result!!.user?.uid!!).child("email").setValue(usernameEditText?.text.toString())
                                logIn()
                            } else {
                                Toast.makeText(this, "Login failed! Please try again!", Toast.LENGTH_SHORT).show()
                                Log.i("Info", task.exception.toString())
                            }
                        }
                }
            }
    }

    fun logIn() {
        // Move to next activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}
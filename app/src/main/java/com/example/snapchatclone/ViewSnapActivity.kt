package com.example.snapchatclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.net.HttpURLConnection
import java.net.URL



class ViewSnapActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    val mAuth = FirebaseAuth.getInstance()
    val database = Firebase.database("https://snapchatclone-8bef3-default-rtdb.asia-southeast1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.snapImageView)

        messageTextView?.text = intent.getStringExtra("message")


        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapImageView?.setImageBitmap(myImage)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


    }

    inner class ImageDownloader: AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String?): Bitmap? {

            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val `in` = connection.inputStream

                return BitmapFactory.decodeStream(`in`)

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()

        database.reference.child("users").child(mAuth.currentUser?.uid!!).child("snaps").child(intent.getStringExtra("snapKey")!!).removeValue()
        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")!!).delete()
    }
}
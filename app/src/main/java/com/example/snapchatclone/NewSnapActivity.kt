package com.example.snapchatclone

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.jar.Manifest

class NewSnapActivity : AppCompatActivity() {

    var newSnapImageView: ImageView? = null
    var messageEditText: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_snap)

        newSnapImageView = findViewById(R.id.newSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    fun getPhoto(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImage(view: View){
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                newSnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun nextClicked(view: View) {
        // Get the data from an ImageView as bytes
        newSnapImageView?.isDrawingCacheEnabled = true
        newSnapImageView?.buildDrawingCache()
        val bitmap = (newSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val ref = FirebaseStorage.getInstance().reference.child("images").child(imageName)
        var uploadTask = ref.putBytes(data)

        var urlTask = uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let{
                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("imageName", imageName)
                intent.putExtra("imageURL", task.result.toString())
                intent.putExtra("message", messageEditText?.text.toString())
                startActivity(intent)
            }
        }


        }
    }
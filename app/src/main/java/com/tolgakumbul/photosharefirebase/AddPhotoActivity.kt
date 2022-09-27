package com.tolgakumbul.photosharefirebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    var pickedImage: Uri? = null
    var pickedBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun pickPhoto(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)
        }
    }

    fun addPhoto(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)
        if (imageReference != null) {
            imageReference.putFile(pickedImage!!).addOnSuccessListener {
                val uploadedImgRef =
                    FirebaseStorage.getInstance().reference.child("images").child(imageName)
                uploadedImgRef.downloadUrl.addOnSuccessListener {
                    val downloadUri = it.toString()
                    val currentUserMail = auth.currentUser!!.email.toString()
                    val commentText = findViewById<EditText>(R.id.commentText)
                    val userComment = commentText.text.toString()
                    val time = Timestamp.now()

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("imageUrl", downloadUri)
                    postHashMap.put("currentUserMail", currentUserMail)
                    postHashMap.put("userComment", userComment)
                    postHashMap.put("time", time)
                    database.collection("Post").add(postHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                applicationContext,
                                exception.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    android.content.Intent(
                        android.content.Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                startActivityForResult(galleryIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            pickedImage = data.data
            if (pickedImage != null) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, pickedImage!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(pickedBitmap)
                } else {
                    pickedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, pickedImage)
                    imageView.setImageBitmap(pickedBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
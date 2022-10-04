package com.tolgakumbul.photosharefirebase.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.tolgakumbul.photosharefirebase.R
import com.tolgakumbul.photosharefirebase.adapter.PhotoFeedRecyclerAdapter
import com.tolgakumbul.photosharefirebase.model.Post

class PhotoFeedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter : PhotoFeedRecyclerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_feed)
        auth = Firebase.auth
        database = FirebaseFirestore.getInstance()

        getData()

        val layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = PhotoFeedRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //To connect menu with the activity
        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            //Logout from Firebase
            auth.signOut()
            //Back to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else if(item.itemId == R.id.add_photo){
            //Checkout to the add photo activity
            val intent = Intent(this, AddPhotoActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun getData() {
        database.collection("Post")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
            if(exception != null){
                Toast.makeText(this, exception.localizedMessage,
                    Toast.LENGTH_LONG).show()
            } else {
                if(snapshot != null && !snapshot.isEmpty) {
                    val documents = snapshot.documents
                    postList.clear()
                    for(document in documents) {
                        val currentUserMail = document.get("currentUserMail") as String
                        val userComment = document.get("userComment") as String
                        val imageUrl = document.get("imageUrl") as String

                        val downloadedPost = Post(currentUserMail, userComment, imageUrl)
                        postList.add(downloadedPost)
                    }
                    // To refresh the recyclerViewAdapter
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
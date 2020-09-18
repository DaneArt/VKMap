package com.daneart.vkmap.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FBStorage {

    private val dataBase = FirebaseFirestore.getInstance()


    fun addPost(post: Post) {
        val jsonPost = mapOf(
            "id" to post.id.toString(),
            "content" to post.content,
            "author" to post.author,
            "theme" to post.theme,
            "emotion" to post.emotion,
            "latitude" to post.latitude,
            "longitude" to post.longitude
        )
        dataBase.collection("posts")
            .add(jsonPost)
            .addOnCompleteListener {
                Log.d("TAG", "Completed adding")
            }
            .addOnCanceledListener {
                Log.d("TAG", "Canceled adding")
            }
    }

}
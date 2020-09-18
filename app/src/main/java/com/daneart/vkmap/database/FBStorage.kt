package com.daneart.vkmap.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

object FBStorage {

    private val dataBase = FirebaseFirestore.getInstance()


    fun addPost(post: Post) {
        val jsonPost = Gson().toJson(post)
        dataBase.collection("posts")
            .add(jsonPost)
            
    }

}
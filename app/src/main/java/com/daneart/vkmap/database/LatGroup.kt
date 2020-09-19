package com.daneart.vkmap.database

data class LatGroup(
    val emotion: String,
    val topThemes: ArrayList<Pair<String, Int>>,
    val mainPost: Post,
    val posts: ArrayList<Post>
)
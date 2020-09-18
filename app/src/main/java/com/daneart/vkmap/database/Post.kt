package com.daneart.vkmap.database

import android.net.Uri
import com.google.type.LatLng
import java.util.*

enum class Emotion(val rus:String){
    Anxiety("Беспокойное"),
    Action("Энергия"),
    HighEnergy(""),
    Positive(""),
    Calm(""),
    LowEnergy(""),
    Tired(""),
    Negative("")
}

enum class Theme(val rus:String){
    Art("Арт"),
    Photo("Фото"),
    Science("Наука"),
    Sport("Спорт"),
    Tourism("Туризм"),
    IT("IT"),
    Humour("Юмор"),
    Games("Игры"),
    Cinema("Кино"),
    Style("Стиль"),
    Music("Музыка"),
    Focus("Фокус"),
    Coronavirus("Коронавирус")
}

sealed class Post(val id:UUID, val content:Any?, val author: String, val theme: Theme, val emotion:Emotion, val latLng:LatLng) {
    class TextPost(id: UUID, textContent: String?, author: String, theme: Theme, emotion:Emotion,latLng:LatLng) : Post(id = id, content = textContent, author = author, theme = theme, emotion = emotion,latLng = latLng)
    class ImagePost(id: UUID, imageContent: Uri?, author: String,theme: Theme, emotion:Emotion,latLng:LatLng) : Post(id = id, content = imageContent, author = author, theme = theme,emotion = emotion,latLng = latLng)
}
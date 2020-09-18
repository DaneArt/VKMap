package com.daneart.vkmap.database

import com.google.type.LatLng
import java.io.Serializable
import java.util.*

enum class Emotion(val rus: String) {
    Positive("Хорошее"),
    Action("Активное"),
    HighEnergy("Гиперактивное"),
    Calm("Спокойное"),
    Anxiety("Беспокойное"),
    Tired("Усталое"),
    LowEnergy("Расслабленное"),
    Negative("Негативное")
}

enum class Theme(val rus: String) {
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

data class Post(
    val id: UUID,
    val content: String?,
    val author: String,
    val theme: String,
    val emotion: String,
    val latitude: Double,
    val longitude: Double
)

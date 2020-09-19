package com.daneart.vkmap.database

import com.google.type.LatLng
import java.io.Serializable
import java.util.*

enum class Emotion(val rus: String) {
    Positive("\uD83D\uDE0C Хорошее"),
    Action("\uD83D\uDE03 Активное"),
    HighEnergy("\uD83D\uDE1C Гиперактивное"),
    Calm("\uD83D\uDE10 Спокойное"),
    Anxiety("\uD83D\uDE41 Беспокойное"),
    Tired("\uD83D\uDE34 Усталое"),
    LowEnergy("\uD83E\uDD24 Расслабленное"),
    Negative("\uD83D\uDE21 Негативное")
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

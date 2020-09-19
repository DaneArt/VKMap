package com.daneart.vkmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daneart.vkmap.database.Emotion
import com.daneart.vkmap.database.Theme

class ThemesAdapter : RecyclerView.Adapter<ThemesAdapter.ThemesViewHolder>() {
    private val themeArr = Emotion.values().map { e -> Theme.values().map { it to e } }.flatten()
    private val themeEmojis = listOf<String>(
        "\uD83C\uDFA8",
        "\uD83D\uDCF7",
        "\uD83E\uDDEA",
        "\uD83C\uDFC5",
        "⛰️",
        "\uD83D\uDDA5️",
        "\uD83C\uDFAD",
        "\uD83C\uDFAE",
        "\uD83C\uDFA6",
        "\uD83D\uDC5A",
        "\uD83C\uDFBC",
        "\uD83C\uDFAF",
        "\uD83E\uDD12"
    )

    var displayedList = themeArr

    private var onClickListenter: (view: View, position: Int) -> Unit = { _, _ ->

    }

    inner class ThemesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.theme_item, parent, false)
        return ThemesViewHolder(view)
    }

    override fun getItemCount(): Int =
        displayedList.size


    override fun onBindViewHolder(holder: ThemesViewHolder, position: Int) {
        try{
            if(displayedList.isNotEmpty()){
                val themeBtn = holder.itemView.findViewById<Button>(R.id.btn_theme)
                val themeName = holder.itemView.findViewById<TextView>(R.id.txt_theme_name)
                val themeEmotion = holder.itemView.findViewById<TextView>(R.id.txt_theme_emotion)
                themeBtn.text = themeEmojis[displayedList[position].first.ordinal]
                themeBtn.setOnClickListener { onClickListenter(it, position) }
                themeName.text = displayedList[position].first.rus
                themeEmotion.text = displayedList[position].second.rus.split(" ").first()}
        }catch (e:Throwable){

        }

    }

    fun setOnClickListener(lambda: (view: View, position: Int) -> Unit) {
        onClickListenter = lambda
    }

    fun filterTheme(text: String) {

        if (text.isNotEmpty()) {
            displayedList =
                themeArr.filter {
                    it.first.rus.contains(text) || it.second.rus.split(" ")[1].contains(text) || it.first.rus.toLowerCase()
                        .contains(text) || it.second.rus.split(" ")[1].toLowerCase().contains(
                        text
                    ) || it.first.rus.toUpperCase().contains(text) || it.second.rus.split(" ")[1].toUpperCase()
                        .contains(text)
                }
        }else{
            displayedList = themeArr
        }
        notifyDataSetChanged()
    }
}
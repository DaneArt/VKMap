package com.daneart.vkmap

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class SpinnerAdapter(
    ctx: Activity,
    resource: Int,
    textViewId: Int,
    objects: MutableList<String>
) :
    ArrayAdapter<String>(ctx, resource, textViewId, objects) {

    var activePos = -1


    private val fluter = ctx.layoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return if (position == 0) {
            val view = fluter.inflate(R.layout.spinner_item, parent, false)

            val txt = view.findViewById<TextView>(R.id.txt_spinner_item)
            txt.text = getItem(position)
            view
        } else  {

            val view = fluter.inflate(R.layout.spinner_item_active, parent, false)

            val txt = view.findViewById<TextView>(R.id.txt_spinner_item_active)
            txt.text = getItem(position)
            view
        }


    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {


        if (position == 0) {

            return fluter.inflate(R.layout.empty_layout, parent, false)
        } else if (position == activePos) {

            var view: View = fluter.inflate(R.layout.spinner_txt_active, parent, false)

            val txt = view.findViewById<TextView>(R.id.txt_spinner_active)
            txt.text = getItem(position)
            return view
        } else {
            var view: View = fluter.inflate(R.layout.spinner_txt, parent, false)

            val txt = view.findViewById<TextView>(R.id.txt_spinner)
            txt.text = getItem(position)
            return view
        }
    }


    override fun isEnabled(position: Int): Boolean = position != 0

}
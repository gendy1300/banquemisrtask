package com.ahmedelgendy.banquemisrtask.convert.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes

class SpinnerAdapter(
    context: Context,
    @LayoutRes layout: Int,
    private val entries: Map<String, String>,
    val newSelectedPositions: (Int) -> Unit

) : ArrayAdapter<String>(context, layout, entries.keys.toList()) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val selectedItemPosition = when (parent) {
            is AdapterView<*> -> parent.selectedItemPosition
            else -> position
        }


            newSelectedPositions.invoke(position)


        return makeLayout(
            selectedItemPosition,
            convertView,
            parent,
            android.R.layout.simple_spinner_dropdown_item
        )
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {



        return makeLayout(
            position,
            convertView,
            parent,
            android.R.layout.simple_spinner_dropdown_item
        )
    }


    private fun makeLayout(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        layout: Int
    ): View {
        val tv = convertView ?: LayoutInflater.from(context).inflate(layout, parent, false)
        if (position != -1) {
            (tv as? TextView)?.text = entries.toList()[position].toString()
        }
        return tv
    }
}
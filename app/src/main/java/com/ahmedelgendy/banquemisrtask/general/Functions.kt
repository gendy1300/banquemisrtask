package com.ahmedelgendy.banquemisrtask.general

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ahmedelgendy.banquemisrtask.activities.MainActivity

fun showLongToast(msg: String, context: Context) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}

fun Fragment.showLoading(show:Boolean){
   (activity as MainActivity).showLoading(show)
}
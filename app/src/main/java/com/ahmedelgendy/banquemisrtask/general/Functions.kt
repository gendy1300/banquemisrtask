package com.ahmedelgendy.banquemisrtask.general

import android.content.Context
import android.widget.Toast

fun showLongToast(msg: String, context: Context) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG)

}
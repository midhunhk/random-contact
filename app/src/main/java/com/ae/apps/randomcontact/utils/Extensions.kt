package com.ae.apps.randomcontact.utils

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.remove(){
    this.visibility = View.GONE
}

fun Context.showShortToast(message : String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showShortToast(messageRes : Int){
    Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
}
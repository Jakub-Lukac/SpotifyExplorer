package com.example.spotifyexplorer.data.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.spotifyexplorer.R

fun showCustomToast(
    context: Context,
    message: String,
    iconRes: Int
) {
    val inflater = LayoutInflater.from(context)
    val layout = inflater.inflate(R.layout.custom_toast, null)

    val textView = layout.findViewById<TextView>(R.id.toast_text)
    val imageView = layout.findViewById<ImageView>(R.id.toast_icon)

    textView.text = message
    imageView.setImageResource(iconRes)

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
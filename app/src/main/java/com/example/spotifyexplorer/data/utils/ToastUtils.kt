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
    // Inflater creates a layout XML file into its corresponding View objects
    val inflater = LayoutInflater.from(context)
    // Inflate the custom toast layout
    val layout = inflater.inflate(R.layout.custom_toast, null)

    // Get the TextView and ImageView from the layout
    val textView = layout.findViewById<TextView>(R.id.toast_text)
    val imageView = layout.findViewById<ImageView>(R.id.toast_icon)

    // Set the text and icon for the toast
    textView.text = message
    imageView.setImageResource(iconRes)

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT // Duration of the toast, from the android.widget.Toast class
    toast.view = layout
    toast.show()
}
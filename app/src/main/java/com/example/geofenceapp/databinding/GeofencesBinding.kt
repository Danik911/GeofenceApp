package com.example.geofenceapp.databinding

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("loadSnapshot")
fun ImageView.loadSnapshot(bitmap: Bitmap){
    this.load(bitmap)
}

@BindingAdapter("formatCoordinates")
fun TextView.formatCoordinates(value: Double){
  val coordinate = String.format("%.4f", value)
    this.text = coordinate
}
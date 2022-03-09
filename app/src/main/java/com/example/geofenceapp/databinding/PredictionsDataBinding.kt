package com.example.geofenceapp.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.libraries.places.api.model.AutocompletePrediction


@BindingAdapter("setCityText")
fun TextView.setCity(prediction: AutocompletePrediction) {
    this.text = prediction.getPrimaryText(null).toString()
}

@BindingAdapter("setCountryText")
fun TextView.setCounty(prediction: AutocompletePrediction) {
    this.text = prediction.getSecondaryText(null).toString()
}
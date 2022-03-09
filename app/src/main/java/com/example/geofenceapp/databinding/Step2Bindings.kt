package com.example.geofenceapp.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.geofenceapp.util.ExtensionFunctions.hide
import com.example.geofenceapp.util.ExtensionFunctions.show
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorHandler", "recyclerViewHandler", requireAll = true)
fun TextInputLayout.errorHandler(internetConnectionAvailable: Boolean, recyclerView: RecyclerView){
    if (!internetConnectionAvailable){
        this.isErrorEnabled = true
        this.error = "No Internet Connection"
        recyclerView.hide()
    } else {
        this.isErrorEnabled = false
        this.error = null
        recyclerView.show()
    }
}
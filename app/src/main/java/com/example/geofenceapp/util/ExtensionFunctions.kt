package com.example.geofenceapp.util

import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object ExtensionFunctions {
    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
    fun View.hide(){
        this.visibility = View.GONE
    }
    fun View.show(){
        this.visibility = View.VISIBLE
    }
}
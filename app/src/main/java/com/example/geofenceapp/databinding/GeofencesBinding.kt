package com.example.geofenceapp.databinding

import android.graphics.Bitmap
import android.opengl.Visibility
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.BindingAdapter
import coil.load
import com.example.geofenceapp.R
import com.example.geofenceapp.data.GeofenceEntity
import com.example.geofenceapp.util.ExtensionFunctions.disable
import com.example.geofenceapp.util.ExtensionFunctions.enable

@BindingAdapter("loadSnapshot")
fun ImageView.loadSnapshot(bitmap: Bitmap) {
    this.load(bitmap)
}

@BindingAdapter("formatCoordinates")
fun TextView.formatCoordinates(value: Double) {
    val coordinate = String.format("%.4f", value)
    this.text = coordinate
}

@BindingAdapter("handleViews")
fun View.handleViews(data: List<GeofenceEntity>?) {
    if (data.isNullOrEmpty()) {
        this.visibility = View.INVISIBLE
    } else {
        this.visibility = View.VISIBLE
    }

}


@BindingAdapter("handleDeleteIcon")
fun MotionLayout.handleDeleteIcon(deleteImageView: ImageView) {
    deleteImageView.disable()
    this.setTransitionListener(object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

        }

        override fun onTransitionChange(
            motionLayout: MotionLayout?,
            startId: Int,
            endId: Int,
            progress: Float
        ) {

        }

        override fun onTransitionTrigger(
            motionLayout: MotionLayout?,
            triggerId: Int,
            positive: Boolean,
            progress: Float
        ) {
        }

        override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
            if (motionLayout != null && currentId == R.id.start) {
                deleteImageView.disable()
            } else if (motionLayout != null && currentId == R.id.end) {
                deleteImageView.enable()
            }
        }

    })
}
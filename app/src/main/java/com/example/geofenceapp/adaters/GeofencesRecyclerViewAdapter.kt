package com.example.geofenceapp.adaters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.geofenceapp.data.GeofenceEntity
import com.example.geofenceapp.databinding.GeofencesRowLayoutBinding
import com.example.geofenceapp.ui.geofences.GeofenceFragmentDirections
import com.example.geofenceapp.util.MyDiffUtil
import com.example.geofenceapp.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
class GeofencesRecyclerViewAdapter(private val sharedViewModel: SharedViewModel) :
    RecyclerView.Adapter<GeofencesRecyclerViewAdapter.MyViewHolder>() {


    private var geofenceEntity = mutableListOf<GeofenceEntity>()

    class MyViewHolder(val binding: GeofencesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(geofenceEntity: GeofenceEntity) {
            binding.geofencesEntity = geofenceEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GeofencesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentGeofence = geofenceEntity[position]
        holder.bind(currentGeofence)

        holder.binding.deleteImageView.setOnClickListener {
            removeItem(holder, position)
        }
        holder.binding.snapshotImageView.setOnClickListener {
            val action =
                GeofenceFragmentDirections.actionGlobalMapsFragment(currentGeofence)
            holder.itemView.findNavController().navigate(action)
        }

    }

    private fun removeItem(
        holder: MyViewHolder,
        position: Int
    ) {
        sharedViewModel.viewModelScope.launch {
            val geofencesStopped =
                sharedViewModel.stopGeofence(listOf(geofenceEntity[position].geofenceId))
            if (geofencesStopped) {
                sharedViewModel.deleteGeofence(geofenceEntity[position])
                sharedViewModel.geofenceStopped = true
                showSnackBar(holder, geofenceEntity[position])
            } else {
                Log.d("GeofenceAdapter", "Item has not been removed")
            }
        }
    }

    private fun showSnackBar(
        holder: MyViewHolder,
        removedGeofence: GeofenceEntity
    ) {
        Snackbar.make(
            holder.itemView,
            "Removed" + removedGeofence.name,
            Snackbar.LENGTH_LONG
        ).setAction("UNDO") {
            restoreGeofence(holder, removedGeofence)
        }.show()
    }


    private fun restoreGeofence(
        holder: MyViewHolder,
        removedGeofence: GeofenceEntity
    ) {
        sharedViewModel.insertGeofence(removedGeofence)
        sharedViewModel.startGeofence(removedGeofence.latitude, removedGeofence.longitude)
        sharedViewModel.geofenceStopped = false
    }

    override fun getItemCount(): Int {
        return geofenceEntity.size
    }

    fun setData(newGeofenceEntity: MutableList<GeofenceEntity>) {
        val geofenceDiffUtil = MyDiffUtil(geofenceEntity, newGeofenceEntity)
        val diffUtilResult = DiffUtil.calculateDiff(geofenceDiffUtil)
        geofenceEntity = newGeofenceEntity
        diffUtilResult.dispatchUpdatesTo(this)
    }
}

package com.example.geofenceapp.adaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.geofenceapp.data.GeofenceEntity
import com.example.geofenceapp.databinding.GeofencesRowLayoutBinding
import com.example.geofenceapp.util.MyDiffUtil
import com.example.geofenceapp.viewmodels.SharedViewModel

class GeofencesRecyclerViewAdapter {
    class GeofencesAdapter(private val sharedViewModel: SharedViewModel) :
        RecyclerView.Adapter<GeofencesAdapter.MyViewHolder>() {

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
}
package com.example.geofenceapp.data

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.geofenceapp.util.Constants.TABLE_ENTITY_NAME


@Entity(tableName = TABLE_ENTITY_NAME)
class GeofenceEntity(
    val geofenceId: Long,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val snapshot: Bitmap
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
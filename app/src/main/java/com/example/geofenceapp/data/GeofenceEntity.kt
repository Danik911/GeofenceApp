package com.example.geofenceapp.data

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.NonParcelField
import com.example.geofenceapp.util.Constants.TABLE_ENTITY_NAME
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.IgnoredOnParcel


@Parcelize
@Entity(tableName = TABLE_ENTITY_NAME)
class GeofenceEntity(
    val geofenceId: Long,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val snapshot: Bitmap
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
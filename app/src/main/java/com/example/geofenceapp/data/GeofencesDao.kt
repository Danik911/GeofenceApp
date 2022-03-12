package com.example.geofenceapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofencesDao {

    @Query("SELECT * FROM geofence_entity ORDER BY id ASC")
    fun loadAllGeofences(): Flow<MutableList<GeofenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeafence(geofence: GeofenceEntity)

    @Delete
    suspend fun deleteGeofence(geofence: GeofenceEntity)
}
package com.example.geofenceapp.data

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GeofenceRepository @Inject constructor(private val geofencesDao: GeofencesDao) {

    val allGeofences: Flow<MutableList<GeofenceEntity>> = geofencesDao.loadAllGeofences()

    suspend fun insertGeofence(geofenceEntity: GeofenceEntity) {
        geofencesDao.insertGeafence(geofenceEntity)
    }

    suspend fun deleteGeofence(geofenceEntity: GeofenceEntity) {
        geofencesDao.deleteGeofence(geofenceEntity)
    }

}
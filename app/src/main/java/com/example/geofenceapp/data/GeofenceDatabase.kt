package com.example.geofenceapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GeofenceEntity::class], version = 1, exportSchema = false)
abstract class GeofenceDatabase : RoomDatabase() {

    abstract fun geofenceDao(): GeofencesDao

}
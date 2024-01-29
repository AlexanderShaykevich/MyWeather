package com.example.myweather.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}

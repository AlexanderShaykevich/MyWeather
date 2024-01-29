package com.example.myweather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherEntity ORDER BY 1")
    fun getAll(): List<WeatherEntity>

    @Insert
    fun insert(city: WeatherEntity)
    @Query(
        """
        UPDATE WeatherEntity SET
        image = :image, `temp` = :temp
        WHERE id = :id
    """
    )
    fun update(id: Int, image: String, temp: Double)

    @Delete
    fun delete(city: WeatherEntity)

}
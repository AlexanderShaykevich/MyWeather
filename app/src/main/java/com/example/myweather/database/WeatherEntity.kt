package com.example.myweather.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myweather.data.FavoriteCity

@Entity
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "temp")
    val temp: Double,
    @ColumnInfo(name = "image")
    val image: String
)

internal fun WeatherEntity.toModel() = FavoriteCity(
    id = id,
    city = city,
    temp = temp,
    image = image,
)

internal fun FavoriteCity.toEntity() = WeatherEntity(
    id = id,
    city = city,
    temp = temp,
    image = image,
)


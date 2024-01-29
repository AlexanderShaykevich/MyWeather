package com.example.myweather.data

interface WeatherRepository {
    suspend fun getWeather(city: String): WeatherCurrent
    suspend fun getWeatherForecast(city: String, days: Int): WeatherForecast
}
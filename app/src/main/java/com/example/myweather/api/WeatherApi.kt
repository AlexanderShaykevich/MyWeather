package com.example.myweather.api

import com.example.myweather.data.WeatherCurrent
import com.example.myweather.data.WeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL_WEATHER = "http://api.weatherapi.com/v1/"
const val WEATHER_API_KEY = ""

interface WeatherApi {
    @GET("current.json?key=$WEATHER_API_KEY")
    suspend fun getCurrentWeather(@Query("q") city: String): Response<WeatherCurrent>

    @GET("forecast.json?key=$WEATHER_API_KEY")
    suspend fun getWeatherForecast(@Query("q") city: String, @Query("days") days: Int)
    : Response<WeatherForecast>
}



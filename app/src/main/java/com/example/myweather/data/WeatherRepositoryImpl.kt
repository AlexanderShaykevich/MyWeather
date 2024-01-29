package com.example.myweather.data

import android.content.Context
import com.example.myweather.R
import com.example.myweather.api.WeatherApi
import com.example.myweather.database.WeatherDao
import com.example.myweather.database.toEntity
import com.example.myweather.database.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val dao: WeatherDao,
    val context: Context
) : WeatherRepository {
    private val storagePrefs = context.getSharedPreferences(
        context.getString(R.string.storageName),
        Context.MODE_PRIVATE
    )
    private var storage = emptyList<FavoriteCity>()
    val data = MutableStateFlow(storage)

    init {
        getAllFavorites()
    }

    override suspend fun getWeather(city: String): WeatherCurrent {
        val response = api.getCurrentWeather(city)
        if (!response.isSuccessful) {
            if (response.code() == 400) {
                throw NoCityException()
            } else {
                throw RuntimeException(response.message())
            }
        }
        return response.body() ?: throw RuntimeException(response.message())
    }

    override suspend fun getWeatherForecast(city: String, days: Int): WeatherForecast {
        val response = api.getWeatherForecast(city, days)
        if (!response.isSuccessful) {
            if (response.code() == 400) {
                throw NoCityException()
            } else {
                throw RuntimeException(response.message())
            }
        }
        return response.body() ?: throw RuntimeException(response.message())
    }

    suspend fun checkCity(city: String): Boolean {
        val response = api.getCurrentWeather(city)
        return response.code() != 400
    }

    fun saveDefaultCity(city: String) {
        with(storagePrefs.edit()) {
            putString("location", city).apply()
        }
    }

    fun saveDurationForecast(number: Int) {
        with(storagePrefs.edit()) {
            putInt("duration", number).apply()
        }
    }

    fun saveDefaultColor(color: Int) {
        with(storagePrefs.edit()) {
            putInt("color", color).apply()
        }
    }

    fun saveLanguage(language: String) {
        with(storagePrefs.edit()) {
            putString("language", language).apply()
        }
    }

    fun getDefaultCity() = storagePrefs.getString("location", "Vaalimaa")!!
    fun getDurationForecast() = storagePrefs.getInt("duration", 3)
    fun getDefaultColor() = storagePrefs.getInt("color", 0)
    fun getLanguage() = storagePrefs.getString("language", "English").toString()

    private fun getAllFavorites() {
        storage = dao.getAll().map { it.toModel() }
        data.value = storage
    }

    fun addCityToFavorites(city: FavoriteCity) {
        for (item in storage) {
            if (item.city == city.city) return
        }
        dao.insert(city.toEntity())
        getAllFavorites()
    }

    fun deleteCityFromFavorites(city: FavoriteCity) {
        dao.delete(city.toEntity())
        getAllFavorites()
    }

    fun updateFavorite(city: FavoriteCity) {
        dao.update(city.id, city.image, city.temp)
        getAllFavorites()
    }

//    fun changeLanguage(language: String) {
//        saveLanguage(language)
//        LanguageManager.setAppLanguage(context, language)
//    }

    fun getFavoritesToViewModel(): StateFlow<List<FavoriteCity>> = data



}






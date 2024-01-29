package com.example.myweather.viewmodels

import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.data.*
import com.example.myweather.ui.theme.CustomBlue
import com.example.myweather.ui.theme.CustomGreen
import com.example.myweather.ui.theme.CustomGrey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepositoryImpl,
) : ViewModel() {
    private val _currentWeather = MutableStateFlow<WeatherCurrent?>(null)
    val currentWeather = _currentWeather.asStateFlow()
    private val _noCityError = MutableStateFlow(true)
    val noCityError = _noCityError.asStateFlow()
    private val _forecastWeather = MutableStateFlow<WeatherForecast?>(null)
    val forecastWeather = _forecastWeather.asStateFlow()
    private val _currentLocation = MutableStateFlow("")
    val currentLocation = _currentLocation.asStateFlow()
    private val _defaultCity = MutableStateFlow("")
    val defaultCity = _defaultCity.asStateFlow()
    private val _forecastDuration = MutableStateFlow(3)
    val forecastDuration = _forecastDuration.asStateFlow()
    private val _language = MutableStateFlow("English")
    val language = _language.asStateFlow()
    private val _backgroundColor = MutableStateFlow(CustomBlue)
    val backgroundColor = _backgroundColor.asStateFlow()
    private val _locationPermission = MutableStateFlow(false)
    val locationPermission = _locationPermission.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val favoriteList = repository.getFavoritesToViewModel()
    private val colors = listOf(CustomBlue, LightGray, CustomGrey, CustomGreen)

    init {
        if (currentLocation.value == "") {
            _defaultCity.value = repository.getDefaultCity()
            getWeather(defaultCity.value, false)
        } else {
            getWeather(currentLocation.value, false)
        }
        _forecastDuration.value = repository.getDurationForecast()
        _backgroundColor.value = colors[repository.getDefaultColor()]
        _language.value = repository.getLanguage()
    }

    fun getWeather(city: String, errorIsNeeded: Boolean) {
        _noCityError.value = false
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _currentWeather.value = repository.getWeather(city)
                getWeatherForecast(city)
            } catch (e: NoCityException) {
                if (errorIsNeeded) {
                    _noCityError.value = true
                }
            } catch (_: Exception) {
            }
        }
        _isLoading.value = false
    }

    private fun getWeatherForecast(city: String) {
        viewModelScope.launch {
            try {
                _forecastWeather.value = repository.getWeatherForecast(city, 3)
            } catch (_: Exception) {
            }
        }
    }

    fun saveLocation(location: String) {
        _currentLocation.value = location
    }

    fun saveDefaultCity(city: String) {
        viewModelScope.launch {
            if (repository.checkCity(city)) {
                changeDefaultCity(city)
                repository.saveDefaultCity(city)
            }
        }
    }

    fun saveDurationForecast(number: Int) {
        repository.saveDurationForecast(number)
    }

    fun saveLanguage(language: String) {
        _language.value = language
        repository.saveLanguage(language)
    }

    private fun saveDefaultColor(newValue: Int) {
        repository.saveDefaultColor(newValue)
    }

    fun changeForecastDuration(newValue: Int) {
        _forecastDuration.value = newValue
    }

    private fun changeDefaultCity(newValue: String) {
        _defaultCity.value = newValue
    }

    fun changeColor() {
        val currentIndex = colors.indexOf(_backgroundColor.value)
        val nextIndex = if (currentIndex == colors.size - 1) 0 else currentIndex + 1
        _backgroundColor.value = colors[nextIndex]
        saveDefaultColor(nextIndex)
    }

    fun addFavorite(value: String) {
        val data = currentWeather.value
        val temp = data?.current?.temp_c
        val image = data?.current?.condition?.icon
        val city = FavoriteCity(id = 0, city = value, temp = temp!!, image = image!!)
        repository.addCityToFavorites(city)
    }

    fun deleteFavorite(city: FavoriteCity) {
        repository.deleteCityFromFavorites(city)
    }

    fun updateFavorites() {
        favoriteList.value.forEach {
            viewModelScope.launch {
                val data = repository.getWeather(it.city)
                val newValue = FavoriteCity(
                    it.id,
                    it.city,
                    data.current?.temp_c!!,
                    data.current.condition.icon
                )
                repository.updateFavorite(newValue)
            }
        }
    }

    fun clearNoCityError() {
        _noCityError.value = false
    }

    fun changeLocationPermissionStatus(newValue: Boolean) {
        _locationPermission.value = newValue
    }


}

package com.example.myweather.screens

sealed class Screens(val route: String) {
    object Weather: Screens("current_weather")
    object Forecast: Screens("info")
    object Settings: Screens("settings")
    object Favorites: Screens("favorites")
}


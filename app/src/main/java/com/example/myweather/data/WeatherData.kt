package com.example.myweather.data

data class WeatherCurrent(
    val location: Location?,
    val current: Current?,
)

data class WeatherForecast(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Int,
    val localtime: String,
)

data class Condition(
    val text: String,
    val icon: String,
)

data class Current(
    val last_updated: String,
    val temp_c: Double,
    val is_day: Int,
    val condition: Condition,
    val cloud: Int,
    val feelslike_c: Double,
    val uv: Byte,
    val pressure_mb: Double,
    val humidity: Int,
    val wind_kph: Double
)

class Astro(
    val sunrise: String,
    val sunset: String,
)

data class ConditionXX(
    val code: Int,
    val icon: String,
    val text: String
)

data class Day(
    val avgtemp_f: Double,
    val condition: Condition,
    val daily_chance_of_rain: Int,
    val daily_chance_of_snow: Int,
    val daily_will_it_rain: Int,
    val daily_will_it_snow: Int,
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val totalsnow_cm: Double,
    val totalprecip_mm: Double,
    val avghumidity: Double
)

data class Forecast(
    val forecastday: List<Forecastday>
)

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>
)

data class Hour(
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    val cloud: Int,
    val condition: ConditionXX,
    val feelslike_c: Double,
    val is_day: Int,
    val temp_c: Double,
    val time: String,
    val time_epoch: Int,
    val will_it_rain: Int,
    val will_it_snow: Int
)




package com.example.myweather.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myweather.viewmodels.WeatherViewModel
import com.example.myweather.data.Forecastday
import com.example.myweather.R

@Composable
fun ForecastScreenView(viewModel: WeatherViewModel) {
    val forecast by viewModel.forecastWeather.collectAsState()
    val days by viewModel.forecastDuration.collectAsState()
    val currentColor = viewModel.backgroundColor.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(currentColor.value)
            .padding(bottom = 56.dp)
            .verticalScroll(rememberScrollState())
    ) {
        forecast?.forecast?.forecastday?.takeLast(days)?.forEach {
            CardForecastDetailed(forecast = it)
        }
    }
}

@Composable
fun CardForecastDetailed(forecast: Forecastday) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            .alpha(1f),
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    Text(
                        text = parseDate("yyyy-MM-dd", forecast.date, 10),
                        fontSize = 32.sp,
                        color = Color.DarkGray
                    )
                }
                Column() {
                    Row() {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sunrise_48),
                            contentDescription = "sunrise",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = parseTime(forecast.astro.sunrise))
                    }
                    Row() {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sunrise_48),
                            contentDescription = "sunset",
                            modifier = Modifier.size(24.dp)
                        )
                        Text(text = parseTime(forecast.astro.sunset))
                    }
                }

            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_temperature_24),
                        contentDescription = "temperature"
                    )
                    Text(text = stringResource(R.string.temperature))
                    Text(
                        text = "${forecast.day.mintemp_c.toInt()}°/${forecast.day.maxtemp_c.toInt()}°",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_water_drop_24),
                        contentDescription = "precipitation in mm"
                    )
                    Text(text = stringResource(R.string.precipitation))
                    Row() {
                        Text(
                            text = forecast.day.totalprecip_mm.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(text = "mm", modifier = Modifier.align(Alignment.CenterVertically))
                    }

                }
                if (forecast.day.totalsnow_cm.toString() > "0.0") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_snow_48),
                            contentDescription = "snow in cm"
                        )
                        Text(text = stringResource(R.string.snow))
                        Row() {
                            Text(
                                text = forecast.day.totalsnow_cm.toString(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(text = "cm", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_water_drop_24),
                        contentDescription = "avg humidity",
                        colorFilter = ColorFilter.tint(Color.DarkGray)
                    )
                    Text(text = stringResource(R.string.humidity))
                    Row() {
                        Text(
                            text = forecast.day.avghumidity.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(text = "%", modifier = Modifier.align(Alignment.Bottom))
                    }

                }
            }
            LazyRow(Modifier.padding(8.dp)) {
                items(forecast.hour) { hour ->
                    Column(
                        modifier = Modifier.padding(
                            8.dp
                        )
                    ) {
                        Text(text = hour.time.takeLast(5))
                        WeatherImage(url = "http:${hour.condition.icon}", 40.dp, 40.dp)
                        Text(
                            text = hour.temp_c.toInt().toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

            }
        }
    }

}


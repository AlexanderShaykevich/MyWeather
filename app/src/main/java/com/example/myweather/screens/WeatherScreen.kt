package com.example.myweather.screens

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.myweather.R
import com.example.myweather.data.*
import com.example.myweather.viewmodels.WeatherViewModel
import java.text.SimpleDateFormat

@Composable
fun WeatherScreenView(viewModel: WeatherViewModel) {
    val weather by viewModel.currentWeather.collectAsState()
    val forecast by viewModel.forecastWeather.collectAsState()
    val currentColor by viewModel.backgroundColor.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LanguageManager.setAppLanguage(context, currentLanguage)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        )
            LaunchedEffect(key1 = Unit) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
    }

//    val isLoading by viewModel.isLoading.collectAsState()
//    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
//
//    SwipeRefresh(
//        state = swipeRefreshState,
//        onRefresh = {
//            weather?.location?.name?.let {
//                viewModel.getWeather(it, false)
//            }
//        }) {
//
//    }

    if (weather?.location?.name != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(currentColor)
                .verticalScroll(scrollState)
                .padding(bottom = 58.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.size(62.dp))
            CardCity(viewModel = viewModel, weather = weather)
            CardCurrentWeather(weather = weather, forecast = forecast)
            CardHoursForecast(forecast = forecast, weather = weather)
            CardDetails(weather = weather)
            CardSun(forecast)
        }
    } else {
        NoDataView(color = currentColor)
    }

}

@Composable
fun CardCity(
    viewModel: WeatherViewModel,
    weather: WeatherCurrent?
) {
    val currentColor = viewModel.backgroundColor.collectAsState()
    val location by viewModel.currentLocation.collectAsState()
    val showDialogPermissions = remember { mutableStateOf(false) }

    if (showDialogPermissions.value) {
        DialogManager.LocationDialog(showDialogPermissions, currentColor.value)
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showDialogPermissions.value = true

            }
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 14.dp, end = 14.dp)
            .alpha(1f),
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White
    ) {
        Column(
            Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            weather?.location?.name?.let { Text(text = it, fontSize = 24.sp) }
            weather?.location?.country?.let { Text(text = it, fontSize = 18.sp) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_refresh_24),
                    contentDescription = "refresh",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            weather?.location?.name?.let {
                                viewModel.getWeather(it, false)
                            }

                        }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_gps_fixed_24),
                    contentDescription = "location",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                            viewModel.getWeather(location, false)
                        }
                )
            }

        }
    }
}

@Composable
fun CardCurrentWeather(weather: WeatherCurrent?, forecast: WeatherForecast?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 14.dp, end = 14.dp)
            .alpha(1f),
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${weather?.current?.temp_c?.toInt() ?: "?"}°",
                        fontSize = 78.sp
                    )
                }

                Column {
                    val tempMin =
                        forecast?.forecast?.forecastday?.get(0)?.day?.mintemp_c?.toInt().toString()
                    val tempMax =
                        forecast?.forecast?.forecastday?.get(0)?.day?.maxtemp_c?.toInt().toString()
                    weather?.current?.last_updated?.let {
                        Text(
                            text = parseDate("yyyy-MM-dd hh:mm", it, 16),
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    val feelsLike = stringResource(R.string.feels_like)
                    Text(
                        text = "$feelsLike ${weather?.current?.feelslike_c ?: " "}°",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    val min = stringResource(R.string.min)
                    val max = stringResource(R.string.max)
                    Text(
                        text = "$min $tempMin° / $max $tempMax°",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
                Column {
                    WeatherImage(url = "http:${weather?.current?.condition?.icon}", 80.dp, 80.dp)
                }
            }


        }
    }

}

@Composable
fun CardHoursForecast(
    forecast: WeatherForecast?,
    weather: WeatherCurrent?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 14.dp, end = 14.dp)
            .alpha(1f),
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White
    ) {
        val hours = getTwentyThreeHours(forecast, weather)

        LazyRow(Modifier.padding(8.dp)) {
            items(hours) { hour ->
                Column(
                    modifier = Modifier.padding(
                        8.dp
                    )
                ) {
                    Text(text = hour.time.takeLast(5))
                    WeatherImage(url = "http:${hour.condition.icon}", 40.dp, 40.dp)
                    Text(
                        text = hour.temp_c.toInt().toString(),
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

}

@Composable
fun CardDetails(weather: WeatherCurrent?) {
    val fontSize = 18.sp
    Row {
        Card(
            modifier = Modifier
                .padding(top = 14.dp, start = 14.dp, end = 14.dp),
            elevation = 4.dp,
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .padding(0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_cloud_queue_24),
                        contentDescription = "clouds",
                        modifier = Modifier.padding(end = 8.dp),
                        colorFilter = ColorFilter.tint(color = Color.Gray)
                    )
                    Text(text = stringResource(R.string.clouds), fontSize = fontSize)
                    Text(
                        text = "${weather?.current?.cloud ?: " "}" + "%",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_water_drop_24),
                        contentDescription = "drop",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(id = R.string.humidity), fontSize = fontSize)
                    Text(
                        text = "${weather?.current?.humidity ?: " "}" + "%",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val windKms = weather?.current?.wind_kph?.div(3.6)
                    Image(
                        painter = painterResource(id = R.drawable.ic_wind_24),
                        contentDescription = "wind",
                        modifier = Modifier.padding(end = 8.dp),
                        colorFilter = ColorFilter.tint(color = Color.DarkGray)
                    )
                    Text(text = stringResource(R.string.wind), fontSize = fontSize)
                    Text(
                        text = "${windKms?.toInt()}" + "m/s",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val pressureInMm = weather?.current?.pressure_mb?.toInt()?.times(0.750064)
                    Image(
                        painter = painterResource(id = R.drawable.ic_pressure_24),
                        contentDescription = "pressure",
                        modifier = Modifier.padding(end = 8.dp),
                        colorFilter = ColorFilter.tint(color = Color.Red)
                    )
                    Text(text = stringResource(R.string.pressure), fontSize = fontSize)
                    Text(
                        text = "${pressureInMm?.toInt()}" + "mm",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }


    }
}

@Composable
fun CardSun(forecast: WeatherForecast?) {
    val fontSize = 18.sp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 14.dp, end = 14.dp)
            .alpha(1f),
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(start = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sunrise_48),
                        contentDescription = "sunrise"
                    )
                    Text(text = stringResource(R.string.sunrise), fontSize = fontSize)
                    forecast?.forecast?.forecastday?.get(0)?.astro?.sunrise?.let {
                        Text(text = it.dropLast(3), fontSize = 18.sp)
                    }

                }
                Column(
                    modifier = Modifier.padding(end = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sunset_48),
                        contentDescription = "sunset"
                    )
                    Text(text = stringResource(R.string.sunset), fontSize = fontSize)
                    forecast?.forecast?.forecastday?.get(0)?.astro?.sunset?.let {
                        Text(text = parseTime(it), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun WeatherImage(url: String, height: Dp, width: Dp) {
    Box(
        modifier = Modifier
            .height(height)
            .width(width),
        contentAlignment = Alignment.Center
    ) {
        val painter = rememberImagePainter(
            data = url,
            builder = {
                error(R.drawable.ic_baseline_image_not_supported_24)
            })
        val painterState = painter.state
        Image(
            painter = painter, contentDescription = "Image",
            modifier = Modifier.fillMaxSize()
        )
        if (painterState is ImagePainter.State.Loading) {
            CircularProgressIndicator()
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun parseDate(pattern: String, inputDate: String, take: Int): String {
    val formatter = SimpleDateFormat(pattern)
    return formatter.parse(inputDate)?.toString()?.take(take) ?: ""
}

fun getTwentyThreeHours(forecast: WeatherForecast?, weather: WeatherCurrent?): List<Hour> {
    val dayList = forecast?.forecast?.forecastday
    val currentDayHours =
        dayList?.get(0)?.hour?.filter { it.time > weather?.current?.last_updated!! } ?: emptyList()
    val nextDayHours = dayList?.get(1)?.hour?.take(24 - currentDayHours.size) ?: emptyList()
    return currentDayHours + nextDayHours
}

@Composable
fun parseTime(inputTime: String): String {
    var hour = ""
    if (inputTime[6].toString() == "A" && inputTime[0].toString() == "1") {
        hour = "00"
    } else if (inputTime[6].toString() == "A") {
        hour = inputTime[0].toString() + inputTime[1].toString()
    } else if (inputTime[6].toString() == "P") {
        hour = when (inputTime.dropLast(6)) {
            "01" -> {
                "13"
            }

            "02" -> {
                "14"
            }

            "03" -> {
                "15"
            }

            "04" -> {
                "16"
            }

            "05" -> {
                "17"
            }

            "06" -> {
                "18"
            }

            "07" -> {
                "19"
            }

            "08" -> {
                "20"
            }

            "09" -> {
                "21"
            }

            "10" -> {
                "22"
            }

            "11" -> {
                "23"
            }

            "12" -> {
                "12"
            }

            else -> {
                ""
            }
        }
    }
    val minutes = inputTime[3].toString() + inputTime[4].toString()
    return "$hour:$minutes"
}

@Composable
fun NoDataView(color: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
    }
}



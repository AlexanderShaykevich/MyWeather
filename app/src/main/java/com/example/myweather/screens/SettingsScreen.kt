package com.example.myweather.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.myweather.R
import com.example.myweather.data.DialogManager
import com.example.myweather.data.LanguageManager
import com.example.myweather.viewmodels.WeatherViewModel

@Composable
fun SettingsScreen(viewModel: WeatherViewModel, navController: NavController) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val defaultCity by viewModel.defaultCity.collectAsState()
    val duration by viewModel.forecastDuration.collectAsState()
    val language by viewModel.language.collectAsState()
    val currentColor by viewModel.backgroundColor.collectAsState()
    val permission by viewModel.locationPermission.collectAsState()
    val scrollState = rememberScrollState()

    val currentLanguage by remember { mutableStateOf(language) }

    var isEnterCityFieldVisible by rememberSaveable { mutableStateOf(false) }
    val showDialogDays = rememberSaveable { mutableStateOf(false) }
    val showDialogCity = rememberSaveable { mutableStateOf(false) }
    val showPopupLanguageWindow = rememberSaveable { mutableStateOf(false) }

    LanguageManager.setAppLanguage(context, currentLanguage)

    fun changeLanguage(value: String) {
        LanguageManager.setAppLanguage(context, currentLanguage)
        viewModel.saveLanguage(value)
        showPopupLanguageWindow.value = false
        navController.navigate(Screens.Settings.route)
    }

    if (showDialogCity.value) {
        DialogManager.DefaultCityDialog(showDialogCity, currentColor, viewModel)
    }
    if(showDialogDays.value) {
        DialogManager.ForecastDurationDialog(showDialogDays, currentColor, duration, viewModel)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(currentColor)
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 58.dp)
            .verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
                .alpha(1f),
            elevation = 4.dp,
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White
        ) {

            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 24.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.forecast_settings),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.default_location),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = defaultCity,
                        fontSize = 20.sp,
                        maxLines = 2,
                        modifier = Modifier.clickable {
                            isEnterCityFieldVisible = !isEnterCityFieldVisible
                            showDialogCity.value = true
                        }, color = Color.Gray
                    )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.forecast_duration_in_days),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = duration.toString(),
                        fontSize = 20.sp,
                        modifier = Modifier.clickable {
                            showDialogDays.value = true
                        },
                        color = Color.Gray
                    )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.use_current_location),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = if (permission) stringResource(R.string.agree) else stringResource(R.string.disagree),
                        fontSize = 20.sp, modifier = Modifier.clickable {
                            activity.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                            )
                        }, color = Color.Gray
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 12.dp)
                .alpha(1f),
            elevation = 4.dp,
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 24.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.interface_settings),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.background_color),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp, 32.dp)
                            .background(currentColor)
                            .clickable {
                                viewModel.changeColor()
                            }
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.language),
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = language, fontSize = 20.sp, modifier = Modifier.clickable {
                        showPopupLanguageWindow.value = true
                    }, color = Color.Gray)
                }

            }
        }

    }


    if (showPopupLanguageWindow.value) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { showPopupLanguageWindow.value = false },
            properties = PopupProperties(focusable = true)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.White)
                    .border(1.dp, Color.Gray),
                horizontalAlignment = Alignment.Start
            ) {
                    Row(Modifier.padding(top = 24.dp, bottom = 16.dp, start = 12.dp)) {
                        Text(
                            text = stringResource(id = R.string.select_a_language),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val value = "English"
                        Column {
                            Text(
                                text = value,
                                fontSize = 20.sp,
                                modifier = Modifier.clickable {
                                    changeLanguage(value)
                                }
                            )
                        }
                        if(currentLanguage == value) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_check_24),
                                    contentDescription = "selectedLanguage",
                                    colorFilter = ColorFilter.tint(color = Color.Gray)
                                )
                            }
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val value = "Русский"
                        Column {
                            Text(
                                text = value,
                                fontSize = 20.sp,
                                modifier = Modifier.clickable {
                                    changeLanguage(value)
                                }
                            )
                        }
                        if(currentLanguage == value) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_check_24),
                                    contentDescription = "selectedLanguage",
                                    colorFilter = ColorFilter.tint(color = Color.Gray)
                                )
                            }
                        }
                    }

                }
            }
        }
    }












package com.example.myweather.data

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myweather.R
import com.example.myweather.viewmodels.WeatherViewModel

object DialogManager {
    @Composable
    fun LocationDialog(
        showDialogPermissions: MutableState<Boolean>,
        currentColor: Color
    ) {
        val context = LocalContext.current
        val activity = context as Activity

        if (showDialogPermissions.value) {
            AlertDialog(
                onDismissRequest = { showDialogPermissions.value = false },
                title = { Text(text = stringResource(R.string.no_access_to_your_location)) },
                text = { Text(stringResource(R.string.permit_access)) },
                shape = MaterialTheme.shapes.large,
                backgroundColor = currentColor,
                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { showDialogPermissions.value = false }
                        ) {
                            Text(
                                stringResource(R.string.close),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                        TextButton(
                            onClick = {
                                activity.startActivity(
                                    Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                )
                                showDialogPermissions.value = false
                            }
                        ) {
                            Text(
                                stringResource(R.string.settings),
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun DefaultCityDialog(
        showDialogCity: MutableState<Boolean>,
        currentColor: Color,
        viewModel: WeatherViewModel
    ) {
        var currentCityValue by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            backgroundColor = currentColor,
            onDismissRequest = {
                showDialogCity.value = false
                currentCityValue = ""
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.enter_the_city),
                        style = TextStyle(fontSize = 18.sp, color = Color.Black)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    OutlinedTextField(
                        value = currentCityValue,
                        singleLine = true,
                        onValueChange = { currentCityValue = it }
                    )
                }

            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = {
                            showDialogCity.value = false
                            currentCityValue = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel), color = Color.Black)
                    }
                    TextButton(
                        onClick = {
                            if (currentCityValue.isNotBlank()) {
                                viewModel.saveDefaultCity(currentCityValue)
                                currentCityValue = ""
                                showDialogCity.value = false
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save), color = Color.Black)
                    }
                }

            }
        )
    }

    @Composable
    fun ForecastDurationDialog(
        showDialogDays: MutableState<Boolean>,
        currentColor: Color,
        duration: Int,
        viewModel: WeatherViewModel
    ) {
        var currentDurationValue by remember { mutableStateOf(duration) }
        val daysList = listOf(1, 2, 3)

        AlertDialog(
            backgroundColor = currentColor,
            onDismissRequest = { showDialogDays.value = false },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.select_the_number_of_days),
                        style = TextStyle(fontSize = 18.sp, color = Color.Black)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            items(daysList) { item ->
                                Text(
                                    text = item.toString(),
                                    style =
                                    if (item == currentDurationValue) {
                                        TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                    } else
                                        TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal),
                                    modifier = Modifier.clickable {
                                        currentDurationValue = item
                                    }
                                )
                            }
                        }
                    }
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = {
                            showDialogDays.value = false
                        }
                    ) {
                        Text(stringResource(id = R.string.cancel), color = Color.Black)
                    }
                    TextButton(
                        onClick = {
                            viewModel.saveDurationForecast(currentDurationValue)
                            viewModel.changeForecastDuration(currentDurationValue)
                            showDialogDays.value = false
                        }
                    ) {
                        Text(stringResource(id = R.string.save), color = Color.Black)
                    }
                }
            }
        )
    }


}




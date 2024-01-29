package com.example.myweather.data

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myweather.R
import com.example.myweather.screens.Screens
import com.example.myweather.viewmodels.WeatherViewModel

object AppBars {
    @Composable
    fun BottomBar(navController: NavController) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color.Black
        ) {
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate(Screens.Weather.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_sun_24),
                        contentDescription = "weather",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate(Screens.Forecast.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_forecast_24),
                        contentDescription = "forecast",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate(Screens.Favorites.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_favorite_border_24),
                        contentDescription = "favorites",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                },
            )
            BottomNavigationItem(
                selected = false,
                onClick = { navController.navigate(Screens.Settings.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_settings_24),
                        contentDescription = "settings",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            )

        }
    }

    @Composable
    fun TopBar(viewModel: WeatherViewModel, context: Context) {
        val error by viewModel.noCityError.collectAsState()
        var input by rememberSaveable { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        TopAppBar(backgroundColor = Color.White) {
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(8.dp)
            )
            IconButton(onClick = {
                if (input.isNotBlank()) {
                    focusManager.clearFocus()
                    viewModel.getWeather(input, true)
                    input = ""
                }
            }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            if (error) {
                Toast.makeText(context, "Sorry, city was not found!", Toast.LENGTH_SHORT).show()
                viewModel.clearNoCityError()
            }

        }
    }
}
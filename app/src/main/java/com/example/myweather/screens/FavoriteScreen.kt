package com.example.myweather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myweather.R
import com.example.myweather.data.FavoriteCity
import com.example.myweather.viewmodels.WeatherViewModel

@Composable
fun FavoriteScreenView(viewModel: WeatherViewModel, navController: NavController) {
    val currentColor by viewModel.backgroundColor.collectAsState()
    val currentWeather by viewModel.forecastWeather.collectAsState()
    val city = currentWeather?.location?.name
    val favoritesList by viewModel.favoriteList.collectAsState()

    if (favoritesList.isNotEmpty()) {
        viewModel.updateFavorites()
    }

    fun isAddFavoriteButtonVisible(): Boolean {
        if (city == null) return false
        var response = true
        favoritesList.forEach {
            if (it.city == city) response = false
        }
        return response
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(currentColor)
            .padding(top = 64.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (isAddFavoriteButtonVisible()) {
                OutlinedButton(shape = RoundedCornerShape(12.dp), onClick = {
                    if (city != null) {
                        viewModel.addFavorite(city)
                    }
                }) {
                    val text = stringResource(R.string.add)
                    Text(text = "$text $city", color = Color.Black)
                }
            }
        }

        LazyColumn(Modifier.padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 48.dp)) {
            items(favoritesList) { favoriteItem ->
                FavoriteItemView(
                    viewModel = viewModel,
                    favoriteItem = favoriteItem,
                    navController = navController
                )
            }
        }


    }


}

@Composable
fun FavoriteItemView(
    viewModel: WeatherViewModel,
    favoriteItem: FavoriteCity,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .alpha(1f)
                .clickable {
                    viewModel.getWeather(favoriteItem.city, false)
                    navController.navigate(Screens.Weather.route)
                },
            elevation = 4.dp,
            shape = RoundedCornerShape(24.dp),
            backgroundColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = favoriteItem.city, modifier = Modifier.padding(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    WeatherImage(url = "http:${favoriteItem.image}", 60.dp, 60.dp)
                    Text(text = favoriteItem.temp.toInt().toString())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = "delete favorite",
                        modifier = Modifier
                            .clickable {
                                viewModel.deleteFavorite(favoriteItem)
                            }
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}



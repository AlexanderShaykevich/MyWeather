package com.example.myweather.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myweather.data.AppBars.BottomBar
import com.example.myweather.data.AppBars.TopBar
import com.example.myweather.screens.FavoriteScreenView
import com.example.myweather.screens.ForecastScreenView
import com.example.myweather.screens.Screens
import com.example.myweather.screens.SettingsScreen
import com.example.myweather.screens.WeatherScreenView
import com.example.myweather.ui.theme.MyWeatherTheme
import com.example.myweather.viewmodels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {
    private val viewModel by viewModels<WeatherViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter",
        "UnusedMaterial3ScaffoldPaddingParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val weatherViewModel = hiltViewModel<WeatherViewModel>()
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            MyWeatherTheme {
                Scaffold(
                    bottomBar = { BottomBar(navController) },
                    topBar = {
                        if (currentRoute == Screens.Weather.route ||
                            currentRoute == Screens.Favorites.route
                        )
                            TopBar(weatherViewModel, this)
                    },
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screens.Weather.route
                    ) {
                        composable(Screens.Weather.route) {
                            WeatherScreenView(weatherViewModel)
                        }
                        composable(Screens.Forecast.route) {
                            ForecastScreenView(weatherViewModel)
                        }
                        composable(Screens.Settings.route) {
                            SettingsScreen(weatherViewModel, navController)
                        }
                        composable(Screens.Favorites.route) {
                            FavoriteScreenView(weatherViewModel, navController)
                        }
                    }
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        getLocation()
        viewModel.changeLocationPermissionStatus(
            EasyPermissions.hasPermissions(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        if (!isLocationEnabled()) {
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val cancellationToken = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnCompleteListener {
                viewModel.saveLocation("${it.result.latitude}, ${it.result.longitude}")
                viewModel.getWeather("${it.result.latitude}, ${it.result.longitude}", false)
            }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
    }

    private fun hasPermissions() =
        EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)

}


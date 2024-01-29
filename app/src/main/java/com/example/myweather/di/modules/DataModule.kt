package com.example.myweather.di.modules

import android.content.Context
import androidx.room.Room
import com.example.myweather.api.BASE_URL_WEATHER
import com.example.myweather.api.WeatherApi
import com.example.myweather.data.WeatherRepositoryImpl
import com.example.myweather.database.AppDb
import com.example.myweather.database.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        dao: WeatherDao,
        @ApplicationContext
        context: Context
    ): WeatherRepositoryImpl {
        return WeatherRepositoryImpl(api, dao, context)
    }

}

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_WEATHER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideWeatherDao(appDatabase: AppDb): WeatherDao {
        return appDatabase.weatherDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context):
            AppDb {
        return Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .allowMainThreadQueries()
            .build()
    }

}




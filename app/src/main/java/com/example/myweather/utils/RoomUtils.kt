package com.example.myweather.utils

import android.os.AsyncTask
import com.example.myweather.bean.Weather
import com.example.myweather.room.IDispose
import com.example.myweather.room.WeatherDao

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
class RoomUtils {
    companion object {
        fun insert(weatherDao: WeatherDao, weather: Weather) {
            InsertWeatherTask(weatherDao).execute(weather)
        }

        fun delete(weatherDao: WeatherDao, city: String) {
            DeleteWeatherTask(weatherDao, city).execute()
        }

        fun query(dispose: IDispose, weatherDao: WeatherDao, city: String) {
            GetWeatherTask(weatherDao, city, dispose).execute()
        }

        fun queryAll(dispose: IDispose, weatherDao: WeatherDao) {
            GetAllWeathersTask(weatherDao, dispose).execute()
        }

        fun update(weatherDao: WeatherDao, weather: Weather, city: String) {
            UpdateWeatherTask(weatherDao, city, weather).execute()
        }

        class GetAllWeathersTask(
            _weatherDao: WeatherDao,
            _dispose: IDispose
        ) :
            AsyncTask<Void, Void, Void>() {
            private val weatherDao = _weatherDao
            private val dispose = _dispose
            private lateinit var weathers: List<Weather>
            override fun doInBackground(vararg p0: Void?): Void? {
                weathers = weatherDao.getAllWeathers()
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                dispose.runOnUi(weathers)
            }


        }

        class GetWeatherTask(_weatherDao: WeatherDao, _city: String, _dispose: IDispose) :
            AsyncTask<Void, Void, Void>() {
            private val weatherDao = _weatherDao
            private val dispose = _dispose
            private lateinit var weather: Weather
            private val city = _city

            override fun doInBackground(vararg p0: Void?): Void? {
                weather = weatherDao.getWeatherByCity(city)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                dispose.runOnUi(weather)
            }


        }

        class InsertWeatherTask(_weatherDao: WeatherDao) :
            AsyncTask<Weather, Void, Void>() {
            private val weatherDao = _weatherDao

            override fun doInBackground(vararg p0: Weather): Void? {
                val weathers: Array<out Weather> = p0
                weatherDao.insertWeather(weathers[0])
                return null
            }

        }

        class DeleteWeatherTask(_weatherDao: WeatherDao, _city: String) :
            AsyncTask<Void, Void, Void>() {
            private val weatherDao = _weatherDao
            private val city = _city

            override fun doInBackground(vararg p0: Void?): Void? {
                weatherDao.deleteByCity(city)
                return null
            }

        }

        class UpdateWeatherTask(_weatherDao: WeatherDao, _city: String, _weather: Weather) :
            AsyncTask<Void, Void, Void>() {
            private val weatherDao = _weatherDao
            private val city = _city
            private val weather = _weather

            override fun doInBackground(vararg p0: Void?): Void? {
                weatherDao.update(city, weather.data)
                return null
            }

        }

    }
}
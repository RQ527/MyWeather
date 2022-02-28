package com.example.myweather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import com.example.myweather.bean.Weather

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
@Dao
interface WeatherDao {
    @Insert
    fun insertWeather(vararg weathers: Weather)

    @Query("SELECT * FROM weather WHERE city = :city")
    fun getWeatherByCity(city: String):Weather

    @Query("SELECT * FROM weather")
    fun getAllWeathers(): List<Weather>

    @Query("DELETE FROM weather WHERE city = :city")
    fun deleteByCity(city: String)
    @Query("UPDATE weather SET City =:city,data =:data WHERE city =:city")
    @TypeConverters(MyConverters::class)
    fun update(city: String,data:Weather.Data)
}
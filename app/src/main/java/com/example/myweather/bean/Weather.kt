package com.example.myweather.bean

import androidx.room.*
import com.example.myweather.room.MyConverters
import java.io.Serializable

/**
 * ...
 * 数据库与Gson对应的天气类
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/16
 */
@Entity(tableName = "weather")
@TypeConverters(MyConverters::class)
data class Weather(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int,
    @ColumnInfo(name = "City")
    var city:String,
    @ColumnInfo(name = "code")
    val code: Int,
    @ColumnInfo(name = "data")
    val `data`: Data,
    @ColumnInfo(name = "log_id")
    val log_id: Long,
    @ColumnInfo(name = "msg")
    val msg: String,
    @ColumnInfo(name = "time")
    val time: Int
):Serializable {

    data class Data(
        val air: String,
        val air_pm25: String,
        val alarm: List<Any>,
        val aqi: Aqi,
        val city: String,
        val city_en: String,
        val city_id: String,
        val hour: List<Hour>,
        val humidity: String,
        val index: Index,
        val max_temp: String,
        val min_temp: String,
        val pressure: String,
        val province: String,
        val province_en: String,
        val rain: String,
        val rain_24h: String,
        val sunrise: String,
        val sunset: String,
        val tail_number: String,
        val temp: String,
        val update_time: String,
        val visibility: String,
        val weather: String,
        val weather_code: String,
        val wind: String,
        val wind_scale: String,
        val wind_speed: String
    ):Serializable {


        data class Aqi(
            val air: String,
            val air_level: String,
            val air_tips: String,
            val co: String,
            val no2: String,
            val o3: String,
            val pm10: String,
            val pm25: String,
            val so2: String
        ):Serializable

        data class Hour(
            val temp: String,
            val time: String,
            val wea: String,
            val wea_code: String,
            val wind: String,
            val wind_level: String
        ):Serializable

        data class Index(
            val chuangyi: Chuangyi,
            val ganmao: Ganmao,
            val huazhuang: Huazhuang,
            val xiche: Xiche,
            val yundong: Yundong,
            val ziwaixian: Ziwaixian
        ):Serializable {
            data class Chuangyi(
                val content: String,
                val level: String,
                val name: String
            ):Serializable

            data class Ganmao(
                val content: String,
                val level: String,
                val name: String
            ):Serializable

            data class Huazhuang(
                val content: String,
                val level: String,
                val name: String
            ):Serializable

            data class Xiche(
                val content: String,
                val level: String,
                val name: String
            ):Serializable

            data class Yundong(
                val content: String,
                val level: String,
                val name: String
            ):Serializable

            data class Ziwaixian(
                val content: String,
                val level: String,
                val name: String
            ):Serializable
        }
    }

}
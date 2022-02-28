package com.example.myweather.utils

import com.example.myweather.R

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
class SelectUtils {
    companion object {
        fun selectWeatherBackground(weather:String,time:String) :Int=
            when (weather) {
                "晴" -> if (time == "白天") R.drawable.sunny_bg1 else R.drawable.sunny_bg2
                "阴" ->  R.drawable.overcast_bg
                "多云" -> if (time == "白天") R.drawable.cloudy_bg else R.drawable.cloudy_bg2
                "大雨", "中雨", "小雨", "暴雨" -> R.drawable.rainy_bg
                "霾","雾霾" ->if (time=="白天") R.drawable.haze_bg else R.drawable.night
                "雾" ->if (time=="白天") R.drawable.fog_bg else R.drawable.night
                "小雪", "中雪", "大雪", "暴雪","雨夹雪" -> R.drawable.snowy_bg
                else -> R.drawable.overcast_bg
            }
        fun selectWeatherPicture(weather:String,time:String):Int=
            when(weather){
                "晴" -> if (time == "白天") R.drawable.sunny1 else R.drawable.sunny2
                "阴" -> if (time == "白天") R.drawable.overcast else R.drawable.overcast2
                "多云" -> if (time == "白天") R.drawable.cloudy1 else R.drawable.cloudy2
                "大雨", "中雨", "小雨", "暴雨" -> R.drawable.rainy
                "霾" -> R.drawable.haze
                "雾" -> R.drawable.fog
                "雨夹雪" -> R.drawable.sleet
                "小雪", "中雪", "大雪", "暴雪" -> R.drawable.snowy
                else -> R.drawable.test
            }
    }
}
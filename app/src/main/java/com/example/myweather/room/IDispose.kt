package com.example.myweather.room

import com.example.myweather.bean.Weather

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
interface IDispose {
    fun runOnUi(weather: Weather)
    fun runOnUi(weathers:List<Weather>)
}
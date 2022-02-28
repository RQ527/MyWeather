package com.example.myweather.utils

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */
class TimeUtils {
    companion object {
        fun timeToMinutes(time: String):Int {
            val hour = time.substring(0, 2)
            val minutes = time.substring(3, 5)
            return hour.toInt() * 60 + minutes.toInt()
        }
    }
}
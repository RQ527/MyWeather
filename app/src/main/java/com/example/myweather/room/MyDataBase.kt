package com.example.myweather.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myweather.bean.Weather

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
@Database(entities = [Weather::class], version = 1, exportSchema = false)
abstract class MyDataBase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME: String = "my_db.db"
        private var mInstance: MyDataBase?=null
        @Synchronized
        fun getInstance(context: Context): MyDataBase{
            if (mInstance===null) mInstance = Room.databaseBuilder(
                context.applicationContext,
                MyDataBase::class.java, DATABASE_NAME
            ).build()
            return mInstance as MyDataBase
        }

    }
    abstract fun getWeatherDao():WeatherDao
}
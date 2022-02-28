package com.example.myweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myweather.base.BaseActivity
import com.example.myweather.bean.Weather
import com.example.myweather.room.IDispose
import com.example.myweather.room.MyDataBase
import com.example.myweather.room.WeatherDao
import com.example.myweather.utils.NetUtils
import com.example.myweather.utils.RoomUtils
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class LoadingActivity : BaseActivity() {

    private val TAG = "RQ"
    private var myDataBase: MyDataBase? = null

    private var weatherDao: WeatherDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        myDataBase = MyDataBase.getInstance(this)
        weatherDao = myDataBase!!.getWeatherDao()
        Thread {
            update()
            RoomUtils.queryAll(object : IDispose {
               override fun runOnUi(weather: Weather) {}
                override fun runOnUi(weathers: List<Weather>) {
                    if (weathers.isEmpty()) {
                        val intent =
                            Intent(this@LoadingActivity, AddWeatherActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@LoadingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }, weatherDao!!)
        }.start()
    }

    /**
     * 更新数据
     */
    private fun update() {
        weatherDao?.let {
            RoomUtils.queryAll(object : IDispose {
                override fun runOnUi(weather: Weather) {}

                @Throws(Exception::class)
                override fun runOnUi(weathers: List<Weather>) {
    //                遍历数据库的weathers进行更新
                    for (weather in weathers) {
                        NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi",
                            "POST",
                            "city",
                            weather.city,
                            object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    e.printStackTrace()
                                }

                                @Throws(IOException::class)
                                override fun onResponse(call: Call, response: Response) {
                                    if (response.isSuccessful) {
                                        val json = response.body!!.string()
                                        val gson = Gson()
                                        val weather2: Weather = gson.fromJson(json, Weather::class.java)
                                        weather2.city=weather.city
                                        weatherDao?.let { RoomUtils.update(it, weather2, weather2.city) }
                                    }
                                }
                            })
                        //防止一次访问接口的次数过多
                        Thread.sleep(500)
                    }
                }
            }, it)
        }
    }

}
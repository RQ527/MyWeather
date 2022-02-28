package com.example.myweather

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class AddWeatherActivity : BaseActivity(),View.OnClickListener {
    var mEditText: EditText? = null
    private var addButton: Button? = null
    private var backButton: Button? = null
    private var mHandler: MyHandler? = null
    private var weatherDao: WeatherDao? = null
    private var myDataBase: MyDataBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weather)
        initView()
    }

    private fun initView() {
        myDataBase = MyDataBase.getInstance(this)
        weatherDao = myDataBase?.getWeatherDao()
        mHandler = MyHandler()
        mEditText = findViewById(R.id.editText2)
        addButton = findViewById(R.id.bt_addWeather_add)
        backButton = findViewById(R.id.bt_addWeather_back)
        backButton?.setOnClickListener(this)
        addButton?.setOnClickListener(this)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_addWeather_add -> {
                //防止连击多次添加数据
                addButton!!.isClickable = false
                try {
                    weatherDao?.let {
                        RoomUtils.queryAll(object : IDispose {
                            override fun runOnUi(weather: Weather) {
                                TODO("Not yet implemented")
                            }

                            @Throws(Exception::class)
                            override fun runOnUi(weathers: List<Weather>) {
                                //是否访问获取数据
                                var isRequest = true
                                if (weathers.size == 8) {
                                    isRequest = false
                                    Toast.makeText(
                                        this@AddWeatherActivity,
                                        "城市数量已达上限，请删除一些城市再添加。",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    addButton!!.isClickable = true
                                } else {
                                    //判断是否已存在城市
                                    for (weather in weathers) {
                                        if (weather.city == mEditText!!.text.toString()) {
                                            Toast.makeText(
                                                this@AddWeatherActivity,
                                                "城市已存在",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            isRequest = false
                                            addButton!!.isClickable = true
                                            break
                                        }
                                    }
                                }
                                if (isRequest) {
                                    request()
                                }
                            }
                        }, it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.bt_addWeather_back -> finish()
        }
    }

    /**
     * 访问数据
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun request() {
        NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi",
            "POST",
            "city",
            mEditText!!.text.toString(),
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val message = Message()
                        message.obj = response.body!!.string()
                        mHandler!!.sendMessage(message)
                    }
                }
            })
    }

    /**
     * 自定义Handler处理网络请求结果
     */
    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val responseData = msg.obj.toString()
            val gson = Gson()
            val weather: Weather = gson.fromJson(responseData, Weather::class.java) //剥壳
            if (weather.data.city != mEditText?.text.toString()) {
                Toast.makeText(this@AddWeatherActivity, "输入格式错误，请按要求输入。", Toast.LENGTH_SHORT).show()
                addButton?.setClickable(true)
            } else {
                weather.city=mEditText?.text.toString()
                if (weather != null) {
                    weatherDao?.let { RoomUtils.insert(it, weather) }
                    //判断是从哪个activity跳转过来
                    val flag: String? = intent.getStringExtra("flag")
                    if (flag != null) {
                        val intent: Intent
                        if (flag == "manage") {
                            val intent1 = Intent()
                            intent1.putExtra("weather", weather)
                            setResult(0, intent1)
                            Toast.makeText(this@AddWeatherActivity, "添加成功", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        } else {
                            intent = Intent(this@AddWeatherActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    } else {
                        val intent = Intent(this@AddWeatherActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish()
                }
            }
        }
    }
}
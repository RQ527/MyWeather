package com.example.myweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.myweather.adapter.FragmentPagerAdapter
import com.example.myweather.base.BaseActivity
import com.example.myweather.bean.Weather
import com.example.myweather.room.IDispose
import com.example.myweather.room.MyDataBase
import com.example.myweather.room.WeatherDao
import com.example.myweather.utils.NetUtils
import com.example.myweather.utils.RoomUtils
import com.example.myweather.view.HomeFragment
import com.example.myweather.view.MyMenu
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class MainActivity : BaseActivity(),View.OnClickListener {

    private val TAG = "RQ"
    private var mViewPager: ViewPager2? = null
    private var fragments //viewpager的数据源
            : MutableList<HomeFragment>? = null
    private var myDataBase: MyDataBase? = null
    private var weatherDao: WeatherDao? = null
    private var addCityButton: Button? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private var adapter: FragmentStateAdapter? = null
    private var pointLinearLayout //tabLayout的指示点
            : LinearLayout? = null
    private var menu: MyMenu? = null

    @SuppressLint("StaticFieldLeak")
    companion object{
        @SuppressLint("StaticFieldLeak")
        var context //全局获取上下文
            : Context? = null}
    private var prePosition = 0 //上一页的位置

    var mState = -1 //viewpager的状态值

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //隐藏actionBar
        val actionBar = supportActionBar
        actionBar?.hide()
        initView()
    }

    //供fragment获取viewpager的状态
    fun getState(): Int {
        return mState
    }

    private fun initView() {
        pointLinearLayout = findViewById(R.id.ll_home_point)
        context = applicationContext
        myDataBase = MyDataBase.getInstance(this)
        weatherDao = myDataBase!!.getWeatherDao()
        mViewPager = findViewById(R.id.vp_home_fragment)
        mViewPager!!.offscreenPageLimit = 10
        refreshLayout = findViewById(R.id.srl_main_refresh)
        refreshLayout!!.setColorSchemeResources(R.color.blue)
        refreshLayout!!.setOnRefreshListener { refresh() }
        addCityButton = findViewById(R.id.bt_toolbar_city)
        addCityButton!!.setOnClickListener(this)
        menu = findViewById(R.id.mm_toolbar_menu)
        menu!!.setListen ( object :MyMenu.MyMenuListener{
            override fun itemListen(position: Int) {
                if (position === 1) {
                    val intent = Intent(this@MainActivity, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            })
        fragments = ArrayList<HomeFragment>()
        //为fragment加载数据
        RoomUtils.queryAll(object : IDispose {
           override fun runOnUi(weather: Weather) {}

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("RestrictedApi")
           override fun runOnUi(weathers: List<Weather>) {
                for (i in weathers.indices) {
                    val weather: Weather? = weathers[i]
                    val fragment = HomeFragment()
                    fragment.setWeather(weather)
                    fragments!!.add(fragment)
                    //添加指示点
                    val point = ImageView(this@MainActivity)
                    point.setBackgroundResource(R.drawable.point_selector)
                    //设置每个指示点的大小
                    val params = LinearLayout.LayoutParams(10, 10)
                    //设置第一个高亮
                    if (i == 0) {
                        point.isEnabled = true
                    } else {
                        point.isEnabled = false
                        //设置point的间距
                        params.leftMargin = 10
                    }
                    point.layoutParams = params
                    pointLinearLayout!!.addView(point)
                }
                adapter = FragmentPagerAdapter(this@MainActivity,
                    fragments as ArrayList<HomeFragment>
                )
                mViewPager!!.adapter = adapter
                //viewpager设置监听
                mViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        //设置当前页面的tabLayout高亮
                        pointLinearLayout!!.getChildAt(prePosition).isEnabled = false
                        pointLinearLayout!!.getChildAt(position).isEnabled = true
                        prePosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        mState = state
                        //处理refreshLayout与viewpager的滑动冲突
                        if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                            runOnUiThread { refreshLayout!!.isEnabled = false }
                        }
                        if (state == ViewPager2.SCROLL_STATE_SETTLING || state == ViewPager2.SCROLL_STATE_IDLE) {
                            runOnUiThread { refreshLayout!!.isEnabled = true }
                        }
                    }
                })
            }
        }, weatherDao!!)
    }

    /**
     * 刷新
     */
    private fun refresh() {
        Thread {
            for (i in fragments!!.indices) {
                try {
                    upDateWeather(fragments!![i].getWeather()!!.city, i)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            refreshLayout!!.isRefreshing = false
            runOnUiThread { Toast.makeText(this@MainActivity, "刷新成功", Toast.LENGTH_SHORT).show() }
        }.start()
    }

    /**
     * 更新weather数据
     * @param city 城市
     * @param position 位置
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun upDateWeather(city: String, position: Int) {
        NetUtils.sendRequest(
            "https://v2.alapi.cn/api/tianqi",
            "POST",
            "city",
            city,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val weather2: Weather =
                            gson.fromJson(response.body!!.string(), Weather::class.java)
                        weather2.city=fragments!![position].getWeather()!!.city
                        //在主线程更新UI数据
                        runOnUiThread {
                            Log.d(TAG, "run: " + weather2.city)
                            fragments!![position].setData(weather2)
                        }
                    }
                }
            })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_toolbar_city -> {
                val intent = Intent(this@MainActivity, ManageActivity::class.java)
                startActivityForResult(intent, 0)
            }
        }
    }

    /**
     * 当切换至当前activity时，判断是否有其他activity传递数据过来
     */
    override fun onResume() {
        super.onResume()
        //获取intent的数据
        val position = intent.getStringExtra("position")
        //用过得数据当然要扔掉
        intent.removeExtra("position")
        if (position != null) {
            val index = Integer.valueOf(position)
            //viewpager的跳转
            mViewPager!!.postDelayed({ mViewPager!!.currentItem = index }, 10)
        }
    }

    /**
     * 这是当其他activity销毁时启动的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            0 -> if (data != null) {
                //获取点击item和删除item的数据
                val position = data.getStringExtra("position")
                val position2 = data.getStringExtra("position2")
                if (position != null || position2 != null) {
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    //如果是点击则跳转
                    if (position != null) {
                        intent.putExtra("position", position)
                    }
                    //如果删除，则刷新
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
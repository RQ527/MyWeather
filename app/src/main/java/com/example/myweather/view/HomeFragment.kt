package com.example.myweather.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.myweather.DetailActivity
import com.example.myweather.MainActivity
import com.example.myweather.R
import com.example.myweather.bean.Weather
import com.example.myweather.utils.SelectUtils
import com.example.myweather.utils.TimeUtils

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */
class HomeFragment:Fragment(), View.OnClickListener {

    private val TAG = "RQ"

    /**
     * 都是fragment里面的控件，看名字应该就知道啦，我就不一一注释了。
     */
    private var weather: Weather? = null
    private var mMyDiagram: MyDiagram? = null
    private var mScrollView: MyScrollView? = null
    private var adviceCardView: CardView? = null
    private var mConstraintLayout: ConstraintLayout? = null
    private var topConstraintLayout: ConstraintLayout? = null
    private var backgroundImageView: ImageView? = null
    private var visibilityLinearLayout: LinearLayout? = null
    private var humidityLinearLayout: LinearLayout? = null
    private var pressureLinearLayout: LinearLayout? = null
    private var windLinearLayout: LinearLayout? = null
    private var pressureTextView: TextView? = null
    private var humidityTextView: TextView? = null
    private var windDirectionTextView: TextView? = null
    private var windLevelTextView: TextView? = null
    private var visibilityTextView2: TextView? = null
    private var wearTextView: TextView? = null
    private var lipstickTextView: TextView? = null
    private var motionTextView: TextView? = null
    private var cleanCarTextView: TextView? = null
    private var coldTextView: TextView? = null
    private var raysTextView: TextView? = null
    private var pmTextView: TextView? = null
    private var airTextView: TextView? = null
    private var tempTextView: TextView? = null
    private var visibilityTextView: TextView? = null
    private var weatherTextView: TextView? = null
    private var minToMaxTextView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        initView(view)
        setData(weather)
        return view
    }

    /**
     * 设置weather
     * @param weather
     */
    fun setWeather(weather: Weather?) {
        this.weather = weather
    }

    /**
     * 供外界获取当前weather
     * @return
     */
    fun getWeather(): Weather? {
        return weather
    }

    /**
     * 设置ui的数据
     * @param weather1
     */
    @SuppressLint("SetTextI18n")
    fun setData(weather1: Weather?) {
        if (weather1 != null) {
            //设置自定义view的数据
            mMyDiagram!!.setWeather(weather1)
            mMyDiagram!!.initWeather()
            mMyDiagram!!.initPath()
            mMyDiagram!!.invalidate()

            //获取时间
            val time: String = weather1.data.update_time
            val minuteTime: Int = TimeUtils.timeToMinutes(time.substring(11, 16))
            val sunrise: Int = TimeUtils.timeToMinutes(weather1.data.sunrise)
            val sunset: Int = TimeUtils.timeToMinutes(weather1.data.sunset)
            var textColor = Color.WHITE //文本颜色
            var `when` = "晚上"
            //判断白天还是晚上用于设置不同的视图
            if (minuteTime in sunrise..sunset) {
                `when` = "白天"
                textColor = Color.BLACK
            }
            //挑选背景图片
            val backgroundPicture: Int =
                SelectUtils.selectWeatherBackground(weather1.data.weather, `when`)
            backgroundImageView!!.setImageResource(backgroundPicture)
            //防止某些小地区没有数据
            pmTextView?.text = weather1.data.air_pm25
            airTextView?.text = weather1.data.air
            //给UI设置数据的操作
            tempTextView?.text = weather1.data.temp
            visibilityTextView?.text = weather1.data.visibility
            weatherTextView?.text = weather1.data.weather
            minToMaxTextView?.text = weather1.data.min_temp + "~" + weather1.data
                .max_temp + "℃"
            windDirectionTextView?.text = weather1.data.wind
            windLevelTextView?.text = weather1.data.wind_speed
            visibilityTextView2?.text = weather1.data.visibility
            pressureTextView?.text = weather1.data.pressure + "hPa"
            humidityTextView?.text = weather1.data.humidity
            //防止某些小地区没有数据
            wearTextView?.text = weather1.data.index.chuangyi.level
            lipstickTextView?.text = weather1.data.index.huazhuang.level
            motionTextView?.text = weather1.data.index.yundong.level
            cleanCarTextView?.text = weather1.data.index.xiche.level
            coldTextView?.text = weather1.data.index.ganmao.level
            raysTextView?.text = weather1.data.index.ziwaixian.level

            windLevelTextView!!.setTextColor(textColor)
            visibilityTextView2!!.setTextColor(textColor)
            pressureTextView!!.setTextColor(textColor)
            humidityTextView!!.setTextColor(textColor)
            wearTextView!!.setTextColor(textColor)
            lipstickTextView!!.setTextColor(textColor)
            motionTextView!!.setTextColor(textColor)
            cleanCarTextView!!.setTextColor(textColor)
            coldTextView!!.setTextColor(textColor)
            raysTextView!!.setTextColor(textColor)
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun initView(view: View) {
        //初始化很多很多控件
        mConstraintLayout = view.findViewById(R.id.cl_fragment_windmill)
        topConstraintLayout = view.findViewById(R.id.cl_fragment_top)
        adviceCardView = view.findViewById(R.id.cd_fragment_advice)
        mConstraintLayout?.setOnClickListener(this)
        topConstraintLayout?.setOnClickListener(this)
        adviceCardView?.setOnClickListener(this)
        visibilityLinearLayout = view.findViewById(R.id.ll_fragment_visibility)
        humidityLinearLayout = view.findViewById(R.id.ll_fragment_humidity)
        pressureLinearLayout = view.findViewById(R.id.ll_fragment_pressure)
        windLinearLayout = view.findViewById(R.id.ll_fragment_wind)
        windDirectionTextView = view.findViewById(R.id.tv_fragment_windDirection)
        windLevelTextView = view.findViewById(R.id.tv_fragment_windLevel)
        visibilityTextView2 = view.findViewById(R.id.tv_fragment_visibility)
        humidityTextView = view.findViewById(R.id.tv_fragment_humidity)
        pressureTextView = view.findViewById(R.id.tv_fragment_pressure)
        wearTextView = view.findViewById(R.id.tv_cardView_wear)
        lipstickTextView = view.findViewById(R.id.tv_cardView_lipstick)
        motionTextView = view.findViewById(R.id.tv_cardView_motion)
        cleanCarTextView = view.findViewById(R.id.tv_cardView_cleanCar)
        coldTextView = view.findViewById(R.id.tv_cardView_cold)
        raysTextView = view.findViewById(R.id.tv_cardView_rays)
        mMyDiagram = view.findViewById(R.id.md_home_show)
        pmTextView = view.findViewById(R.id.tv_home_pm2_5)
        airTextView = view.findViewById(R.id.tv_home_air)
        tempTextView = view.findViewById(R.id.tv_home_temp)
        visibilityTextView = view.findViewById(R.id.tv_home_visibility)
        weatherTextView = view.findViewById(R.id.tv_home_weather)
        minToMaxTextView = view.findViewById(R.id.tv_home_minToMax)
        backgroundImageView = view.findViewById(R.id.ll_fragment_weatherBackground)
        mScrollView = view.findViewById(R.id.sc_fragment)
        //scrollView设置监听
        mScrollView?.setScrollListener(object :MyScrollView.ScrollListener{
            override fun scrollOritention(l: Int, t: Int, oldl: Int, oldt: Int) {
                var alpha = (255.0 - (255.0 / 700 * mScrollView!!.scrollY).toFloat()).toInt()
                if (alpha < 0) {
                    alpha = 0
                }
                if (alpha > 255) {
                    alpha = 255
                }
                backgroundImageView?.drawable?.alpha = alpha
            }

        }/*{ l, t, oldl, oldt ->
            var alpha = (255.0 - (255.0 / 700 * mScrollView.getScrollY()).toFloat()).toInt()
            if (alpha < 0) {
                alpha = 0
            }
            if (alpha > 255) {
                alpha = 255
            }
            backgroundImageView.getDrawable().alpha = alpha
        }*/)

        //风车背景旋转
        val animator = ObjectAnimator.ofFloat(mConstraintLayout, "rotation", 0f, 360f)
        animator.duration = 8000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.start()
        //字体逆时针旋转
        rotate(visibilityLinearLayout)
        rotate(humidityLinearLayout)
        rotate(pressureLinearLayout)
        rotate(windLinearLayout)
    }

    /**
     * 字体旋转
     * @param view
     */
    private fun rotate(view: View?) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f)
        animator.duration = 8000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    /**
     * 切换fragment时
     */
    @SuppressLint("ResourceAsColor")
    override fun onResume() {
        super.onResume()
        //scrollView增加监听
        mScrollView!!.viewTreeObserver.addOnScrollChangedListener {

            //获取activity的viewpager的状态
            val activity: MainActivity? = activity as MainActivity?
            val state: Int = activity!!.getState()
            //如果不是拖拉就加入scrollview监听拦截SwipeRefreshLayout的滑动时间，如果是就不加入，以免影响viewpager2和
            // SwipeRefreshLayout的滑动冲突处理
            if (state != ViewPager2.SCROLL_STATE_DRAGGING) {
                val refresh: SwipeRefreshLayout =
                    getActivity()!!.findViewById(R.id.srl_main_refresh)
                refresh.isEnabled = mScrollView!!.scrollY in 0..2
            }
        }
        //获取activity的背景，以跟随当前fragment更换背景。
        val background2: ConstraintLayout = activity!!.findViewById(R.id.cl_activity_main_bg)
        if (weather != null) {
            //设置当前城市名称
            val city = activity!!.findViewById<TextView>(R.id.tv_toolbar_city)
            city.text = weather?.city
            //根据早晚设置背景颜色
            val time: String = weather?.data!!.update_time
            val minuteTime: Int = TimeUtils.timeToMinutes(time.substring(11, 16))
            val sunrise: Int = TimeUtils.timeToMinutes(weather!!.data.sunrise)
            val sunset: Int = TimeUtils.timeToMinutes(weather?.data!!.sunrise)
            var `when` = "晚上"
            //判断白天还是晚上用于设置不同的视图
            if (minuteTime >= sunrise && minuteTime <= sunset) {
                `when` = "白天"
            }
            if (`when` == "晚上") {
                background2.setBackgroundResource(R.color.navyBlue)
            } else if (`when` == "白天") {
                background2.setBackgroundResource(R.drawable.background_gradient)
            }
        }
        //防止天气背景透明度重置
        var alpha = (255.0 - (255.0 / 700 * mScrollView!!.scrollY).toFloat()).toInt()
        if (alpha < 0) {
            alpha = 0
        }
        if (alpha > 255) {
            alpha = 255
        }
        backgroundImageView!!.drawable.alpha = alpha
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cl_fragment_top, R.id.cl_fragment_windmill, R.id.cd_fragment_advice -> {
                val intent = Intent(activity, DetailActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
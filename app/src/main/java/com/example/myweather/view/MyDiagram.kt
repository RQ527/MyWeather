package com.example.myweather.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.MeasureSpec
import android.widget.ImageView
import android.widget.Scroller
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.example.myweather.MainActivity
import com.example.myweather.R
import com.example.myweather.bean.Weather
import com.example.myweather.utils.SelectUtils
import com.example.myweather.utils.TimeUtils
import java.util.ArrayList

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */
class MyDiagram(context: Context,attrs:AttributeSet):ViewGroup(context,attrs) {
    private val TAG = "RQ"
    private var mHeight //控件高度
            = 0
    private var screenWidth //屏幕宽度
            = 0
    private var minTemp = 0 //最小温度初始值为0

    var viewWidth //view的宽
            = 0
    private var size = 1 //视图数量初始值为1

    private var mScaleX //X轴缩放比率
            = 0f
    private var mScaleY //Y轴缩放比率
            = 0f
    private var differDistance //与整数倍的viewWidth的偏移量
            = 0f
    private var drawX = 100f //圆环圆心初始值为100

    private var drawY = 300f //圆环圆心初始值为300

    private var detector //手势辅助器
            : GestureDetector? = null
    private var mVelocityTracker //滑动速度追踪器
            : VelocityTracker? = null
    private var mFling //惯性滑动线程
            : FlingRunnable? = null
    private var points //曲线上点的集合
            : List<Point>? = null
    private var mScroller //优化滑动效果的工具
            : Scroller? = null
    private var isFlinging = false //是否正在惯性滑动的标志

    private var weather //weather数据
            : Weather? = null
    private var curvePaint //曲线画笔
            : Paint? = null
    private var whiteCirclePaint //圆环内园画笔
            : Paint? = null
    private var textPaint //文字画笔
            : Paint? = null
    private var dottedPaint //虚线画笔
            : Paint? = null
    private var curvePath //曲线路径
            : Path? = null
    private var dottedPath //虚线路径
            : Path? = null
    private var view //展示天气的视图
            : View? = null

    init {

        //允许ViewGroup的onDraw生效
        setWillNotDraw(false)
        init()
        initPaint()
        //咦？你可能会疑问为什么没有初始化路径，因为我放在了omMeasure里面。具体看onMeasure
    }

    //从外界设置数据
    fun setWeather(weather: Weather?) {
        this.weather = weather
    }

    /**
     * 初始化其他东西
     */
    private fun init() {
        mVelocityTracker = VelocityTracker.obtain()
        mScroller = Scroller(MainActivity.context)
        screenWidth = getScreenWidth()
        viewWidth = (screenWidth / 5.0).toInt()

        //初始化手势识别器
        detector = GestureDetector(MainActivity.context, object : SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                scrollTo(distanceX.toInt() + scrollX, 0)
                //反拦截，处理于viewpage2的滑动冲突
                if (Math.abs(distanceX) >= Math.abs(distanceY) || Math.abs(distanceY) < 8) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                return true
            }
        })
    }

    /**
     * 初始化路径
     */
    fun initPath() {
        curvePath = Path()
        //这里面减0.1是为了防止误差画圆环找不到圆心，具体看画圆心的原理。
        curvePath!!.moveTo((points!![0].x - 1).toFloat(), points!![0].y.toFloat())
        //遍历点集合画曲线
        for (i in 0 until points!!.size - 1) {
            curvePath!!.cubicTo(
                ((points!![i].x + points!![i + 1].x) / 2).toFloat(),
                points!![i].y.toFloat(),
                (
                        (points!![i].x + points!![i + 1].x) / 2).toFloat(),
                points!![i + 1].y.toFloat(),
                points!![i + 1].x.toFloat(),
                points!![i + 1].y.toFloat()
            )
        }
        dottedPath = Path()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        mFling = FlingRunnable(MainActivity.context)
        curvePaint = Paint()
        curvePaint!!.isAntiAlias = true
        curvePaint!!.color = Color.parseColor("#FF03DAC5")
        curvePaint!!.strokeWidth = 15f
        curvePaint!!.style = Paint.Style.STROKE
        whiteCirclePaint = Paint()
        whiteCirclePaint!!.strokeWidth = 40f
        whiteCirclePaint!!.color = Color.WHITE
        whiteCirclePaint!!.isAntiAlias = true
        textPaint = Paint()
        textPaint!!.textSize = 40f
        textPaint!!.isAntiAlias = true
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.color = Color.BLACK
        dottedPaint = Paint()
        dottedPaint!!.isAntiAlias = true
        dottedPaint!!.strokeWidth = 3f
        dottedPaint!!.color = Color.GRAY
        dottedPaint!!.style = Paint.Style.STROKE
    }

    /**
     * 初始化天气数据
     */
    fun initWeather() {
        //防止weather为空，因此设置了假数据,也是累死了.
        if (weather == null) {
            val hours: MutableList<Weather.Data.Hour> = ArrayList<Weather.Data.Hour>()
            val hour = Weather.Data.Hour(
                "0", "null", "null", "null",
                "null", "null"
            )
            hours.add(hour)
            weather = Weather(
                0, " ", 0,
                Weather.Data(
                    "null", "null",
                    ArrayList(), Weather.Data.Aqi(
                        "null", "null",
                        "null", "null", "null", "null", "null",
                        "null", "null"
                    ), "null", "null", "null",
                    hours, "null",
                    Weather.Data.Index(
                        Weather.Data.Index.Chuangyi("null", "null", "null"),
                        Weather.Data.Index.Ganmao("null", "null", "null"),
                        Weather.Data.Index.Huazhuang("null", "null", "null"),
                        Weather.Data.Index.Xiche("null", "null", "null"),
                        Weather.Data.Index.Yundong("null", "null", "null"),
                        Weather.Data.Index.Ziwaixian("null", "null", "null")
                    ),
                    "null", "null", "null", "null", "null",
                    "null", "null", "null", "null", "null",
                    "null", "null", "null", "null", "null",
                    "null", "null", "null"
                ), 0, "null", 0
            )
        }

        //将weather的温度转化成点。
        if (weather!!.code !== 0 && weather!!.data != null) {
            points = weatherToPoint(weather!!)
        }
    }

    //重写scrollTo方法防止滑动过度
    override fun scrollTo(x: Int, y: Int) {
        var x = x
        if (x <= 0) {
            x = 0
        }
        if (x >= viewWidth * (size - 5)) {
            x = viewWidth * (size - 5)
        }
        super.scrollTo(x, y)
    }

    /**
     * 获取xml布局里的width和mHeight
     *
     * @param defaultSize 默认大小
     * @param measureSpec 解析的测量码
     * @return
     */
    private fun getMySize(defaultSize: Int, measureSpec: Int): Int {
        var mySize = defaultSize
        val mode = MeasureSpec.getMode(measureSpec) //测量模式
        val size = MeasureSpec.getSize(measureSpec) //测量尺寸
        when (mode) {
            MeasureSpec.UNSPECIFIED -> mySize = defaultSize
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> mySize = size
        }
        return mySize
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private fun getScreenWidth(): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    /**
     * 添加天气视图的方法。
     */
    private fun insertView() {
        if (weather?.code !== 0) {
            val hours: List<Weather.Data.Hour> = weather?.data!!.hour //获取的时间集合
            var hour: Weather.Data.Hour //用来存储小时天气
            var wea: String //天气状况
            var time: String //时间
            var timeTextView: TextView //展示时间的控件
            var weatherImageView: ImageView //展示天气的控件
            var weatherTextview: TextView //展示天气的TextView
            var fanTextView: TextView //展示风力的TextView
            var wind: String //风力
            var weatherPicture: Int //天气图片ID
            var minuteTime: Int //时间（分钟）
            //更新时间.
            val updateTime: String = weather!!.data.update_time
            val nowTime: Int = TimeUtils.timeToMinutes(updateTime.substring(11, 16))
            val sunrise: Int = TimeUtils.timeToMinutes(weather!!.data.sunrise) //日出时间（分钟）
            val sunset: Int = TimeUtils.timeToMinutes(weather!!.data.sunset) //日落时间（分钟）
            var textColor = Color.WHITE
            //判断白天还是晚上用于设置不同颜色的字体
            if (nowTime >= sunrise && nowTime <= sunset) {
                textColor = Color.BLACK
            }
            var `when` = "晚上" //白天还是晚上
            //添加天气视图
            for (i in 0 until size) {
                view = LayoutInflater.from(MainActivity.context)
                    .inflate(R.layout.item_mydaigram, null)
                hour = hours[i]
                wea = hour.wea
                time = hour.time
                minuteTime = TimeUtils.timeToMinutes(time.substring(11, 16))


                //判断白天还是晚上用于设置不同的视图
                if (minuteTime >= sunrise && minuteTime <= sunset) {
                    `when` = "白天"
                }
                weatherPicture = SelectUtils.selectWeatherPicture(wea, `when`)
                weatherImageView = view!!.findViewById(R.id.iv_weather)
                weatherImageView.setImageResource(weatherPicture)
                timeTextView = view!!.findViewById(R.id.tv_time)
                timeTextView.text = time.substring(11, 16)
                timeTextView.setTextColor(textColor)
                weatherTextview = view!!.findViewById(R.id.tv_weather)
                weatherTextview.text = hour.wea
                weatherTextview.setTextColor(textColor)
                wind = hour.wind_level
                fanTextView = view!!.findViewById(R.id.tv_fan)
                fanTextView.text = wind
                fanTextView.setTextColor(textColor)
                addView(view)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //获取控件高度
        mHeight = getMySize(100, heightMeasureSpec)
        //因为初始化weather需要height,而height在上面才初始化完成所以放在这。
        initWeather()
        //path也是一样
        initPath()
        //到这里数据加载完毕，添加视图。
        insertView()
        //测量view
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            childAt.measure(childAt.measuredWidth, childAt.measuredHeight)
        }
        if (view != null) {
            //获取view缩放比率
            mScaleX = viewWidth / view!!.measuredWidth.toFloat()
            mScaleY = mHeight / 2 / view!!.measuredHeight.toFloat()
        }
        setMeasuredDimension(size * viewWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val left = this.left
        //调整view位置
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            childAt.layout(left, mHeight / 2, viewWidth, mHeight)
            childAt.pivotX = 0f
            childAt.pivotY = 0f
            childAt.scaleX = mScaleX
            childAt.scaleY = mScaleY
            childAt.layout(
                viewWidth * i, mHeight / 2,
                (viewWidth / mScaleX).toInt() * (i + 1), (mHeight / mScaleY).toInt()
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画曲线
        canvas.drawPath(curvePath!!, curvePaint!!)
        //画圆环
        drawRing(drawX, drawY, canvas, curvePaint)
    }

    //为调整圆环位置以及优化圆环移动效果而开启的线程,这里更新的方法用的是郭神的思路，感谢郭神!!!
    private val mMoveDrawRun: Runnable = object : Runnable {
        /*算法:
        计算每次圆环位置的算法是画一条path于曲线路径相交，然后取交集，
        再用矩形计算交点位置，再得出圆环新的位置，并通知刷新。
         */
        private val linePath = Path()
        private val rect = RectF()
        override fun run() {
            linePath.reset() //重置路径
            //圆环的位置有两种情况，在前面一直在屏幕左方固定位置，在最后面可以通过点击来调整圆环的位置，所以分成了两种情况：
            if (scrollX >= viewWidth * (size - 5)) {
                // 绘制垂直线与曲线取交集
                linePath.moveTo(drawX, 0f)
                linePath.lineTo(drawX, (mHeight / 2 + 1).toFloat())

                // 这里就直接取一个底为1，高为控件高度的矩形
                linePath.lineTo(drawX + 1, (mHeight / 2 + 1).toFloat())
                linePath.lineTo(drawX + 1, 0f)
            } else if (scrollX < viewWidth * (size - 5)) {
                // 绘制垂直线与曲线取交集
                linePath.moveTo((scrollX + viewWidth / 2.0).toFloat(), 0f)
                linePath.lineTo((scrollX + viewWidth / 2.0).toFloat(), (mHeight / 2 + 10).toFloat())

                // 这里就直接取一个底为 1，高为控件高度的矩形
                linePath.lineTo(
                    (scrollX + viewWidth / 2.0 + 1).toFloat(),
                    (mHeight / 2 + 10).toFloat()
                )
                linePath.lineTo((scrollX + viewWidth / 2.0 + 1).toFloat(), 0f)
                drawX = (scrollX + viewWidth / 2.0).toFloat()
            }
            //取交集
            linePath.op(curvePath!!, Path.Op.INTERSECT)

            // 取完交集后使用下面这个得到包裹它的矩形
            linePath.computeBounds(rect, false)
            drawY = if (rect.top == 0f) {
                //为零就让其在底部
                (mHeight / 2).toFloat()
            } else if (points!![0].y >= points!![1].y && points!!.size != 0) {
                rect.top // 上升的点，矩形的  top 就是圆心 y
            } else {
                rect.bottom // 下降的点，矩形的  bottom 就是圆心 y
            }

            //这里更新的方法用的是郭神的思路，感谢郭神!!!
            invalidate() // 通知重绘，为什么放这里而不放在 onTouchEvent 中？原因看下面注释
            /*
             * 下面这个是重点！
             * 方法意思为：将这个 Runnable 放在下一个动画时间点再调用
             * 为什么要这样？
             * 1、调用 invalidate() 会回调 onDraw()，但并不是马上就回调
             * 2、onTouchEvent 的回调频率与 onDraw() 的回调频率并不相同
             * 3、为了节省资源，减少不必要的回调，刷新率 60 的手机是每 16 毫秒回调一次 onDraw()
             *    刷新率 90 的手机是每 11 毫秒回调一次（计算方式：1 秒 = 1000 毫秒，1000 / 60 = 16）
             * 4、正是因为这样的设计，并不需要每次产生移动就重新计算圆心坐标后调用 invalidate() 刷新，
             *    所以官方就提供了下面这个方法，你可以在很多官方源码中看见这种用法
             * */ViewCompat.postOnAnimation(this@MyDiagram, this)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        differDistance = 0f
        //增加速度追踪器
        mVelocityTracker!!.addMovement(event)
        detector!!.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //对应圆环位于后面的情况
                if (scrollX >= (size - 5) * viewWidth) {
                    drawX = if (scrollX + event.x > (size - 0.5) * viewWidth) {
                        scrollX + 4.5.toFloat() * viewWidth - 1
                    } else {
                        scrollX + event.x
                    }
                }
                //按下开始监听
                mMoveDrawRun.run()
                //按下就停止正在的滑动
                mFling!!.stop()
            }
            MotionEvent.ACTION_MOVE ->
                //对应圆环位于后面的情况
                if (scrollX >= (size - 5) * viewWidth) {
                    drawX = if (scrollX + event.x > (size - 0.5) * viewWidth) {
                        scrollX + 4.5.toFloat() * viewWidth - 1
                    } else {
                        scrollX + event.x
                    }
                }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                //一旦消费了事件就反拦截
                parent.requestDisallowInterceptTouchEvent(true)
                //对应圆环位于后面的情况
                if (scrollX >= (size - 5) * viewWidth) {
                    drawX = (scrollX + viewWidth / 2.0).toFloat()
                }
                //测量1秒的滑动速度
                mVelocityTracker!!.computeCurrentVelocity(1000)
                //获取水平方向的滑动速度
                val xVelocity = mVelocityTracker!!.xVelocity
                //大于最小速度才算滑动
                if (Math.abs(xVelocity) > ViewConfiguration.get(MainActivity.context).scaledMinimumFlingVelocity) {
                    //从当前位置开始滑动
                    val initX = scrollX
                    //最大滑动距离为控件宽度
                    val maxX = viewWidth * size
                    if (maxX > 0) {
                        isFlinging = true
                        mFling!!.start(initX, xVelocity.toInt(), initX, maxX)
                    }
                }
                //如果没有惯性滑动就调整Ui
                if (!isFlinging) {
                    adjustUi()
                }
                removeCallbacks(mMoveDrawRun) // 抬手或被父布局拦截时关闭 mMoveDrawRun
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 调整控件位置保持只有五个view出现在屏幕中
     */
    private fun adjustUi() {
        differDistance = (scrollX % viewWidth).toFloat()
        if (differDistance >= viewWidth / 2) {
            differDistance = viewWidth - differDistance
        } else if (differDistance < viewWidth / 2) {
            differDistance = -differDistance
        }
        //平滑过渡
        mScroller!!.startScroll(scrollX, scrollY, differDistance.toInt(), 0)
        invalidate()
    }

    //滑动工具类的计算滑动方法
    override fun computeScroll() {
        //滑动同时调整圆环位置
        mMoveDrawRun.run()
        if (mScroller!!.computeScrollOffset()) {
            val currX = mScroller!!.currX.toFloat()
            scrollTo(currX.toInt(), 0)
            invalidate()
        } else {
            //滑动结束移除圆环位置监听
            removeCallbacks(mMoveDrawRun)
        }
    }

    /**
     * 进行惯性滚动线程，因为滚动比较耗时所以另外开启的线程进行滚动
     */
    private inner class FlingRunnable internal constructor(context: Context?) : Runnable {
        private val mScroller: Scroller
        private var mInitX //开始滚动位置
                = 0
        private var mMinX = 0
        private var mMaxX = 0
        private var mVelocityX = 0
        fun start(initX: Int, velocityX: Int, minX: Int, maxX: Int) {
            mInitX = initX
            mVelocityX = velocityX
            mMinX = minX
            mMaxX = maxX

            // 先停止上一次的滚动
            if (!mScroller.isFinished) {
                mScroller.abortAnimation()
            }

            // 开始 fling
            mScroller.fling(
                initX, 0, velocityX,
                0, 0, maxX, 0, 0
            )
            post(this)
        }

        override fun run() {
            // 如果已经结束，就不再进行
            if (!mScroller.computeScrollOffset()) {
                return
            }
            // 计算偏移量
            val currX = mScroller.currX
            var diffX = mInitX - currX
            // 用于记录是否超出边界，如果已经超出边界，则不再进行回调，即使滚动还没有完成
            var isEnd = false
            if (diffX != 0) {
                // 超出右边界，进行修正
                if (getScrollX() + diffX >= viewWidth * size) {
                    diffX = viewWidth * size - getScrollX()
                    isEnd = true
                }
                // 超出左边界，进行修正
                if (getScrollX() + diffX <= 0) {
                    diffX = 0 - getScrollX()
                    isEnd = true
                }
                if (!mScroller.isFinished) {
                    scrollBy(diffX, 0)
                }
                mInitX = currX
            }
            //在滚动就更新UI
            if (!isEnd) {
                post(this)
            }
            //滚动停止调整UI
            if (mScroller.computeScrollOffset()) {
                adjustUi()
                isFlinging = false
            }
        }

        /**
         * 进行停止
         */
        fun stop() {
            if (!mScroller.isFinished) {
                mScroller.abortAnimation()
            }
        }

        init {
            mScroller = Scroller(context, null, false)
        }
    }

    /**
     * weather转化成point的方法
     *
     * @param weather2 数据weather
     * @return
     */
    private fun weatherToPoint(weather2: Weather): List<Point>? {
        //用于保存获取的温度
        var temp: Int
        //点的集合
        val points: MutableList<Point> = ArrayList()
        //获取hours的集合
        val hours: List<Weather.Data.Hour> = weather2.data.hour
        size = hours.size
        //获取最小温度
        minTemp = hours[0].temp.toInt()
        //找出最小温度
        for (i in hours.indices) {
            val hour: Weather.Data.Hour = hours[i]
            temp = hour.temp.toInt()
            if (minTemp >= temp) {
                minTemp = temp
            }
        }
        //遍历转化成点
        for (i in hours.indices) {
            val hour: Weather.Data.Hour = hours[i]
            temp = hour.temp.toInt()
            val point = Point()
            point.y = mHeight / 2 - mHeight / 2 / 15 * (temp - minTemp)
            point.x = ((2 * (i + 1) - 1) * (viewWidth / 2.0)).toInt()
            points.add(point)
        }
        return points
    }

    /**
     * 画圆环
     *
     * @param x      圆环圆心坐标x
     * @param y      圆环圆心坐标y
     * @param canvas 画布
     * @param paint  画笔
     */
    private fun drawRing(x: Float, y: Float, canvas: Canvas, paint: Paint?) {
        //外圆
        canvas.drawCircle(x, y, 40f, paint!!)
        //内园
        canvas.drawCircle(x, y, 33f, whiteCirclePaint!!)
        //计算温度
        val temp = Math.round((mHeight / 2 - y) * (15.toFloat() / (mHeight / 2))) + minTemp
        //文字
        canvas.drawText(temp.toString(), x, y + 10, textPaint!!)
        //虚线
        var drawDistance = 50
        while (y + drawDistance <= mHeight / 2) {
            dottedPath!!.moveTo(x, y + drawDistance)
            dottedPath!!.lineTo(x, y + drawDistance + 15)
            canvas.drawPath(dottedPath!!, dottedPaint!!)
            drawDistance += 30
        }
        dottedPath!!.reset()
    }
}
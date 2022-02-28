package com.example.myweather.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myweather.MainActivity
import com.example.myweather.R

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */
class MyMenu(context: Context, attrs: AttributeSet) : View(context, attrs), View.OnClickListener {
    lateinit var listener:MyMenuListener
    lateinit var popupWindow:PopupWindow
    private var background //菜单图片
            : Bitmap? = null
    private var paint: Paint? = null

    //图片的缩放比例
    private var mScaleX = 0f
    private var mScaleY = 0f
    private fun initView() {
        paint = Paint()
        paint!!.isAntiAlias = true
        background = BitmapFactory.decodeResource(resources, R.drawable.menu)
    }

    init {
        initView()
        this.setOnClickListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width: Int = getMySize(100, widthMeasureSpec)
        val height: Int = getMySize(100, heightMeasureSpec)
        //以短的一边进行缩放
        //以短的一边进行缩放
        if (width >= height) {
            mScaleX = height / background!!.width.toFloat()
            mScaleY = height / background!!.height.toFloat()
        } else {
            scaleX = width / background!!.width.toFloat()
            scaleY = width / background!!.height.toFloat()
        }

        setMeasuredDimension(width, height)
    }

    private fun getMySize(defaultSize: Int, measureSpec: Int): Int {
        var mySize: Int = defaultSize

        val mode = MeasureSpec.getMode(measureSpec) //测量模式

        val size = MeasureSpec.getSize(measureSpec) //测量尺寸


        when (mode) {
            MeasureSpec.UNSPECIFIED -> mySize = defaultSize
            MeasureSpec.AT_MOST -> mySize = size
            MeasureSpec.EXACTLY -> mySize = size
        }
        return mySize
    }

    override fun onDraw(canvas: Canvas?) {
        //缩放矩阵
        val matrix = Matrix()
        matrix.preScale(mScaleX, mScaleY)
        //缩放操作
        background?.let { canvas!!.drawBitmap(it, matrix, paint) }
    }
    override fun onClick(p0: View?) {
        openPopupWindow()
    }

    private fun openPopupWindow() {
        val view =
            LayoutInflater.from(MainActivity.context).inflate(R.layout.popupwindow_list, null)
        val listView = view.findViewById<ListView>(R.id.lt_popupWindow)
        //数据
        val data = arrayOf("设置", "关于")
        val adapter = object :
            ArrayAdapter<String>(MainActivity.context!!, android.R.layout.simple_list_item_1, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView =super.getView(position, convertView, parent)
                textView.foregroundGravity=Gravity.CENTER
                        return textView
            }
        }
        listView.adapter = adapter
        listView.setOnItemClickListener(object :AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (data[p2] == "关于" && listener != null) {
                    listener.itemListen(p2)
                } else {
                    //点击反应
                    Toast.makeText(MainActivity.context, data[p2], Toast.LENGTH_SHORT)
                        .show()
                }
                //影藏弹窗
                //影藏弹窗
                dismissPopupWindow()
            }

        })

        popupWindow = PopupWindow(view,250,ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isFocusable = true
        popupWindow.animationStyle = R.style.mypopwindow_anim_style
        popupWindow.showAsDropDown(this, -170, 50)
    }

    private fun dismissPopupWindow() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * menu监听接口
     */
    interface MyMenuListener {
        fun itemListen(position: Int)
    }

    /**
     * 为menu设置监听
     * @param listen
     */
    fun setListen(l: MyMenuListener) {
        listener = l
    }
}
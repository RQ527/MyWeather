package com.example.myweather.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import kotlin.math.abs

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */

class MyScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ScrollView(context, attrs, defStyle) {
    lateinit var mListener:ScrollListener
    interface ScrollListener{
        fun scrollOritention(l:Int,t:Int,oldl:Int,oldt:Int)
    }

    fun setScrollListener(l:ScrollListener){
        mListener=l
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mListener.scrollOritention(l,t,oldl, oldt)
    }
    var startX:Float = 0f
    var startY:Float = 0f
    var endX:Float = 0f
    var endY:Float = 0f
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            when(ev.action){
                MotionEvent.ACTION_DOWN -> {startX =ev.x;startY=ev.y}
                MotionEvent.ACTION_MOVE -> {
                endX = ev.x
                endY = ev.y
                //拦截viewpager的滑动
                //拦截viewpager的滑动
                if (abs(endX - startX) > abs(endY - startY) && abs(endX - startX) > 8) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
                else -> null
            }
        }
        return super.onTouchEvent(ev)
    }
}
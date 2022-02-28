package com.example.myweather.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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
class MyRecyclerAdapter(_data: ArrayList<Weather?>?, _context: Context) :
    RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {
    val data = _data
    val context = _context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRecyclerAdapter.MyViewHolder {
        val view = View.inflate(context, R.layout.item_rv, null)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "Range")
    override fun onBindViewHolder(holder: MyRecyclerAdapter.MyViewHolder, position: Int) {
        val weather = data?.get(position)
        val time = weather?.data?.update_time
        val minuteTime = time?.let { TimeUtils.timeToMinutes(it.substring(11, 16)) }
        val sunrise = weather?.data?.let { TimeUtils.timeToMinutes(it.sunrise) }
        val sunset = weather?.data?.let { TimeUtils.timeToMinutes(it.sunset) }
        var `when` = "晚上"
        if (sunrise != null) {
            if (minuteTime in sunrise..sunset!!) {
                `when` = "白天";
                holder.backgroundCardView.setCardBackgroundColor(Color.parseColor("#6699FF"));
            }
        }
        holder.cityTextView.text = data?.get(position)!!.city
        holder.tempTextView.text = data[position]?.data?.temp + "℃"
        if (weather != null) {
            holder.backgroundImageView.setImageResource(
                SelectUtils.selectWeatherBackground(
                    weather.data.weather, `when`
                )
            )
        }
        holder.backgroundImageView.alpha= 200F
    }

    override fun getItemCount(): Int = data?.size ?: 0

    inner class MyViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        var cityTextView: TextView = _itemView.findViewById(R.id.tv_itemRv_city)
        var tempTextView: TextView = _itemView.findViewById(R.id.tv_itemRv_temp)
        var backgroundCardView: CardView =
            _itemView.findViewById(R.id.cd_recyclerView_background)
        var backgroundImageView: ImageView =
            _itemView.findViewById(R.id.iv_recyclerView_background)

        init {
            _itemView.setOnClickListener {
                mOnItemClickListener.onRecyclerItemClick(adapterPosition, itemView)
            }
            _itemView.setOnLongClickListener {
                mOnItemClickListener.onRecyclerItemLongClick(adapterPosition, itemView)
                return@setOnLongClickListener true
            }
        }
    }

    private lateinit var mOnItemClickListener: OnRecyclerItemClickListener

    fun setRecyclerItemClickListener(listener: OnRecyclerItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnRecyclerItemClickListener {
        fun onRecyclerItemClick(position: Int, view: View)
        fun onRecyclerItemLongClick(position: Int, view: View)
    }
}
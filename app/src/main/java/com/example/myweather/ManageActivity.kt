package com.example.myweather

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.adapter.MyRecyclerAdapter
import com.example.myweather.base.BaseActivity
import com.example.myweather.bean.Weather
import com.example.myweather.room.IDispose
import com.example.myweather.room.MyDataBase
import com.example.myweather.room.WeatherDao
import com.example.myweather.utils.RoomUtils
import java.util.ArrayList

class ManageActivity : BaseActivity(),View.OnClickListener {
    private val TAG = "RQ"
    private var mRecyclerView: RecyclerView? = null
    private var weatherDao: WeatherDao? = null
    private var myDataBase: MyDataBase? = null
    private var data //recyclerView的数据源
            : ArrayList<Weather?>? = null
    private var backButton: Button? = null
    private var addButton: Button? = null
    private var recyclerAdapter: MyRecyclerAdapter? = null
    private val mIntent = Intent() //用于携带数据


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        initView()
        //加载数据
        weatherDao?.let {
            RoomUtils.queryAll(object : IDispose {
                override fun runOnUi(weather: Weather) {}
                override fun runOnUi(weathers: List<Weather>) {
                    if (weathers != null) {
                        //遍历weathers添加数据
                        for (weather in weathers) {
                            data!!.add(weather)
                        }
                        recyclerAdapter = MyRecyclerAdapter(data, this@ManageActivity)
                        //设置item监听
                        recyclerAdapter!!.setRecyclerItemClickListener(object :
                            MyRecyclerAdapter.OnRecyclerItemClickListener {
                            override fun onRecyclerItemClick(position: Int, view: View) {
                                //点击就跳转，并携带位置
                                mIntent.putExtra("position", position.toString())
                                setResult(0, mIntent)
                                finish()
                            }

                            override fun onRecyclerItemLongClick(position: Int, view: View) {
                                //长按弹出窗口
                                openPopupWindow(view, position)
                            }
                        })
                        mRecyclerView!!.adapter = recyclerAdapter
                        mRecyclerView!!.layoutManager = LinearLayoutManager(this@ManageActivity)
                    }
                }
            }, it)
        }
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.rv_manageActivity_city)
        myDataBase = MyDataBase.getInstance(this)
        weatherDao = myDataBase!!.getWeatherDao()
        data = ArrayList<Weather?>()
        backButton = findViewById(R.id.bt_toolbar_back)
        addButton = findViewById(R.id.bt_toolbar_addCity)
        backButton!!.setOnClickListener(this)
        addButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_toolbar_back -> finish()
            R.id.bt_toolbar_addCity -> {
                val intent2 = Intent(this@ManageActivity, AddWeatherActivity::class.java)
                intent2.putExtra("flag", "manage")
                startActivityForResult(intent2, 0)
            }
        }
    }

    var popupWindow: PopupWindow? = null

    //用popupWindow作为弹出的dialog
    private fun openPopupWindow(mView: View, mPosition: Int) {
        //渲染
        val view =
            LayoutInflater.from(MainActivity.context).inflate(R.layout.popupwindow_list, null)
        val listView = view.findViewById<ListView>(R.id.lt_popupWindow)
        //数据
        val data2 = arrayOf("删除")
        //让文本显示居中
        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            MainActivity.context!!,
            android.R.layout.simple_list_item_1,
            data2
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                textView.gravity = Gravity.CENTER
                return textView
            }
        }
        listView.adapter = adapter

        //设置点击事件
        listView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view1: View?, position: Int, id: Long ->
                val city = mView.findViewById<TextView>(R.id.tv_itemRv_city)
                for (weather in data!!) {
                    if (weather!!.city == city.text.toString()) {
                        data!!.remove(weather)
                        recyclerAdapter!!.notifyItemRemoved(mPosition)
                        break
                    }
                }
                weatherDao?.let { RoomUtils.delete(it, city.text.toString()) }
                mIntent.putExtra("position2", mPosition.toString())
                setResult(0, mIntent)
                //如果删完了就跳转至添加城市界面
                if (data!!.size == 0) {
                    val intent3 = Intent(this@ManageActivity, AddWeatherActivity::class.java)
                    intent3.putExtra("flag", "manage")
                    startActivityForResult(intent3, 0)
                }

                //影藏弹窗
                dismissPopupWindow()
            }
        popupWindow = PopupWindow(view, 250, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isFocusable = true
        popupWindow!!.animationStyle = R.style.mypopwindow2_anim_style
        popupWindow!!.showAsDropDown(mView, 300, -350)
    }

    private fun dismissPopupWindow() {
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
            popupWindow = null
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            //添加后通知recyclerView更新
            val weather: Weather? = data.getSerializableExtra("weather") as Weather?
            this.data!!.add(weather)
            recyclerAdapter!!.notifyDataSetChanged()
        }
    }
}
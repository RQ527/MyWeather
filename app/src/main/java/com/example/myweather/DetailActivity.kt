package com.example.myweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.myweather.base.BaseActivity

class DetailActivity : BaseActivity() {
    private var backButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        backButton = findViewById(R.id.bt_detail_back)
        backButton?.setOnClickListener { finish() }
    }
}
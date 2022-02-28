package com.example.myweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myweather.base.BaseActivity

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }
}
package com.example.myweather.utils

import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import javax.security.auth.callback.Callback

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/26
 */
class NetUtils {

    companion object {

        fun sendRequest(
            url: String,
            order: String,
            name: String,
            value: String,
            callback: okhttp3.Callback
        ) {
            var request: Request
            val okHttpClient: OkHttpClient = OkHttpClient()
            if (order == "Get" || order == "GET" || order == "get") {
                request = Request.Builder()
                    .url(url)
                    .build()
            } else if (order == "Post" || order == "POST" || order == "post") {
                val formBody = FormBody.Builder()
                    .add("token", "MFyGNtFWMZOlVL2l")
                    .add(name, value)
                    .build()

                request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
            } else {
                throw Exception("请求命令错误")
            }
            val call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }
    }
}
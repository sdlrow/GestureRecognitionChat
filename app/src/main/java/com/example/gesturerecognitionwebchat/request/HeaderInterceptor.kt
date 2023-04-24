package com.example.gesturerecognitionwebchat.request

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Content-Type", "application/json")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

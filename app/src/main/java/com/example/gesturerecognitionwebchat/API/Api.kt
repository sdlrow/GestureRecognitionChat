package com.example.gesturerecognitionwebchat.API

import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.Data.RegistrationResponse
import com.example.gesturerecognitionwebchat.Data.TestData
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {
    @GET("cart/getCartItems") // при логине
    fun getJWTtest(): Deferred<Response<List<TestData>>>

    @GET("cart/chatHistory") // при логине
    fun getChatHistory(): Deferred<Response<ArrayList<ChatHistory>>>
}
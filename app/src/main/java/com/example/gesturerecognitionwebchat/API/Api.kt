package com.example.gesturerecognitionwebchat.API

import com.example.gesturerecognitionwebchat.Data.*
import com.example.gesturerecognitionwebchat.utils.Status
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface Api {
    @GET("cart/getCartItems") // при логине
    fun getJWTtest(): Deferred<Response<List<TestData>>>

    @GET("cart/chatHistory") // при логине
    fun getChatHistory(): Deferred<Response<ArrayList<ChatHistory>>>

    @PUT("auth/changePassword") // при логине
    fun changePassword(
        @Body registrationJson: RequestBody
    ): Deferred<Response<ResultStatus>>

    @PUT("auth/changeEmail") // при логине
    fun changeEmail(
        @Body registrationJson: RequestBody
    ): Deferred<Response<ResultStatus>>

    @POST("auth/sessionEnd") // при логине
    fun sessionEnd(
        @Body registrationJson: RequestBody
    ): Deferred<Response<ResultStatus>>

    @POST("auth/deleteUser") // при логине
    fun deleteUserAccount(): Deferred<Response<ResultStatus>>

}
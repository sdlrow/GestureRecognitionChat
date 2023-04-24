package com.example.gesturerecognitionwebchat.API

import com.example.gesturerecognitionwebchat.Data.LoginResponse
import com.example.gesturerecognitionwebchat.Data.RegistrationResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenApi {
    @POST("auth/signup") // при логине
    fun registration(
        @Body registrationJson: RequestBody
    ): Deferred<Response<RegistrationResponse>>

    @POST("auth/signin") // при логине
    fun login(
        @Body registrationJson: RequestBody
    ): Deferred<Response<LoginResponse>>
}
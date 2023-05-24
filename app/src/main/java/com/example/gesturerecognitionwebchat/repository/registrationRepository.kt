package com.example.gesturerecognitionwebchat.repository

import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.API.OpenApi
import com.example.gesturerecognitionwebchat.ApiCaller
import com.example.gesturerecognitionwebchat.CoroutineCaller
import com.example.gesturerecognitionwebchat.Data.LoginResponse
import com.example.gesturerecognitionwebchat.Data.RegistrationResponse
import com.example.gesturerecognitionwebchat.RequestResult
import com.example.gesturerecognitionwebchat.base.PrefManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class RegistrationRepository(
    private val openApi: OpenApi,
    private val api: Api,
    private val prefManager: PrefManager
): CoroutineCaller by ApiCaller() {
    suspend fun postRegistration(email: String, password: String, roles: List<String>): RequestResult<RegistrationResponse>{
        val registrationJson = """
            {
                "email": "$email",
                "password": "$password",
                "role": ${roles.map { "\"$it\"" }.joinToString(prefix = "[", postfix = "]")}
            }
        """.trimIndent()

        val requestBody = registrationJson.toRequestBody("application/json".toMediaTypeOrNull())

        return coroutineApiCall(openApi.registration(requestBody))
    }

    suspend fun sendLogin(email: String, password: String): RequestResult<LoginResponse>{
        val registrationJson = """
            {
                "username": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = registrationJson.toRequestBody("application/json".toMediaTypeOrNull())

        return coroutineApiCall(openApi.login(requestBody))
    }



    suspend fun testJWT() =
        coroutineApiCall(api.getJWTtest())
}

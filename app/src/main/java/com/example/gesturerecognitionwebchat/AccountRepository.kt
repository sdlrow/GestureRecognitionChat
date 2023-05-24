package com.example.gesturerecognitionwebchat

import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.Data.ResultStatus
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ChangeRepository(private val api: Api): CoroutineCaller by ApiCaller() {

    suspend fun changePassword(oldPassword: String, password: String): RequestResult<ResultStatus> {
        val registrationJson = """
            {
                "oldPassword": "$oldPassword"
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = registrationJson.toRequestBody("application/json".toMediaTypeOrNull())
        return coroutineApiCall(api.changePassword(requestBody))
    }


    suspend fun changeEmail(email: String, password: String): RequestResult<ResultStatus> {
        val registrationJson = """
            {
                "newEmail": "$email"
                "password": "$password"
            }
        """.trimIndent()

        val requestBody = registrationJson.toRequestBody("application/json".toMediaTypeOrNull())
        return coroutineApiCall(api.changeEmail(requestBody))
    }

}
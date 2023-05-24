package com.example.gesturerecognitionwebchat

import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.Data.ResultStatus
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class NewChatRepository(private val api: Api): CoroutineCaller by ApiCaller() {

    suspend fun sessionEnd(time: String, duration: String, date: String, room: String): RequestResult<ResultStatus> {
        val json = """
            {
                "time": "$time",
                "duration": "$duration",
                "date": "$date",
                "room": "$room",
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        return coroutineApiCall(api.sessionEnd(requestBody))
    }

}
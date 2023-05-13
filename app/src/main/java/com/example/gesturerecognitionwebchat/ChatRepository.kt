package com.example.gesturerecognitionwebchat

import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.Data.RegistrationResponse

class ChatRepository(private val api: Api): CoroutineCaller by ApiCaller() {

    suspend fun getChatHistory(): RequestResult<ArrayList<ChatHistory>>{
        return coroutineApiCall(api.getChatHistory())
    }
}
package com.example.gesturerecognitionwebchat.Data

import androidx.annotation.Keep

@Keep
data class RegistrationResponse(
    val message: String?
)

@Keep
data class ChatHistory(
    val sessionId: Int,
    val sessionLanguage: String?,
    val duration: String?,
    val emailBlur: String = "U"
)

@Keep
data class LoginResponse(
    val accessToken: String?
)

@Keep
data class TestData(
    val id: Int?
)
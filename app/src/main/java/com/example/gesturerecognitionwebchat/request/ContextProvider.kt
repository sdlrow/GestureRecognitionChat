package com.example.gesturerecognitionwebchat.request

import android.content.Context

class ContextProvider( private val context : Context) {
    fun getContext() = context
}
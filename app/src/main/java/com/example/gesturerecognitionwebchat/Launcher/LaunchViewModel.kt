package com.example.gesturerecognitionwebchat.Launcher

import androidx.lifecycle.ViewModel
import com.example.gesturerecognitionwebchat.base.PrefManager

class LaunchViewModel(
    private val prefManager: PrefManager
) : ViewModel() {
    fun isLoginAndPasswordExist() = prefManager.isLoginAndPasswordExist()
}
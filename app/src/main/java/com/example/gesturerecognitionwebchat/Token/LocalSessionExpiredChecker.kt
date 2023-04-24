package com.example.gesturerecognitionwebchat.Token

import java.util.*

class LocalSessionExpiredChecker(val sessionTime: Long = defaultSessionTime) {

    var lastTime: Long = -1L
    fun saveCurrentTime() {
        lastTime = Date().time
    }

    fun clearUserLastActiveTime() {
        lastTime = -1L
    }

    fun isLastTimeClear () = lastTime == -1L

    fun isTimeExpired(): Boolean {
        if (lastTime == -1L) return false
        val currentTime = Date()
        if (currentTime.time - lastTime > sessionTime) {
            return true
        }
        return false
    }

    companion object {
        const val defaultSessionTime = 86400000L
    }
}
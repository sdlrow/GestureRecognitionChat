package com.example.gesturerecognitionwebchat

import android.util.Log
import com.example.gesturerecognitionwebchat.Token.TokenHolder
import com.example.gesturerecognitionwebchat.base.BaseViewModel
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository

class RegistrationViewModel(private val registrationRepository: RegistrationRepository) :
    BaseViewModel() {
    fun registerTest() {
        uiCaller.makeRequest({ registrationRepository.postRegistration(
            "test1221",
            "test1221@gmail.com",
            "test123",
            listOf("user")

        ) }) { requestResult ->
            uiCaller.unwrap(requestResult) {
                Log.d("test23M", it.message.toString())
            }
        }
    }

    fun login(email: String, password: String) {
        uiCaller.makeRequest({ registrationRepository.sendLogin(
            email,
            password

        ) }) { requestResult ->
            uiCaller.unwrap(requestResult) {
                TokenHolder.access_token = it.accessToken ?: ""
                Log.d("test23Access", it.accessToken.toString())
                testJWT()
            }
        }
    }

    fun testJWT(){
        uiCaller.makeRequest({
            registrationRepository.testJWT()
        }) {
            when (it) {
                is RequestResult.Success -> {
                    Log.d("test23S", "Success")
                }
                is RequestResult.Error -> {
                    Log.d("test23F", "Fail")
                }
            }
        }
    }
}
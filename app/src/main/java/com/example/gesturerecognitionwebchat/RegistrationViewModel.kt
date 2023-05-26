package com.example.gesturerecognitionwebchat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gesturerecognitionwebchat.Token.TokenHolder
import com.example.gesturerecognitionwebchat.base.BaseViewModel
import com.example.gesturerecognitionwebchat.base.PrefManager
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository

class RegistrationViewModel(
    private val registrationRepository: RegistrationRepository, private val prefManager: PrefManager
) :
    BaseViewModel() {
    var errorButtonLiveData = MutableLiveData<String>()
    var loginPasswordLiveData = MutableLiveData<Boolean>()
    var registerPasswordLiveData = MutableLiveData<Boolean>()
    fun register(email: String, password: String) {
        uiCaller.makeRequest({
            registrationRepository.postRegistration(
                email,
                password,
                listOf("user")
            )
        }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage->
                errorButtonLiveData.value = errorMessage
            }) {
                registerPasswordLiveData.value = true
                Log.d("test23M", it.message.toString())
            }
        }
    }

    fun login(email: String, password: String) {
        uiCaller.makeRequest({ registrationRepository.sendLogin(
            email,
            password

        ) }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage->
                errorButtonLiveData.value = errorMessage
            }) {
                TokenHolder.access_token = it.accessToken ?: ""
                prefManager.clean()
                prefManager.saveLogin(email)
                prefManager.savePassword(password)
                loginPasswordLiveData.value = true
                Log.d("test23Access", it.accessToken.toString())
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
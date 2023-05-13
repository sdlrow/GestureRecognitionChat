package com.example.gesturerecognitionwebchat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.base.BaseViewModel
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository

class ChatViewModel(private val chatRepository: ChatRepository) :
    BaseViewModel() {
    var errorButtonLiveData = MutableLiveData<String>()
    var chatLiveData = MutableLiveData<ArrayList<ChatHistory>>()

    fun showChatHistory() {
        uiCaller.makeRequest({
            chatRepository.getChatHistory()
        }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage ->
                errorButtonLiveData.value = errorMessage
            }) {
                chatLiveData.value = it
            }
        }
    }

}
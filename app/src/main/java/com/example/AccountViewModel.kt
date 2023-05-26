package com.example

import com.example.gesturerecognitionwebchat.AccountRepository
import com.example.gesturerecognitionwebchat.base.BaseViewModel
import com.example.gesturerecognitionwebchat.base.PrefManager
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository

class AccountViewModel(
    private val accountRepository: AccountRepository, private val prefManager: PrefManager
) :
    BaseViewModel() {

    fun getLogin(): String = prefManager.getLogin().toString()

    fun deleteUser() {
        uiCaller.makeRequest({
            accountRepository.deleteUser()
        }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage ->

            }) {

            }
        }
    }

    fun changePassword(oldPassword: String, password: String) {
        uiCaller.makeRequest({
            accountRepository.changePassword(oldPassword, password)
        }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage ->

            }) {

            }
        }
    }

    fun changeEmail(email: String, password: String) {
        uiCaller.makeRequest({
            accountRepository.changeEmail(email, password)
        }) { requestResult ->
            uiCaller.unwrap(requestResult, { errorMessage ->

            }) {

            }
        }
    }

}
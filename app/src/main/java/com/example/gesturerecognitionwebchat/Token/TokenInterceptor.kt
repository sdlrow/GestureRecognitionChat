package com.example.gesturerecognitionwebchat.Token

import android.annotation.SuppressLint
import com.example.gesturerecognitionwebchat.API.OpenApi
import com.example.gesturerecognitionwebchat.ApiCaller
import com.example.gesturerecognitionwebchat.request.ContextProvider
import com.example.gesturerecognitionwebchat.CoroutineCaller
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException

class TokenInterceptor(private val openApi: OpenApi) : Interceptor, KoinComponent,
    CoroutineCaller by ApiCaller() {

    private val contextProvider: ContextProvider by inject()

    val localSessionExpiredChecker = LocalSessionExpiredChecker(10000L)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        token = "Bearer " + TokenHolder.access_token
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        setAuthHeader(requestBuilder, token)
        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401 && (localSessionExpiredChecker.isTimeExpired() || localSessionExpiredChecker.isLastTimeClear())) {
            localSessionExpiredChecker.saveCurrentTime()
//            val res = coroutineApiCall(
//                openApi.sendLoginRefresh(refreshToken = TokenHolder.refresh_token)
//            )
//
//
//            when (res) {
//                is RequestResult.Success<*> -> {
//                    TokenHolder.access_token = res.result?.access_token ?: ""
//                    TokenHolder.refresh_token = res.result?.refresh_token ?: ""
//                    setAuthHeader(requestBuilder, "Bearer " + res.result?.access_token)
//                    response.close()
//                    return@runBlocking response
//                }
//                is RequestResult.Error -> {
//                    TokenHolder.access_token = ""
//                    TokenHolder.fio = ""
//                    TokenHolder.tokenFirebase = ""
//                    val intent = Intent(contextProvider.getContext(), LoginActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    contextProvider.getContext().startActivity(intent)
//                }
//            }
        }

        return@runBlocking response
    }

    @SuppressLint("HardwareIds")
    private fun setAuthHeader(builder: Request.Builder, token: String?) {
        if (token != null) {
            builder.addHeader(KEY_AUTH, token)
        }
    }

    companion object {
        val KEY_AUTH = "Authorization"
        var token: String? = null
    }
}

object TokenHolder {
    var access_token = ""
    var refresh_token = ""
    var phone = ""
    var fio = ""
    var tokenFirebase = ""
    var avatar = ""
    var id: Long = 1
    var customer_user_id: String? = null
    var firstName = ""
    fun clearAll() {
        access_token = ""
        refresh_token = ""
        phone = ""
        fio = ""
        tokenFirebase = ""
        avatar = ""
        id = 1
        customer_user_id = null
    }
}
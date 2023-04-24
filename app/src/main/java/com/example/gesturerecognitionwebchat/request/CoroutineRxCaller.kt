package com.example.gesturerecognitionwebchat

import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Deferred
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

interface CoroutineCaller {
    suspend fun <T> coroutineApiCall(deferred: Deferred<Response<T>>): RequestResult<T>
    suspend fun <T> coroutineApiCall(response: Response<T>): RequestResult<T>
    suspend fun <T> coroutineApiCallRaw(deferred: Deferred<T>): RequestResult<T>
}

interface MultiCoroutineCaller : CoroutineCaller {
    suspend fun <T> multiCall(vararg requests: Deferred<Response<T>>): List<RequestResult<T>>

    suspend fun <T1, T2, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        zipper: (RequestResult<T1>, RequestResult<T2>) -> R
    ): R

    suspend fun <T1, T2, T3, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>) -> R
    ): R

    suspend fun <T1, T2, T3, T4, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        request4: Deferred<Response<T4>>,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>, RequestResult<T4>) -> R
    ): R

    suspend fun <T, R> zipArray(
        vararg requests: Deferred<Response<T>>,
        zipper: (List<RequestResult<T>>) -> R
    ): R
}

class ApiCaller : MultiCoroutineCaller {

    companion object {

        /**
         * Константа только для ОТП fragment-a [OTPFragment]
         * означает то-что ОТП фрагмент не закрывается
         * при любой другой 500-ой ошибке нужно закрывать ОТП фрагмент
         */
        val HTTP_CODE_CLOSE_OTP = 2

        /**
         * [SMS_VALID_TIME_EXPIRED] и [WRONG_OTP]
         * действуют только на отп fragment-е
         * приходят от сервера при 500-ой ошибке в поле message
         * см [HTTP_CODE_CLOSE_OTP] этот код ошибки
         */
        val SMS_VALID_TIME_EXPIRED = "error.sms_valid_time_expired"
        val WRONG_OTP = "sms.wrong_otp"
    }

    val HTTP_CODE_BAD_REQUEST = 400
    val HTTP_CODE_TOKEN_EXPIRED = 420
    val HTTP_CODE_ACCOUNT_BLOCKED = 419
    val HTTP_CODE_UNTRUSTED_DEVICE = 417
    val HTTP_CODE_REFUSE_SMS_FOR_TRUSTED_DEVICE = 406

    /**
     * Обработчик запросов на `kotlin coroutines`
     * ждет выполнения запроса [deferred]
     * обрабатывает ошибки сервера
     * обрабатывает ошибки соединения
     * возвращает [RequestResult.Success] или [RequestResult.Error]
     */
    override suspend fun <T> coroutineApiCall(deferred: Deferred<Response<T>>): RequestResult<T> = try {
        handleResult(deferred.await())
    } catch (e: Exception) {
        handleException(e)
    }

    override suspend fun <T> coroutineApiCall(response: Response<T>): RequestResult<T> = try {
        handleResult(response)
    } catch (e: Exception) {
        handleException(e)
    }

    override suspend fun <T> coroutineApiCallRaw(deferred: Deferred<T>): RequestResult<T> = try {
        RequestResult.Success(deferred.await())
    } catch (e: Exception) {
        handleException(e)
    }

    /**
     * Обработчик для нескольких запросов на `kotlin coroutines`
     * запускает все [requests] и записывает их в массив [RequestResult]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     *   пока есть ограничение: можно делать только однородные запросы
     *   то есть [requests] должны возвращать либо один тип данных, либо общий интерфейс
     */
    override suspend fun <T> multiCall(vararg requests: Deferred<Response<T>>): List<RequestResult<T>> =
        requests.map {
            coroutineApiCall(it)
        }

    /**
     * Обработчик для однородных запросов на `kotlin coroutines`
     * [requests] должны возвращать один тип данных
     * запускает все [requests], записывает их в массив [RequestResult]
     * и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T, R> zipArray(
        vararg requests: Deferred<Response<T>>,
        zipper: (List<RequestResult<T>>) -> R
    ): R = zipper(requests.map { coroutineApiCall(it) })

    /**
     * Обработчик для двух разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T1, T2, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        zipper: (RequestResult<T1>, RequestResult<T2>) -> R
    ): R = zipper(coroutineApiCall(request1), coroutineApiCall(request2))

    /**
     * Обработчик для трех разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2], [request3] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T1, T2, T3, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>) -> R
    ): R = zipper(coroutineApiCall(request1), coroutineApiCall(request2), coroutineApiCall(request3))

    /**
     * Обработчик для трех разнородных запросов на `kotlin coroutines`
     * запускает [request1], [request2], [request3] и передает в обработчик [zipper]
     * обрабатывает ошибки сервера при помощи [coroutineApiCall]
     * обрабатывает ошибки соединения при помощи [coroutineApiCall]
     */
    override suspend fun <T1, T2, T3, T4, R> zip(
        request1: Deferred<Response<T1>>,
        request2: Deferred<Response<T2>>,
        request3: Deferred<Response<T3>>,
        request4: Deferred<Response<T4>>,
        zipper: (RequestResult<T1>, RequestResult<T2>, RequestResult<T3>, RequestResult<T4>) -> R
    ): R = zipper(
        coroutineApiCall(request1),
        coroutineApiCall(request2),
        coroutineApiCall(request3),
        coroutineApiCall(request4)
    )

    private fun <T> handleResult(result: Response<T>): RequestResult<T> {
        return if (result.isSuccessful) {
            RequestResult.Success(result.body())
        } else {
            throw HttpException(result)
        }
    }

    private fun <T> handleException(e: Exception): RequestResult<T>  {
        Log.d("test23Ex", e.toString())
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    HTTP_CODE_BAD_REQUEST -> {
                        RequestResult.Error("")
                    }
                    HTTP_CODE_UNTRUSTED_DEVICE -> {
                        RequestResult.Error("")
                    }
                    HTTP_CODE_REFUSE_SMS_FOR_TRUSTED_DEVICE -> {
                        RequestResult.Error("")
                    }
                    HTTP_UNAUTHORIZED -> {
                        RequestResult.Error(e.code().toString())
                    }
                    HTTP_CODE_TOKEN_EXPIRED -> {
                        RequestResult.Error("")
                    }
                    HTTP_CODE_ACCOUNT_BLOCKED -> {
                        RequestResult.Error("")
                    }
                    HTTP_INTERNAL_ERROR -> {
                        RequestResult.Error("")
                    }
                    else -> {
                        RequestResult.Error("")
                    }
                }
            }
            is SocketTimeoutException -> {
                RequestResult.Error("")
            }
            is SSLHandshakeException -> {
                RequestResult.Error("")
            }
            is JsonParseException -> {
                RequestResult.Error("")
            }
            is UnknownHostException,
            is ConnectException -> {
                RequestResult.Error("")
            }
            is SSLException -> {
                RequestResult.Error("")
            }
            is SSLException -> {
                RequestResult.Error("")
            }
            is SSLException -> {
                RequestResult.Error("")
            }
            else -> {
                RequestResult.Error("")
            }
        }
    }
}

/**
 * Презентация ответов сервера для `Presentation layer`
 * должно возвращаться репозиториями, использующими [CoroutineCaller], [RxSingleCaller] или [CoroutineRxCaller]
 * //todo implement [ResourceString] для [Error]
 */
sealed class RequestResult<out T : Any?> {
    data class Success<out T : Any?>(val result: T? = null) : RequestResult<T>()
    data class Error(val error: String, val code: Int = 0) : RequestResult<Nothing>()
}

/**
 * Модель ошибок сервера
 * //todo оставить только нужные поля
 */
@Keep
data class ErrorResponse(
    val timestamp: String?,
    val status: Int?,
    val error: String?,
    val exception: String?,
    val message: String?,
    val description: String?,
    val path: String?,
    val authToken: String?,
    val fio: String?,
    val lastLogin: String?,
    val companies: String?,
    val code: String?,
    val value: String?,
    val privileges: String?,
    val translationKey: String?,
    val error_description: String?
) {

    fun print(default: String): String {
        return message ?: error_description ?: description ?: value ?: translationKey ?: default
    }

    fun printOtpMessage(default: String): String {
        return description ?: message ?: error_description ?: value ?: translationKey ?: default
    }

    companion object {
        /**
         * Парсинг ответа сервера вручную в объект [ErrorResponse]
         *
         * Этот метод должен оставаться приватным
         * (ничего страшного, что каждый раз создаётся новый объект)
         */
        @Throws(JsonSyntaxException::class)
        private fun from(response: String): ErrorResponse {
            return Gson().fromJson(response, ErrorResponse::class.java)
        }

        fun print(response: String, default: String) = try {
            from(response).print(default)
        } catch (e: Exception) {
            default
        }


        fun printOtpMessage(response: String, default: String) = try {
            from(response).printOtpMessage(default)
        } catch (e: Exception) {
            default
        }

        /**
         * Проверка условия для ответа сервера
         *
         * например, мы хотим выяснить, есть ли в ответе поле [error_description]
         * и равно ли оно "User locked"
         */
        fun checkCondition(response: String, condition: ErrorResponse.() -> Boolean) = try {
            from(response).condition()
        } catch (e: Exception) {
            false
        }
    }
}
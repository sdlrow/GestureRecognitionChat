package com.example.gesturerecognitionwebchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gesturerecognitionwebchat.utils.CoroutineProvider
import com.example.gesturerecognitionwebchat.utils.EventWrapper
import com.example.gesturerecognitionwebchat.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Интерфейс для взаимодействия с UI:
 * IO-запросы [makeRequest] (со вспомогательной функцией [unwrap])
 * выставление статуса [set]
 * отправление сообщений об ошибках [setError]
 *
 * Предназначен для того, чтобы интеракторы могли взаимодействовать с View в MVVM
 *
 * Не трогать без консультации с Ильдаром!
 */
interface UiCaller {
    val statusLiveData: LiveData<Status>
    val errorLiveData: LiveData<EventWrapper<String>>

    fun <T> makeRequest(
        call: suspend CoroutineScope.() -> T,
        resultBlock: ((T) -> Unit)? = null
    )

    fun <T> unwrap(
        result: RequestResult<T>,
        errorBlock: ((String) -> Unit)? = { setError(it) },
        successBlock: (T) -> Unit
    ): Unit?

    fun <T> unwrapWithErrorCode(
        result: RequestResult<T>,
        errorBlock: ((RequestResult.Error) -> Unit)? = { setError(it.error) },
        successBlock: (T) -> Unit
    ): Unit?

    fun set(status: Status)

    fun setError(error: String?)
}

class UiCallerImpl(
    private val scope: CoroutineScope,
    private val scopeProvider: CoroutineProvider,
    private val _statusLiveData: MutableLiveData<Status>,
    private val _errorLiveData: MutableLiveData<EventWrapper<String>>
) : UiCaller {

    override val statusLiveData: MutableLiveData<Status> = _statusLiveData
    override val errorLiveData: MutableLiveData<EventWrapper<String>> = _errorLiveData
    /**
     * Presentation-layer-обработчик для запросов через `kotlin coroutines`:
     * запускает [Job] в [scope],
     * вызывает прогресс на [_statusLiveData]
     *
     * [call] - `suspend`-функция запроса из репозитория
     * [resultBlock] - функция, которую нужно выполнить по завершении запроса в UI-потоке
     */
    override fun <T> makeRequest(
        call: suspend CoroutineScope.() -> T,
        resultBlock: ((T) -> Unit)?
    ) {
        scope.launch(scopeProvider.Main) {
            set(Status.SHOW_LOADING)
            val result = withContext(scopeProvider.IO, call)
            result?.let { resultBlock?.invoke(it) }
            set(Status.HIDE_LOADING)
        }
    }

    /**
     * Ограничитель для нескольких запросов.
     * Пока все не отработают, [Status.HIDE_LOADING] не вызовется
     */
    private var requestCounter = 0

    override fun set(status: Status) {
        when (status) {
            Status.SHOW_LOADING -> {
                requestCounter++
            }
            Status.HIDE_LOADING -> {
                requestCounter--
                if (requestCounter > 0) return
                requestCounter = 0
            }
        }
        scope.launch(scopeProvider.Main) {
            _statusLiveData.value = status
        }
    }

    override fun setError(error: String?) {
        scope.launch(scopeProvider.Main) {
            _errorLiveData.value = EventWrapper(error ?: return@launch)
        }
    }

    /**
     * Обработчик для ответов [RequestResult] репозитория.
     * [errorBlock] - функция обработки ошибок, можно передать `null`, чтобы никак не обрабатывать.
     * [successBlock] - обработка непустого результата
     */
    override fun <T> unwrap(
        result: RequestResult<T>,
        errorBlock: ((String) -> Unit)?,
        successBlock: (T) -> Unit
    ) = when (result) {
        is RequestResult.Success -> result.result?.let { successBlock(it) }
        is RequestResult.Error -> errorBlock?.invoke(result.error)
        else -> {}
    }

    override fun <T> unwrapWithErrorCode(
        result: RequestResult<T>,
        errorBlock: ((RequestResult.Error) -> Unit)?,
        successBlock: (T) -> Unit
    ) = when (result) {
        is RequestResult.Success -> result.result?.let { successBlock(it) }
        is RequestResult.Error -> errorBlock?.invoke(result)
        else -> {}
    }
}
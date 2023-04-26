package com.example.gesturerecognitionwebchat

import android.content.Context
import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.API.OpenApi
import com.example.gesturerecognitionwebchat.Launcher.LaunchViewModel
import com.example.gesturerecognitionwebchat.Token.LocalSessionExpiredChecker
import com.example.gesturerecognitionwebchat.Token.TokenInterceptor
import com.example.gesturerecognitionwebchat.base.PrefManager
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository
import com.example.gesturerecognitionwebchat.request.ContextProvider
import com.example.gesturerecognitionwebchat.request.HeaderInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import kotlin.coroutines.CoroutineContext

val appModule = module {
    viewModel {
        LaunchViewModel(get())
        RegistrationViewModel(get(), get())
    }
}
val prefModule = module {
    single { PrefManager(androidContext()) }
    single { LocalSessionExpiredChecker() }
    single<CoroutineContext>(named("io")) { Dispatchers.IO }
    single<CoroutineContext>(named("main")) { Dispatchers.Main }
    factory { ContextProvider(androidContext()) }
    single { TokenInterceptor(get()) }
}

val repositoryModule = module {
    factory {
        RegistrationRepository(get(), get(), get())
    }
}

/**
 * [OkHttpClient] instances
 */


val apiModuleRetrofit= module {
    factory { createWebService<OpenApi>(get(named("openApi")), API_URL) }
    factory { createWebService<Api>(get(named("default")), API_URL) }
}

val apiModuleOkHttp = module {
    factory(named("default")) { createOkHttpClient(androidContext(), get()) }
    factory(named("openApi")) { createOkHttpOpenApi(androidContext()) }

}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient, url: String): T {

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create()).build()
    return retrofit.create(T::class.java)
}

fun createOkHttpClient(context: Context, tokenInterceptor: TokenInterceptor) =
    OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .hostnameVerifier(hostnameVerifier)
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(tokenInterceptor)
        .build()

fun createOkHttpOpenApi(context: Context) = OkHttpClient.Builder()
    .connectTimeout(timeout, TimeUnit.SECONDS)
    .readTimeout(timeout, TimeUnit.SECONDS)
    .hostnameVerifier(hostnameVerifier)
    .addInterceptor(HeaderInterceptor())
    .build()


val hostnameVerifier = HostnameVerifier { _, _ -> true }
const val timeout = 30L
const val stompTimeout = 300L
const val API_URL = "http://192.168.237.1:8081/api/"
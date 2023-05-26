package com.example.gesturerecognitionwebchat

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.AccountViewModel
import com.example.gesturerecognitionwebchat.API.Api
import com.example.gesturerecognitionwebchat.API.OpenApi
import com.example.gesturerecognitionwebchat.Launcher.LaunchViewModel
import com.example.gesturerecognitionwebchat.Token.LocalSessionExpiredChecker
import com.example.gesturerecognitionwebchat.Token.TokenInterceptor
import com.example.gesturerecognitionwebchat.base.PrefManager
import com.example.gesturerecognitionwebchat.repository.RegistrationRepository
import com.example.gesturerecognitionwebchat.request.ContextProvider
import com.example.gesturerecognitionwebchat.request.HeaderInterceptor
import com.example.gesturerecognitionwebchat.utils.SocketProvider
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
import kotlin.coroutines.CoroutineContext
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import okio.ByteString
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.webrtc.*
import org.webrtc.EglBase
import java.net.ServerSocket.setSocketFactory
import java.net.URI
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

val appModule = module {
    viewModel { LaunchViewModel(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { AccountViewModel(get(), get()) }
    viewModel { NewChatViewModel(androidContext()) }
}
val prefModule = module {
    single { PrefManager(androidContext()) }
    single { LocalSessionExpiredChecker() }
    single<CoroutineContext>(named("io")) { Dispatchers.IO }
    single<CoroutineContext>(named("main")) { Dispatchers.Main }
    factory { ContextProvider(androidContext()) }
    single { TokenInterceptor(get()) }
}
var rootEglBaseModule: EglBase? = null
val peerModule = module {

    single(named("rootEglBase")) {
        EglBase.create()
    }

    factory(named("peerConnectionFactory")) {
        rootEglBaseModule
        val options = PeerConnectionFactory.InitializationOptions.builder(get())
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
        rootEglBaseModule = EglBase.create()

        PeerConnectionFactory
            .builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(rootEglBaseModule?.eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    rootEglBaseModule?.eglBaseContext,
                    true,
                    true
                )
            )
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = false
                disableNetworkMonitor = true
            })
            .createPeerConnectionFactory()
    }
}

val socketModule = module {
//    single(named("peer_connection")) {
//        PeerConnectionFactory.initialize(
//            PeerConnectionFactory.InitializationOptions.builder(androidContext())
//                .setEnableInternalTracer(true)
//                .createInitializationOptions()
//        )
//        createPeerConnectionVideoFactory(androidContext())
//    }

//    factory(named("socketInstance")) {
//        val okHttpClient = get<OkHttpClient>(named("webSocketClient"))
//
//        val request = Request.Builder()
//            .url("wss://192.168.56.1/")
//            .addHeader(
//                "User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
//            )
//            .build()
//
//        val listener = object : WebSocketListener() {
//            override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
//                super.onClosed(webSocket, code, reason)
//                Log.d("WebSocketClient", "onClosed: code=$code, reason=$reason")
//            }
//
//            override fun onClosing(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
//                super.onClosing(webSocket, code, reason)
//                Log.d("WebSocketClient", "onClosing: code=$code, reason=$reason")
//            }
//
//            override fun onFailure(
//                webSocket: okhttp3.WebSocket,
//                t: Throwable,
//                response: Response?
//            ) {
//                super.onFailure(webSocket, t, response)
//                Log.e("WebSocketClient", "onFailure: error=${t.message}")
//            }
//
//            override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
//                super.onMessage(webSocket, text)
//                Log.d("WebSocketClient", "onMessage: text=$text")
//            }
//
//            override fun onMessage(webSocket: okhttp3.WebSocket, bytes: ByteString) {
//                super.onMessage(webSocket, bytes)
//                Log.d("WebSocketClient", "onMessage: bytes=$bytes")
//            }
//
//            override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//                Log.d("WebSocketClient", "onOpen")
//            }
//        }
//
//        return@factory okHttpClient.newWebSocket(request, listener)
//    }
//
//    single(named("socket")) {
//        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
//            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//
//            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
//
//            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
//        })
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(null, trustAllCerts, SecureRandom())
//        val headers = mapOf("User-Agent" to "Android")
//        val sslSocketFactory = sslContext.socketFactory
//        val serverUri = URI("wss://192.168.56.1/")
//        val options = mapOf<String, String>(
//            "handshakeTimeout" to "5000"
//        )
//        object : WebSocketClient(serverUri, options) {
//            override fun onOpen(handshakedata: ServerHandshake?) {
//                Log.d("WebSocket", "onOpen")
//                // Send message to server
//                send("Hello, Server!")
//            }
//
//            override fun onMessage(message: String?) {
//                Log.d("WebSocket", "onMessage: $message")
//            }
//
//            override fun onClose(code: Int, reason: String?, remote: Boolean) {
//                Log.d("WebSocket", "onClose: code=$code, reason=$reason, remote=$remote")
//            }
//
//            override fun onError(ex: Exception?) {
//                Log.e("WebSocket", "onError", ex)
//            }
//
//
//        }.apply {
//            setSocketFactory(sslSocketFactory)
//            addHeader(
//                "User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36"
//            )
//        }
//    }

    factory(named("socketInstance")) {
        val okHttpClient: OkHttpClient = get(named("webSocketClient"))
        val options = IO.Options().apply {
            path = SOCKET_IO
            transports = arrayOf(WEB_SOCKET)
            callFactory = okHttpClient
            webSocketFactory = okHttpClient
        }
        IO.socket(SOCKET_URL, options)
    }
}


val repositoryModule = module {
    factory {
        RegistrationRepository(get(), get(), get())
    }
    factory {
        ChatRepository(get())
    }
    factory {
        AccountRepository(get())
    }
    factory {
        NewChatRepository(get())
    }
}


val apiModuleRetrofit= module {
    factory { createWebService<OpenApi>(get(named("openApi")), API_URL) }
    factory { createWebService<Api>(get(named("default")), API_URL) }
}

val apiModuleOkHttp = module {
    factory(named("default")) { createOkHttpClient(androidContext(), get()) }
    factory(named("openApi")) { createOkHttpOpenApi(androidContext()) }
    factory(named("webSocketClient")) { createOkHttpSocket(androidContext()) }
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
        .addInterceptor(ChuckerInterceptor(context))
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(tokenInterceptor)
        .build()

fun createPeerConnectionVideoFactory(context: Context): PeerConnectionFactory {
    val rootEglBase = EglBase.create()
    val videoEncoderFactory = DefaultVideoEncoderFactory(
        rootEglBase.eglBaseContext,
        /* enableIntelVp8Encoder= */ true,
        /* enableH264HighProfile= */ false
    )
    val videoDecoderFactory = DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)
    return PeerConnectionFactory.builder()
        .setVideoEncoderFactory(videoEncoderFactory)
        .setVideoDecoderFactory(videoDecoderFactory)
        .createPeerConnectionFactory()
}



fun createOkHttpOpenApi(context: Context) = OkHttpClient.Builder()
    .connectTimeout(timeout, TimeUnit.SECONDS)
    .readTimeout(timeout, TimeUnit.SECONDS)
    .hostnameVerifier(hostnameVerifier)
    .addInterceptor(ChuckerInterceptor(context))
    .addInterceptor(HeaderInterceptor())
    .build()


fun createOkHttpSocket(context: Context): OkHttpClient {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}

        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    })
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    val sslSocketFactory = sslContext.socketFactory
    return OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .hostnameVerifier(hostnameVerifier)
        .addInterceptor(ChuckerInterceptor(context))
        .addInterceptor(HeaderInterceptor())
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .build()
}


val hostnameVerifier = HostnameVerifier { _, _ -> true }
const val timeout = 30L
const val stompTimeout = 300L
const val API_URL = "http://192.168.1.157:8081/api/"
const val SOCKET_URL = "https://192.168.1.157/"
const val SOCKET_IO = "/socket.io/"
const val WEB_SOCKET = "websocket"
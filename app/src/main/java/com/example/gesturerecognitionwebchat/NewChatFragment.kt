package com.example.gesturerecognitionwebchat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.gesturerecognitionwebchat.context.alertWithActions
import com.example.gesturerecognitionwebchat.databinding.FragmentNewChatBinding
import kotlinx.android.synthetic.main.fragment_new_chat.*
import io.socket.client.IO;
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NewChatFragment : Fragment() {
    private var _binding: FragmentNewChatBinding? = null
    private lateinit var socket: Socket

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val webSocket: WebSocket by inject(named("socketInstance"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickerInitializer()
        localizationInitializer()
//        webSocket.send("Hello, Server!")
        val okHttpClient = createOkHttpClient()

        val options = IO.Options().apply {
            path = "/socket.io/"
            transports = arrayOf("websocket")
            callFactory = okHttpClient
            webSocketFactory = okHttpClient
        }
        socket = IO.socket("https://192.168.56.1/", options)

        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.connect()
        socket.emit("message", "GET / HTTP/1.1 200 2193 - Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 -");
    }

    private val onConnect = Emitter.Listener {
        Log.d("MyFragment", "Socket connected")
    }

    private val onDisconnect = Emitter.Listener {
        Log.d("MyFragment", "Socket disconnected")
    }

    private val onConnectError = Emitter.Listener { args ->
        Log.e("MyFragment", "Socket connect error: ${args.contentToString()}")
    }

    private val onConnectTimeout = Emitter.Listener {
        Log.e("MyFragment", "Socket connect timeout")
    }

    private fun clickerInitializer() {
        confirmButton.setOnClickListener {
            requireActivity().alertWithActions(
                message = "Разрешить приложению видеочат доступ к данным о местоположении устройства и камере?",
                positiveButtonCallback = { Log.d("TestButt", "ДОСТУП К ЖОПЕ ПОЛУЧЕН") },
                negativeButtonCallback = { Log.d("TestButt", "ДОСТУП К ЖОПЕ ПОКА ЧТО НЕ ПОЛУЧЕН") },
                positiveText = "При использовании", negativeText = "Отклонить"

            )
        }
    }

    private fun localizationInitializer() {
        (confirmButton as Button).text = "Дать разрешение"
    }

    override fun onDestroyView() {
        socket.disconnect()
        super.onDestroyView()
        _binding = null
    }

    private fun createOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}
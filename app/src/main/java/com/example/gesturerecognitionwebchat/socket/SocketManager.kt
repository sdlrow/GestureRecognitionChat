package com.example.gesturerecognitionwebchat.socket

import okhttp3.*
import okio.ByteString

class SocketManager(private val url: String) {
    private lateinit var webSocket: WebSocket

    fun connect() {
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                // WebSocket connection opened
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // Received text message from WebSocket
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                // Received binary message from WebSocket
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                // WebSocket connection closed
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // WebSocket connection failure
            }
        })
    }

    fun sendText(text: String) {
        webSocket.send(text)
    }

    fun sendBytes(bytes: ByteString) {
        webSocket.send(bytes)
    }

    fun disconnect() {
        webSocket.close(1000, null)
    }
}
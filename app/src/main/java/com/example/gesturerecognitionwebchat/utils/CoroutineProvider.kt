package com.example.gesturerecognitionwebchat.utils

import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import kotlin.coroutines.CoroutineContext


class CoroutineProvider : KoinComponent {
    val Main: CoroutineContext by inject(named("main"))
    val IO: CoroutineContext by inject(named("io"))
}

class SocketProvider : KoinComponent {
    val socketInject: Socket by inject(named("socket"))
    val peerConnectionFactoryInject: PeerConnectionFactory by inject(named("peer_connection"))

    fun getSocket(): Socket {
        return socketInject
    }

    fun getPeerConnectionFactory(): PeerConnectionFactory {
        return peerConnectionFactoryInject
    }
}
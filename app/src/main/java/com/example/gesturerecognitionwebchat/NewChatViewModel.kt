package com.example.gesturerecognitionwebchat

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.gesturerecognitionwebchat.NewChatFragment.Companion.LOCAL_OFFER
import com.example.gesturerecognitionwebchat.base.BaseViewModel
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.webrtc.*
import java.util.*


class NewChatViewModel(private val context: Context) : BaseViewModel() {
    val localViewInitialized = MutableLiveData<Boolean>()
    val localViewInitializedBefore = MutableLiveData<Boolean>()
    var localView: SurfaceViewRenderer? = null
    var remoteView: SurfaceViewRenderer? = null
    private var audioManager: AudioManager? = null
    private val socket: Socket by inject(named("socketInstance"))
    private val peerConnectionFactory: PeerConnectionFactory by inject(named("peerConnectionFactory"))
    val rootEglBase: EglBase by inject(named("rootEglBase"))
    private val roomNumber: String = "1234"
    private var callerId: String? = null
    private var isCaller: Boolean = true
    private var remoteDescription: Boolean = false
    private val audioConstraints: MediaConstraints = MediaConstraints()
    private val videoSource: VideoSource = peerConnectionFactory.createVideoSource(false)
    private val audioSource: AudioSource = peerConnectionFactory.createAudioSource(audioConstraints)
    lateinit var videoCapturer: CameraVideoCapturer
    private lateinit var rtcPeerConnection: PeerConnection

    val rtcConstraints = MediaConstraints()
    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:${NewChatFragment.PEER_LOCAL}:3478")
            .createIceServer(),
        PeerConnection.IceServer.builder("turn:${NewChatFragment.PEER_LOCAL}:3478")
            .setUsername("username")
            .setPassword("password")
            .createIceServer()
    )
    val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

    fun startSocket() {
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket.on("created", onCreated)
        socket.on("ready", onReady)
        socket.on("joined", onJoined)
        socket.on("setCaller", onCaller)
        socket.on("offer", onOffer)
        socket.on("candidate", onCandidate)
        socket.on("full", onRoomFull)
        socket.on("answer", onAnswer)
        socket.on("userDisconnected", onDisconnectClient)
        socket.connect()
    }

    fun disconnectSocket() {
        socket.disconnect()
    }

    private val onConnect = Emitter.Listener {
        Log.d("MyFragment", "Socket connected")
        socket.emit("joinRoom", roomNumber)
        val cameraEnumerator = Camera2Enumerator(context)
        val deviceNames = cameraEnumerator.deviceNames
        val cameraDeviceName = deviceNames.find { cameraEnumerator.isFrontFacing(it) }
        videoCapturer = cameraEnumerator.createCapturer(cameraDeviceName, null)
//        audioConstraints = MediaConstraints()
//        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
//        videoSource = peerConnectionFactory.createVideoSource(false)
        val surfaceTextureHelper =
            SurfaceTextureHelper.create(Thread.currentThread().name, rootEglBase.eglBaseContext)
        (videoCapturer as VideoCapturer).initialize(
            surfaceTextureHelper,
            context,
            videoSource.capturerObserver
        )
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            audioManager = context.getSystemService(AudioManager::class.java)
            localViewInitializedBefore.value = true
//            localView?.init(rootEglBase.eglBaseContext, null)
//            localView?.setEnableHardwareScaler(true);
//            remoteView?.init(rootEglBase.eglBaseContext, null);
        }
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

    private val onCreated = Emitter.Listener { data ->
        Log.e("created", "Received data: ${data.joinToString()}")
        isCaller = true
    }

    private fun settingRTCConfig() {
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        rtcConfig.enableDtlsSrtp = true
        Log.d("RTCConfiguration", rtcConfig.toString())
    }

    private val onReady = Emitter.Listener { data ->
        Log.e("ready", "Received data: ${data.joinToString()}")
        settingRTCConfig()
        rtcPeerConnection =
            peerConnectionFactory.createPeerConnection(rtcConfig, rtcConstraints, observerPeer)!!
        if (isCaller) {
            val localVideoTrack = peerConnectionFactory.createVideoTrack(
                "a576b87a-f4d3-4abf-947f-12644ce9ccb0",
                videoSource
            )
            val localAudioTrack = peerConnectionFactory.createAudioTrack(
                "bba6f93f-adc6-4f02-b66b-096db15e4c70",
                audioSource
            )
            localAudioTrack.setEnabled(true)
            localVideoTrack.addSink(localView)
            val stream =
                peerConnectionFactory.createLocalMediaStream(NewChatFragment.LOCAL_MEDIA_STREAM_LABEL)
            stream.addTrack(localVideoTrack)
            stream.addTrack(localAudioTrack)
            rtcPeerConnection.addTrack(stream.videoTracks[0], Collections.singletonList(stream.id))
            rtcPeerConnection.addTrack(stream.audioTracks[0], Collections.singletonList(stream.id))
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                localViewInitialized.value = true
            }
            videoCapturer.startCapture(680, 480, 30)
            val sdpMediaConstraints = MediaConstraints()

            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "true"
                )
            )
            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio",
                    "true"
                )
            )
//            rtcPeerConnection.addTrack(localAudioTrack, listOf("streamId"))
//            rtcPeerConnection.addTrack(localVideoTrack, listOf("streamId"))
            rtcPeerConnection.createOffer(object : SdpObserver {
                override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                    Log.d("Test23", "Test23")
                    val sdpJson = JSONObject().apply {
                        put("type", "offer")
                        put("sdp", sessionDescription?.description)
                    }
                    val offerObject = JSONObject().apply {
                        put("type", "offer")
                        put("sdp", sdpJson)
                        put("room", roomNumber)
                    }
                    socket.emit("offer", offerObject)
                    rtcPeerConnection.setLocalDescription(object : SdpObserver {
                        override fun onCreateSuccess(p0: SessionDescription?) {
                            Log.e("setLocalDescription", "onCreateSuccess: $p0")
                        }

                        override fun onSetSuccess() {
                            Log.d("setLocalDescription", "onSetSuccess")
                        }

                        override fun onCreateFailure(p0: String?) {
                            Log.e("setLocalDescription", "onCreateFailure: $p0")
                        }

                        override fun onSetFailure(p0: String?) {
                            Log.e("setLocalDescription", "onSetFailure: $p0")
                        }
                    }, sessionDescription)
                }

                override fun onSetSuccess() {
                    Log.d("onSetSuccess", "Session description successfully set")
                }

                override fun onSetFailure(reason: String?) {
                    Log.e("onSetFailure", "Failed to set session description. Reason: $reason")
                }

                override fun onCreateFailure(reason: String?) {
                    Log.e(
                        "onCreateFailure",
                        "Failed to create session description. Reason: $reason"
                    )
                }
            }, MediaConstraints())
        }
    }

    val observerPeer = object : PeerConnection.Observer {
        override fun onSignalingChange(state: PeerConnection.SignalingState) {
            Log.d("onSignalingChange", "Signaling state changed: $state")
        }

        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
            Log.d("onIceConnectionChange", "ICE connection state changed: $state")
            if (state == PeerConnection.IceConnectionState.CONNECTED || state == PeerConnection.IceConnectionState.COMPLETED) {
                Log.e("established", "E")
            } else {
                Log.e("not established", "N")
            }
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {
            Log.d("onIceConnectionReceivingChange", "ICE connection receiving changed: $receiving")
        }

        override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {
            Log.d("onIceGatheringChange", "ICE gathering state changed: $state")
        }

        override fun onIceCandidate(candidate: IceCandidate) {
            Log.d("onIceCandidate", candidate.toString())
            if (candidate != null) {
                Log.d("onIceCandidate", "Sending ice candidate")
                socket.emit("candidate", JSONObject().apply {
                    put("type", "candidate")
                    put("label", candidate.sdpMLineIndex)
                    put("id", candidate.sdpMid)
                    put("candidate", candidate.sdp)
                    put("room", roomNumber)
                })
            }
        }

        override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
            Log.d("onIceCandidatesRemoved", "ICE candidates removed")
        }

        override fun onAddStream(stream: MediaStream) {
            Log.d("onAddStream", "Media stream added: ${stream.id}")
            addRemoteStreamToVideoView(stream);
        }

        override fun onRemoveStream(stream: MediaStream) {
            Log.d("onRemoveStream", "Media stream removed: ${stream.id}")
        }

        override fun onDataChannel(channel: DataChannel) {
            Log.d("onDataChannel", "Data channel created: ${channel.label()}")
        }

        override fun onRenegotiationNeeded() {
            Log.d("onRenegotiationNeeded", "Renegotiation needed")
        }

        override fun onAddTrack(receiver: RtpReceiver, streams: Array<MediaStream>) {
            Log.d("onAddTrack", "Track added: ${receiver.track()?.kind()}")
        }
    }

    private val onJoined = Emitter.Listener { data ->
        Log.e("joined", "Received data: ${data.joinToString()}")
        isCaller = false
        socket.emit("ready", roomNumber)
    }

    private val onDisconnectClient  = Emitter.Listener { data ->
        Log.e("ClientDisconnected", "Dissconnect data: ${data.joinToString()}")
    }

    private val onAnswer = Emitter.Listener { data ->
        Log.e("onAnswer", rtcPeerConnection.signalingState().name)
        Log.e("OnAnswerData", "Received data: ${data.joinToString()}")
        if (isCaller && rtcPeerConnection.signalingState().name == LOCAL_OFFER) {
            val payload = data[0] as JSONObject
            Log.e("OnAnswerDataR", "Received data: $payload")
            val sdp = payload?.getString("sdp")
            Log.e("OnAnswerDataS", "Received data: ${sdp}")
            val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, sdp)
            rtcPeerConnection.setRemoteDescription(object : SdpObserver {
                override fun onSetSuccess() {
                    remoteDescription = true
                    Log.d("setRemoteDescription", "Remote description set successfully")
                }

                override fun onSetFailure(error: String?) {
                    Log.e("setRemoteDescription", "Failed to set remote description: $error")
                }

                override fun onCreateSuccess(sdp: SessionDescription?) {
                    Log.d("setRemoteDescription", "onCreateSuccess $sdp")

                }

                override fun onCreateFailure(error: String?) {
                    Log.e("setRemoteDescription", "onCreateFailure: $error")

                }
            }, sessionDescription)
        }
    }

    private val onOffer = Emitter.Listener { data ->
        Log.e("offer", "Received data: ${data.joinToString()}")
        if (!isCaller) {
            settingRTCConfig()
            rtcPeerConnection =
                peerConnectionFactory.createPeerConnection(
                    rtcConfig,
                    rtcConstraints,
                    observerPeer
                )!!
            val localVideoTrack = peerConnectionFactory.createVideoTrack(
                "a576b87a-f4d3-4abf-947f-12644ce9ccb0",
                videoSource
            )
            val localAudioTrack = peerConnectionFactory.createAudioTrack(
                "bba6f93f-adc6-4f02-b66b-096db15e4c70",
                audioSource
            )
            localAudioTrack.setEnabled(true)
            localVideoTrack.addSink(localView)
            val stream =
                peerConnectionFactory.createLocalMediaStream(NewChatFragment.LOCAL_MEDIA_STREAM_LABEL)
            stream.addTrack(localVideoTrack)
            stream.addTrack(localAudioTrack)
            rtcPeerConnection.addTrack(stream.videoTracks[0], Collections.singletonList(stream.id))
            rtcPeerConnection.addTrack(stream.audioTracks[0], Collections.singletonList(stream.id))
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                localViewInitialized.value = true
            }
            videoCapturer.startCapture(680, 480, 30)
            val sdpMediaConstraints = MediaConstraints()

            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveVideo",
                    "true"
                )
            )
            sdpMediaConstraints.mandatory.add(
                MediaConstraints.KeyValuePair(
                    "OfferToReceiveAudio",
                    "true"
                )
            )
            val payload = data[0] as JSONObject
            Log.e("OnOfferDataR", "Received data: $payload")
            val sdp = payload?.getString("sdp")
            Log.e("OnOfferDataS", "Received data: ${sdp}")
            val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, sdp)
            rtcPeerConnection.setRemoteDescription(object : SdpObserver {
                override fun onSetSuccess() {
                    remoteDescription = true
                    rtcPeerConnection.createAnswer(object : SdpObserver {
                        override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                            Log.d("Test23", "Test23")
                            val sdpJson = JSONObject().apply {
                                put("type", "answer")
                                put("sdp", sessionDescription?.description)
                            }
                            val offerObject = JSONObject().apply {
                                put("type", "answer")
                                put("sdp", sdpJson)
                                put("room", roomNumber)
                            }
                            socket.emit("answer", offerObject)
                            rtcPeerConnection.setLocalDescription(object : SdpObserver {
                                override fun onCreateSuccess(p0: SessionDescription?) {
                                    Log.e("setLocalDescription", "onCreateSuccess: $p0")
                                }

                                override fun onSetSuccess() {
                                    Log.d("setLocalDescription", "onSetSuccess")
                                }

                                override fun onCreateFailure(p0: String?) {
                                    Log.e("setLocalDescription", "onCreateFailure: $p0")
                                }

                                override fun onSetFailure(p0: String?) {
                                    Log.e("setLocalDescription", "onSetFailure: $p0")
                                }
                            }, sessionDescription)
                        }

                        override fun onSetSuccess() {
                            Log.d("onSetSuccess", "Session description successfully set")
                        }

                        override fun onSetFailure(reason: String?) {
                            Log.e(
                                "onSetFailure",
                                "Failed to set session description. Reason: $reason"
                            )
                        }

                        override fun onCreateFailure(reason: String?) {
                            Log.e(
                                "onCreateFailure",
                                "Failed to create session description. Reason: $reason"
                            )
                        }
                    }, MediaConstraints())
                    Log.d("setRemoteDescription", "Remote description set successfully")
                }

                override fun onSetFailure(error: String?) {
                    Log.e("setRemoteDescription", "Failed to set remote description: $error")
                }

                override fun onCreateSuccess(sdp: SessionDescription?) {
                    Log.d("setRemoteDescription", "onCreateSuccess $sdp")

                }

                override fun onCreateFailure(error: String?) {
                    Log.e("setRemoteDescription", "onCreateFailure: $error")

                }
            }, sessionDescription)
        }
    }

    private fun addRemoteStreamToVideoView(stream: MediaStream) {
        val remoteVideoTrack =
            if (stream.videoTracks != null && stream.videoTracks.size > 0) stream.videoTracks[0] else null

        val remoteAudioTrack =
            if (stream.audioTracks != null && stream.audioTracks.size > 0) stream.audioTracks[0] else null

        if (remoteAudioTrack != null) {
            remoteAudioTrack.setEnabled(true)
            Log.d("Remote", "remoteAudioTrack received: State=" + remoteAudioTrack.state().name)
            audioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
            audioManager?.isSpeakerphoneOn = true
        }

        if (remoteVideoTrack != null) {
            remoteVideoTrack.addSink(remoteView)
        } else {
            Log.e("Remote", "Error in setting remote track")
        }
    }

    private val onCaller = Emitter.Listener { data ->
        Log.e("caller", "Received data: ${data.joinToString()}")
        callerId = data.joinToString()
    }

    private val onRoomFull = Emitter.Listener { data ->
        Log.e("full", "Received data: ${data.joinToString()}")
    }

    private val onCandidate = Emitter.Listener { data ->
        Log.e("candidate", "Received data: ${data.joinToString()}")
        val candidateData = data[0] as JSONObject
        val candidate = IceCandidate(
            candidateData.getString("id"),
            candidateData.getInt("label"),
            candidateData.getString("candidate")
        )
        if (remoteDescription) {
            if (candidate != null) {
                rtcPeerConnection.addIceCandidate(candidate)
            }
        }
    }
}
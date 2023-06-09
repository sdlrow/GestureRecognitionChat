package com.example.gesturerecognitionwebchat

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.gesturerecognitionwebchat.base.BaseFragment
import com.example.gesturerecognitionwebchat.context.alertWithActions
import com.example.gesturerecognitionwebchat.context.showUpperToastError
import com.example.gesturerecognitionwebchat.databinding.FragmentNewChatBinding
import kotlinx.android.synthetic.main.app_main.*
import kotlinx.android.synthetic.main.fragment_new_chat.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class NewChatFragment : BaseFragment() {
    private var _binding: FragmentNewChatBinding? = null
    private lateinit var viewModel: NewChatViewModel
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

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
        observerInitializer()
        localizationInitializer()
        if (checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askAudioAndVideoPermissions()
        } else {
            binding.contentNoPermissions.visibility = View.GONE
            binding.startSocketButton.visibility = View.VISIBLE
        }
    }

    private fun startSocketConnection() {
        binding.startSocketButton.visibility = View.GONE
        binding.contentLoadingSocket.visibility = View.VISIBLE
        loadLocalData()
    }

    private fun loadLocalData() {
        viewModel.startSocket()
    }

    private fun observerInitializer() {
        viewModel.localViewInitialized.observe(viewLifecycleOwner, Observer { initialized ->
            if (initialized) {
                binding.chatView.visibility = View.VISIBLE
            }
        })
        viewModel.localViewInitializedBefore.observe(viewLifecycleOwner, Observer { initialized ->
            if (initialized) {
                if (viewModel.localView == null) {
                    viewModel.localView = binding.localView
                    binding.localView.init(viewModel.rootEglBase?.eglBaseContext, null)
                    binding.localView.setEnableHardwareScaler(true)
                    viewModel.remoteView = binding.remoteView
                    binding.remoteView.init(viewModel.rootEglBase?.eglBaseContext, null)
                }
                viewModel.joinRoom()
            }
        })

    }

    private fun clickerInitializer() {
        confirmButton.setOnClickListener {
//            requireActivity().alertWithActions(
//                message = "Разрешить приложению видеочат доступ к видео и аудио?",
//                positiveButtonCallback = {  askAudioAndVideoPermissions() },
//                negativeButtonCallback = { Log.d("TestButt", "ДОСТУП К ЖОПЕ ПОКА ЧТО НЕ ПОЛУЧЕН") },
//                positiveText = "Разрешить", negativeText = "Отклонить"
//            )
            askAudioAndVideoPermissions()
        }
        socketButton.setOnClickListener {
            requireActivity().toolbar.visibility = View.GONE
            viewModel = getViewModel()
            viewModel.rootEglBase = rootEglBaseModule
            startSocketConnection()
//            binding.contentLoadingSocket.visibility = View.GONE
        }
        imageBack.setOnClickListener {
            viewModel.videoCapturer.stopCapture()
            viewModel.isCaller = null
//            binding.localView.release()
//            binding.remoteView.release()
            viewModel.disconnectSocket()
            requireActivity().toolbar.visibility = View.VISIBLE
            binding.contentLoadingSocket.visibility = View.GONE
            binding.chatView.visibility = View.GONE
            binding.startSocketButton.visibility = View.VISIBLE
        }
    }

    private fun askAudioAndVideoPermissions() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(
                android.Manifest.permission.RECORD_AUDIO
            )
        ) {
            requireActivity().alertWithActions(
                message = "Разрешить приложению видеочат доступ к видео и аудио?",
                positiveButtonCallback = {
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.RECORD_AUDIO
                        ), PERMISSIONS_REQUEST
                    )
                },
                negativeButtonCallback = { Log.d("TestButt", "ДОСТУП К ЖОПЕ ПОКА ЧТО НЕ ПОЛУЧЕН") },
                positiveText = "Разрешить", negativeText = "Отклонить"
            )
        }
        requestPermissions(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            ), PERMISSIONS_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            Log.d("Perm", grantResults[0].toString())
            Log.d("Perm", grantResults[1].toString())
            if (grantResults.isNotEmpty() && grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
                binding.contentNoPermissions.visibility = View.GONE
                binding.startSocketButton.visibility = View.VISIBLE
            } else {
                requireActivity().showUpperToastError("Ошибка разрешений")
            }
        }
    }

    private fun localizationInitializer() {
        (confirmButton as Button).text = "Дать разрешение"
        (socketButton as Button).text = "Подключиться"
    }

    override fun onDestroyView() {
        if( viewModel.checkCameraInitializer()){
            viewModel.videoCapturer.stopCapture()
        }
        viewModel.isCaller = null
        viewModel.disconnectSocket()
        binding.contentLoadingSocket.visibility = View.GONE
        binding.chatView.visibility = View.GONE
        binding.startSocketButton.visibility = View.VISIBLE
        super.onDestroyView()
    }

    companion object {
        const val LOCAL_OFFER = "HAVE_LOCAL_OFFER"
        const val LOCAL_MEDIA_STREAM_LABEL = "Z4uJnUYM9E0aTMSylreEifBpthbaPRoUcn0F"
        const val PEER_LOCAL = "192.168.1.157"
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val PERMISSIONS_REQUEST = 1
    }
}
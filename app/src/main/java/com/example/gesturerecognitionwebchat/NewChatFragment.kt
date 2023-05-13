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
            binding.contentLoadingSocket.visibility = View.VISIBLE
            loadLocalData()
        }
    }

    private fun loadLocalData(){
        viewModel.localView = binding.localView
        viewModel.remoteView = binding.remoteView
        viewModel.startSocket()
    }

    private fun observerInitializer(){
        viewModel.localViewInitialized.observe(viewLifecycleOwner, Observer { initialized ->
            if (initialized) {
                binding.chatView.visibility = View.VISIBLE
            }
        })
        viewModel.localViewInitializedBefore.observe(viewLifecycleOwner, Observer { initialized ->
            if (initialized) {
                binding.localView.release()
                binding.remoteView.release()
                binding.localView?.init(viewModel.rootEglBase.eglBaseContext, null)
                binding.localView?.setEnableHardwareScaler(true);
                binding.remoteView?.init(viewModel.rootEglBase.eglBaseContext, null);
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            Log.d("Perm", grantResults[0].toString())
            Log.d("Perm", grantResults[1].toString())
            if (grantResults.isNotEmpty()  && grantResults.none { it == PackageManager.PERMISSION_DENIED }) {
                binding.contentNoPermissions.visibility = View.GONE
                binding.contentLoadingSocket.visibility = View.VISIBLE
                loadLocalData()
            } else {
                requireActivity().showUpperToastError("Ошибка разрешений")
            }
        }
    }

    private fun localizationInitializer() {
        (confirmButton as Button).text = "Дать разрешение"
    }

    override fun onDestroyView() {
        viewModel.disconnectSocket()
        super.onDestroyView()
        binding.localView.release()
        binding.remoteView.release()
        _binding = null
    }

    companion object {
        const val LOCAL_OFFER = "HAVE_LOCAL_OFFER"
        const val LOCAL_MEDIA_STREAM_LABEL = "Z4uJnUYM9E0aTMSylreEifBpthbaPRoUcn0F"
        const val PEER_LOCAL = "192.168.1.156"
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val PERMISSIONS_REQUEST = 1
    }
}
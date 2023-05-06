package com.example.gesturerecognitionwebchat.Launcher

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gesturerecognitionwebchat.RegisterActivity
import com.example.gesturerecognitionwebchat.databinding.FragmentLauncherLayoutBinding

class LauncherFragment : Fragment() {

    private var _binding: FragmentLauncherLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LaunchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLauncherLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (viewModel.isLoginAndPasswordExist()) {
//
//        }
//        else{
        val intent = Intent(activity, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivityForResult(intent, 0)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
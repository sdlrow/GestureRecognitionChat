package com.example.gesturerecognitionwebchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.gesturerecognitionwebchat.base.BaseFragment
import com.example.gesturerecognitionwebchat.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var viewModel: RegistrationViewModel
    private var clickableButton = true
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        viewModel.statusLiveData.observe(this, statusObserver)
        viewModel.errorLiveData.observe(this, errorMessageObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerInitializer()
        clickerInitializer()
        localizationInitializer()
    }

    private fun observerInitializer() {
        viewModel.errorButtonLiveData.observe(viewLifecycleOwner, errorObserver)
        viewModel.loginPasswordLiveData.observe(viewLifecycleOwner) { loginResult ->
//            if (loginResult) {
//
//            }
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivityForResult(intent, 0)
        }
    }

    private fun clickerInitializer() {
        nextActionButton.setOnClickListener {
            if(clickableButton) {
                nextActionButton.background =
                    resources.getDrawable(R.drawable.button_normal_background_inactive, null)
                clickableButton = false
                viewModel.login(username_edit.text.toString(), password_edit.text.toString())
            }
        }

        registration_hint.setOnClickListener {
            findNavController().navigate(R.id.action_Login_to_Register)
        }
    }

    private fun localizationInitializer() {

    }

    private val errorObserver = Observer<String> {
        clickableButton = true
        nextActionButton.background =
            resources.getDrawable(R.drawable.button_normal_background_active, null)
    }

    override fun showProgress() {
        super.showProgress()
    }

    override fun hideProgress() {
        super.hideProgress()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
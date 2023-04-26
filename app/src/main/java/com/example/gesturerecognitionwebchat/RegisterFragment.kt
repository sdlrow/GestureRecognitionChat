package com.example.gesturerecognitionwebchat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.gesturerecognitionwebchat.base.BaseFragment
import com.example.gesturerecognitionwebchat.databinding.FragmentRegisterBinding
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.nextActionButton
import kotlinx.android.synthetic.main.fragment_register.password_edit
import kotlinx.android.synthetic.main.fragment_register.username_edit
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
    }

    private val errorObserver = Observer<String> {
        clickableButton = true
        nextActionButton.background =
            resources.getDrawable(R.drawable.button_normal_background_active, null)
    }

    private fun clickerInitializer() {
        nextActionButton.setOnClickListener {
            if (clickableButton) {
                nextActionButton.background =
                    resources.getDrawable(R.drawable.button_normal_background_inactive, null)
                clickableButton = false
                viewModel.register(username_edit.text.toString(), password_edit.text.toString())
            }
        }

        login_hint.setOnClickListener {
            findNavController().navigate(R.id.action_Register_to_Login)
        }
    }

    private fun localizationInitializer() {
        (nextActionButton as Button).text = "Зарегистрироваться"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
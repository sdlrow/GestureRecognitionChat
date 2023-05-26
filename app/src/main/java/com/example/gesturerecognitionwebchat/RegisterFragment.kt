package com.example.gesturerecognitionwebchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.gesturerecognitionwebchat.base.BaseFragment
import com.example.gesturerecognitionwebchat.context.MessageType
import com.example.gesturerecognitionwebchat.context.alertWithActions
import com.example.gesturerecognitionwebchat.context.showUpperToast
import com.example.gesturerecognitionwebchat.context.showUpperToastError
import com.example.gesturerecognitionwebchat.databinding.FragmentRegisterBinding
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.nextActionButton
import kotlinx.android.synthetic.main.fragment_register.password_edit
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
        viewModel.registerPasswordLiveData.observe(viewLifecycleOwner) {
            requireActivity().showUpperToast("Вы успешно зарегестрировались")
            findNavController().navigate(R.id.action_Register_to_Login)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun arePasswordsSame(password1: String, password2: String): Boolean {
        return password1 == password2
    }

    private val errorObserver = Observer<String> {
        clickableButton = true
        nextActionButton.background =
            resources.getDrawable(R.drawable.button_normal_background_active, null)
        requireActivity().alertWithActions(
            message = it,
            positiveButtonCallback = {},
            negativeButtonCallback = {},
            negativeText = "Ок",
            type = MessageType.NO_ACTION
        )
    }



    private fun clickerInitializer() {
        nextActionButton.setOnClickListener {
            if (clickableButton) {
                when {
                    !isValidEmail(binding.emailEdit.text.toString()) -> {
                        requireActivity().showUpperToastError("Проверьте правильность введенных данных")
                    }
                    !arePasswordsSame(
                        binding.passwordEdit.text.toString(),
                        binding.passwordAgainEdit.text.toString()
                    ) -> {
                        requireActivity().showUpperToastError("Пароли не совпадают")
                    }
                    binding.passwordEdit.text.toString()
                        .isEmpty() || binding.passwordAgainEdit.text.toString().isEmpty() -> {
                        requireActivity().showUpperToastError("Поле не может быть пустым")
                    }
                    binding.passwordEdit.text.toString().length <= 4 || binding.passwordAgainEdit.text.toString().length <= 4 -> {
                        requireActivity().showUpperToastError("Длина пароля должна быть больше 4 ")
                    }
                    else -> {
                        nextActionButton.background =
                            resources.getDrawable(R.drawable.button_normal_background_inactive, null)
                        clickableButton = false
                        viewModel.register(email_edit.text.toString(), password_edit.text.toString())
                    }
                }
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
package com.example.gesturerecognitionwebchat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.example.gesturerecognitionwebchat.context.alertWithActions
import com.example.gesturerecognitionwebchat.context.showUpperToast
import com.example.gesturerecognitionwebchat.databinding.FragmentAccountBinding
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.image_profile_icon.setImageDrawable(
            AvatarGenerator.AvatarBuilder(requireContext())
                .setLabel("T")
                .setAvatarSize(64)
                .setTextSize(10)
                .toSquare()
                .toCircle()
                .setBackgroundColor(R.color.profile_color)
                .build()
        )

        switch_button_not.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("switvh12", isChecked.toString())
            when (isChecked) {
                true -> requireActivity().showUpperToast("Уведомления включены")
                false -> requireActivity().showUpperToast("Уведомления отключены")
            }
        }

        switch_button_wifi.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d("switvh12", isChecked.toString())
            when (isChecked) {
                true -> requireActivity().showUpperToast("Соединение только по Wi-fi включено")
                false -> requireActivity().showUpperToast("соединение только по Wi-fi отключено")
            }
        }

        my_button.setOnClickListener {
            requireActivity().alertWithActions(
                message = "\n Вы точно хотите выйти? \n",
                positiveButtonCallback = {
                    val intent = Intent(activity, RegisterActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivityForResult(intent, 0)
                },
                negativeButtonCallback = {},
                positiveText = "Выйти", negativeText = "Отменить"
            )
        }
        password_change.setOnClickListener {
            val intent = Intent(activity, ChangeActivity::class.java)
            intent.putExtra("changeData", 1)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivityForResult(intent, 0)
        }
        mail_change.setOnClickListener {
            val intent = Intent(activity, ChangeActivity::class.java)
            intent.putExtra("changeData", 2)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivityForResult(intent, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
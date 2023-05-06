package com.example.gesturerecognitionwebchat

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
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

        switch_button_wifi.setOnCheckedChangeListener{ buttonView, isChecked ->
            requireActivity().showUpperToast("Уведомления отключены")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
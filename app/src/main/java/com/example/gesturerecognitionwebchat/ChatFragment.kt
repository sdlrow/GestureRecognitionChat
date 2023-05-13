package com.example.gesturerecognitionwebchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.adapter.ChatHistoryAdapter
import com.example.gesturerecognitionwebchat.base.BaseFragment
import com.example.gesturerecognitionwebchat.databinding.FragmentChatBinding
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class ChatFragment : BaseFragment() {

    private var _binding: FragmentChatBinding? = null
    lateinit var adapter: ChatHistoryAdapter
    lateinit var viewModel: ChatViewModel
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
        viewModel.chatLiveData.observe(this, chatsHistoryObserver)
        viewModel.statusLiveData.observe(this, statusObserver)
        adapter = ChatHistoryAdapter(requireContext())
        viewModel.showChatHistory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
    }

    private val chatsHistoryObserver = Observer<ArrayList<ChatHistory>> {
        adapter.setData(it)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
        _binding = null
    }
}
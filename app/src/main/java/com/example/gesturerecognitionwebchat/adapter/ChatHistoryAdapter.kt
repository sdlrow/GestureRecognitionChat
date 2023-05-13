package com.example.gesturerecognitionwebchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.example.gesturerecognitionwebchat.Data.ChatHistory
import com.example.gesturerecognitionwebchat.R
import kotlinx.android.synthetic.main.item_history_chat.view.*

class ChatHistoryAdapter(context: Context) : RecyclerView.Adapter<ChatHistoryAdapter.ChatViewHolder>() {

    var fragmentContext: Context = context
    var dataSet = mutableListOf<ChatHistory>()

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ChatHistory, context: Context) {
            itemView.imageProfile.setImageDrawable(
                AvatarGenerator.AvatarBuilder(context = context)
                    .setLabel(item.emailBlur)
                    .setAvatarSize(64)
                    .setTextSize(10)
                    .toSquare()
                    .toCircle()
                    .setBackgroundColor(R.color.profile_color)
                    .build()
            )
            itemView.language.text = "Язык:" + item.sessionLanguage
            itemView.time.text = "Время:" + item.duration
            itemView.session.text = "Номер Сессии:" + item.sessionId
        }
    }

    fun setData(newList: MutableList<ChatHistory>) {
        val diffCallback = DiffCallback(dataSet, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataSet.clear()
        dataSet.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item, fragmentContext)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class DiffCallback(
        private val oldList: List<ChatHistory>,
        private val newList: List<ChatHistory>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].sessionId == newList[newItemPosition].sessionId
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            val (_, _, name) = oldList[oldPosition]
            val (_, _, name1) = newList[newPosition]

            return name == name1
        }
    }

}
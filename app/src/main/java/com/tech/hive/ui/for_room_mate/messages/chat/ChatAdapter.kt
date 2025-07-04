package com.tech.hive.ui.for_room_mate.messages.chat


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tech.hive.databinding.ChatSenderItemBinding
import com.tech.hive.databinding.ReciverChatItemBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = ArrayList<ChatModel>()

    companion object {
        private const val VIEW_TYPE_SENDER = 0
        private const val VIEW_TYPE_RECEIVE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                ConversationRightViewHolder(
                    ChatSenderItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_RECEIVE -> {
                ConversationLeftViewHolder(
                    ReciverChatItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    fun addMessage(message: ChatModel) {
        listItem.add(message)
        notifyItemInserted(listItem.size - 1)
    }

    fun getMessages(): List<ChatModel> = listItem

    override fun getItemCount(): Int {
        return listItem.size
    }


    fun setList(newDataList: List<ChatModel>?) {
        listItem.clear()
        if (newDataList != null) listItem.addAll(newDataList)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listItem[position]

        if (item.type) {
            (holder as ConversationRightViewHolder).binding.apply {
                tvSend.text = item.message
            }
        } else {
            (holder as ConversationLeftViewHolder).binding.apply {
                tvReceiver.text = item.message

            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val item = listItem[position]
        return if (item.type) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVE
        }
    }


    class ConversationRightViewHolder(itemView: ChatSenderItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    class ConversationLeftViewHolder(itemView: ReciverChatItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

}

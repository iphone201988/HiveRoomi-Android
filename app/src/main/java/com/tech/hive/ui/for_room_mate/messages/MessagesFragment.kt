package com.tech.hive.ui.for_room_mate.messages

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetChatData
import com.tech.hive.data.model.GetChatResponse
import com.tech.hive.data.model.OnlineUser
import com.tech.hive.databinding.ChatItemViewBinding
import com.tech.hive.databinding.FragmentMessagesBinding
import com.tech.hive.databinding.StatusItemViewBinding
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.notification.NotificationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding>() {
    private val viewModel: MessagesFragmentVM by viewModels()

    // status adapter
    private lateinit var statusAdapter: SimpleRecyclerViewAdapter<OnlineUser, StatusItemViewBinding>

    // chat adapter
    private lateinit var chatAdapter: SimpleRecyclerViewAdapter<GetChatData, ChatItemViewBinding>
    override fun onCreateView(view: View) {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
        val data = HashMap<String, String>()
        viewModel.getChatApi(Constants.GET_CHAT, data)
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_messages
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    /** handle view **/
    private fun initView() {
        // adapter
        initAdapter()

    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivSettings -> {
                    val intent = Intent(requireContext(), NotificationActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.messagesObserver.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getChatApi" -> {
                            try {
                                val myDataModel: GetChatResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    var count = myDataModel.unReadMessageCount ?: 0
                                    var notificationCount = myDataModel.unReadNotifications ?: 0
                                    if (notificationCount>0){
                                        binding.ivNotificationCount.text = "$notificationCount"
                                        binding.ivNotificationCount.visibility = View.VISIBLE
                                    }else{
                                        binding.ivNotificationCount.visibility = View.GONE
                                    }
                                    if (count > 0) {
                                        binding.tvMsgCount.text =
                                            getString(R.string.new_messages, count.toString())
                                    } else {
                                        binding.tvMsgCount.text = getString(R.string._0_new_message)
                                    }
                                    chatAdapter.clearList()
                                    statusAdapter.clearList()
                                    chatAdapter.list = myDataModel.data as List<GetChatData?>?
                                    statusAdapter.list = myDataModel.onlineUsers as List<OnlineUser?>?

                                    if (chatAdapter.list.isEmpty()){
                                        binding.tvEmptyChat.visibility = View.VISIBLE
                                        binding.tvAllMsgs.visibility = View.GONE
                                        binding.tvAllMsgs.visibility = View.GONE
                                    }else{
                                        binding.tvAllMsgs.visibility = View.VISIBLE
                                    }

                                }
                            } catch (e: Exception) {
                                binding.tvEmptyChat.visibility = View.VISIBLE
                                binding.tvAllMsgs.visibility = View.GONE
                                binding.tvAllMsgs.visibility = View.GONE

                                Log.e("error", "getHomeApi: $e")
                            } finally {

                                hideLoading()
                            }
                        }


                    }
                }

                Status.ERROR -> {
                    binding.tvEmptyChat.visibility = View.VISIBLE
                    binding.tvAllMsgs.visibility = View.GONE
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }

    }

    /** handle adapter **/
    private fun initAdapter() {
        // status adapter
        statusAdapter = SimpleRecyclerViewAdapter(R.layout.status_item_view, BR.bean) { v, m, pos ->
            when (v.id) {
                // move to Chat Activity
                R.id.clMail -> {
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("chatId", m._id)
                    intent.putExtra("socketId", m.otherUser?._id)
                    startActivity(intent)
                }
            }
        }
        binding.rvOnlineStatus.adapter = statusAdapter

        // chat adapter
        chatAdapter = SimpleRecyclerViewAdapter(R.layout.chat_item_view, BR.bean) { v, m, pos ->
            when (v.id) {
                // move to Chat Activity
                R.id.clMain -> {
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("chatId", m._id)
                    intent.putExtra("socketId", m.otherUser?._id)
                    startActivity(intent)
                }
            }
        }
        //  chatAdapter.list = getChatList()
        binding.rvChats.adapter = chatAdapter
    }


}
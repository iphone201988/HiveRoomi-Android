package com.tech.hive.ui.for_room_mate.messages

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.ChatModel
import com.tech.hive.data.model.StatusModel
import com.tech.hive.databinding.ChatItemViewBinding
import com.tech.hive.databinding.FragmentMessagesBinding
import com.tech.hive.databinding.StatusItemViewBinding
import com.tech.hive.ui.for_room_mate.messages.chat.ChatActivity
import com.tech.hive.ui.notification.NotificationActivity
import com.tech.hive.ui.room_offering.discover.DiscoverySettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding>() {
    private val viewModel: MessagesFragmentVM by viewModels()

    // status adapter
    private lateinit var statusAdapter: SimpleRecyclerViewAdapter<StatusModel, StatusItemViewBinding>

    // chat adapter
    private lateinit var chatAdapter: SimpleRecyclerViewAdapter<ChatModel, ChatItemViewBinding>
    override fun onCreateView(view: View) {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
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
             when(it?.id){
                 R.id.ivSettings->{
                     val intent = Intent(requireContext(), NotificationActivity::class.java)
                     startActivity(intent)
                 }
             }
        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.messagesObserver.observe(viewLifecycleOwner) {

        }

    }

    /** handle adapter **/
    private fun initAdapter() {
        // status adapter
        statusAdapter = SimpleRecyclerViewAdapter(R.layout.status_item_view, BR.bean) { v, m, pos ->
            when (v.id) {

            }
        }
        statusAdapter.list = getStatusList()
        binding.rvOnlineStatus.adapter = statusAdapter

        // chat adapter
        chatAdapter = SimpleRecyclerViewAdapter(R.layout.chat_item_view, BR.bean) { v, m, pos ->
            when (v.id) {
                // move to Chat Activity
                R.id.clMain -> {
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        chatAdapter.list = getChatList()
        binding.rvChats.adapter = chatAdapter
    }


    // list
    private fun getStatusList(): ArrayList<StatusModel> {
        val list = ArrayList<StatusModel>()
        list.add(StatusModel(R.drawable.ic_match_dummy, true))
        list.add(StatusModel(R.drawable.ic_match_dummy, false))
        list.add(StatusModel(R.drawable.ic_match_dummy, false))
        list.add(StatusModel(R.drawable.ic_match_dummy, true))
        list.add(StatusModel(R.drawable.ic_match_dummy, false))
        list.add(StatusModel(R.drawable.ic_match_dummy, false))
        list.add(StatusModel(R.drawable.ic_match_dummy, false))
        list.add(StatusModel(R.drawable.ic_match_dummy, true))
        list.add(StatusModel(R.drawable.ic_match_dummy, true))
        return list
    }


    private fun getChatList(): ArrayList<ChatModel> {
        val list = ArrayList<ChatModel>()
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", true))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Bonnie", "Hello", "10:00 AM", false))
        list.add(
            ChatModel(
                R.drawable.ic_match_dummy, "Chris Salvatore", "Hello", "10:00 AM", false
            )
        )
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", false))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", false))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", false))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", false))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", true))
        list.add(ChatModel(R.drawable.ic_match_dummy, "Chris Salvatore", "Hi", "10:00 AM", true))
        return list
    }

}
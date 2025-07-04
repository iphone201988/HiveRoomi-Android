package com.tech.hive.ui.for_room_mate.messages.chat

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityChatBinding
import com.tech.hive.databinding.CalenderDialogItemBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val viewModel: ChatActivityVM by viewModels()
    private var calenderDialog: BaseCustomDialog<CalenderDialogItemBinding>? = null
    private lateinit var chatAdapter: ChatAdapter
    override fun getLayoutResource(): Int {
        return R.layout.activity_chat
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // view
        initView()
        // click
        initOnClick()
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ChatActivity)
        BindingUtils.statusBarTextColor(this@ChatActivity, false)
        // adapter initialize
        chatAdapter = ChatAdapter()
        binding.rvChat.adapter = chatAdapter
        chatAdapter.setList(chatList())

    }

    /*** all click event handle here ***/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(this@ChatActivity) {
            when (it?.id) {
                // dot button click
                R.id.ivDot -> {
                    if (binding.clEdit.visibility == View.VISIBLE) {
                        binding.clEdit.visibility = View.GONE
                    } else {
                        binding.clEdit.visibility = View.VISIBLE
                    }
                }

                // add button click
                R.id.ivAdd -> {
                    if (binding.ivCategory.visibility == View.GONE) {
                        fadeInAnimation(binding.ivCategory)
                    } else {
                        fadeOutAnimation(binding.ivCategory)

                    }
                }

                // calender icon click
                R.id.ivCalender -> {
                    // dialog
                    initDialog()
                }


                // send message
                R.id.ivSend -> {
                    val message = binding.etEmail.text.toString().trim()
                    if (message.isEmpty()) {
                        showInfoToast("Please enter any message")
                    } else {
                        val chatMessage = ChatModel(message, true)
                        chatAdapter.addMessage(chatMessage)
                        // Clear input
                        binding.etEmail.setText("")
                        // Scroll to bottom
                        binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        }

        // add category button click
        binding.ivCategory.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val totalHeight = v.height
                val sectionHeight = totalHeight / 4
                val y = event.y.toInt()

                when {
                    y < sectionHeight -> {
                        // 1st zone - Document
                        Toast.makeText(this, "Document Clicked", Toast.LENGTH_SHORT).show()
                    }

                    y < sectionHeight * 2 -> {
                        // 2nd zone - Image
                        Toast.makeText(this, "Image Clicked", Toast.LENGTH_SHORT).show()
                    }

                    y < sectionHeight * 3 -> {
                        // 3rd zone - Attachment
                        Toast.makeText(this, "Attachment Clicked", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        // 4th zone - Camera
                        Toast.makeText(this, "Camera Clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }

    }

    /** dialog **/
    private fun initDialog() {
        calenderDialog = BaseCustomDialog(this@ChatActivity, R.layout.calender_dialog_item) {
            when (it?.id) {
                R.id.tvCancel -> {
                    calenderDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    calenderDialog?.dismiss()
                }

                // time button  click
                R.id.tvTimes -> {
                    val calendar = Calendar.getInstance()
                    val hour = calendar[Calendar.HOUR_OF_DAY]
                    val minute = calendar[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(
                        this@ChatActivity, { view, hourOfDay, minute ->
                            calenderDialog?.binding?.tvTimes?.text = String.format(
                                "%02d:%02d", hourOfDay, minute
                            )

                        }, hour, minute, true
                    ) // 'true' for 24-hour format

                    timePickerDialog.show()
                }
            }

        }
        calenderDialog?.setCancelable(false)
        calenderDialog?.create()
        calenderDialog?.show()


        var calendar = Calendar.getInstance()
        calenderDialog?.binding?.rangeCalenderOneTime?.setDate(calendar)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentMonthName = monthFormat.format(calendar.time)
        val currentYear = yearFormat.format(calendar.time)
        calenderDialog?.binding?.tvMonth?.text = currentMonthName
        calenderDialog?.binding?.tvYear?.text = currentYear

        calenderDialog?.binding?.rangeCalenderOneTime?.setOnPreviousPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                calendar = calenderDialog?.binding?.rangeCalenderOneTime?.currentPageDate!!
                calendar.set(Calendar.DAY_OF_MONTH, 1)

                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)

                calenderDialog?.binding?.tvMonth?.text = monthName
                calenderDialog?.binding?.tvYear?.text = year

            }
        })

        calenderDialog?.binding?.rangeCalenderOneTime?.setOnForwardPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                calendar = calenderDialog?.binding?.rangeCalenderOneTime?.currentPageDate!!
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)
                calenderDialog?.binding!!.tvMonth.text = monthName
                calenderDialog?.binding!!.tvYear.text = year

            }
        })


        calenderClick()

    }

    private fun calenderClick() {
        calenderDialog?.binding?.rangeCalenderOneTime?.setOnDayClickListener(object :
            OnDayClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar.time
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val startDate1 = clickedDayCalendar.time
                val strStartDate: String = sdf.format(startDate1)
                Log.d("fgdfgfgdg", "onDayClick: $strStartDate")
            }
        })
    }

}

/*** Fades out a view smoothly and hides it when the animation ends.*/
private fun fadeOutAnimation(view: View) {
    view.animate().alpha(0f).setDuration(1000).withEndAction { view.visibility = View.GONE }
}

/*** Fades in a view smoothly and makes it visible */
private fun fadeInAnimation(view: View) {
    view.apply {
        visibility = View.VISIBLE
        alpha = 0f
        animate().alpha(1f).setDuration(1000)
    }
}

private fun chatList(): ArrayList<ChatModel> {
    val list = ArrayList<ChatModel>()
    list.add(ChatModel("Hey there!", true))
    list.add(ChatModel("Hi! How are you?", false))
    list.add(ChatModel("I'm good, thanks! You?", true))
    list.add(ChatModel("Doing well, thanks for asking.", false))
    list.add(ChatModel("Are you free to meet today?", true))
    list.add(ChatModel("Yes, after 5 PM works.", false))

    return list
}





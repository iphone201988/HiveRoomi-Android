package com.tech.hive.ui.for_room_mate.calenders


import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.viewModels
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.databinding.ActivityCalenderBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalenderActivity : BaseActivity<ActivityCalenderBinding>() {
    private val viewModel: CalenderActivityVM by viewModels()
    private lateinit var calendar: Calendar
    override fun getLayoutResource(): Int {
        return R.layout.activity_calender
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()

    }


    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@CalenderActivity)
        BindingUtils.statusBarTextColor(this@CalenderActivity, false)

        calendar = Calendar.getInstance()
        binding.rangeCalenderOneTime.setDate(calendar)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentMonthName = monthFormat.format(calendar.time)
        val currentYear = yearFormat.format(calendar.time)
        binding.tvMonth.text = currentMonthName
        binding.tvYear.text = currentYear

        binding.rangeCalenderOneTime.setOnPreviousPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                val calendar = binding.rangeCalenderOneTime.currentPageDate
                calendar.set(Calendar.DAY_OF_MONTH, 1)

                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)

                binding.tvMonth.text = monthName
                binding.tvYear.text = year

            }
        })

        binding.rangeCalenderOneTime.setOnForwardPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                val calendar = binding.rangeCalenderOneTime.currentPageDate
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)
                binding.tvMonth.text = monthName
                binding.tvYear.text = year

            }
        })


        calenderClick()
    }

    private fun calenderClick() {
        binding.rangeCalenderOneTime.setOnDayClickListener(object : OnDayClickListener {
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

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

            }
        }

    }

    /** handle api response **/
    private fun initObserver() {

    }
}

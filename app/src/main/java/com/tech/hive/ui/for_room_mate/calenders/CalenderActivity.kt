package com.tech.hive.ui.for_room_mate.calenders


import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetVisitResponse
import com.tech.hive.data.model.VisitData
import com.tech.hive.databinding.ActivityCalenderBinding
import com.tech.hive.databinding.VisitRvItemBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CalenderActivity : BaseActivity<ActivityCalenderBinding>() {
    private val viewModel: CalenderActivityVM by viewModels()
    private lateinit var calendar: Calendar
    private lateinit var calenderAdapter: SimpleRecyclerViewAdapter<VisitData, VisitRvItemBinding>
    private var currentMonth = ""
    private var currentYearName = ""
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
        // adapter
        initAdapter()
    }


    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@CalenderActivity)
        BindingUtils.statusBarTextColor(this@CalenderActivity, false)

        calendar = Calendar.getInstance()
        binding.calenderView.setDate(calendar)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentMonthName = monthFormat.format(calendar.time)
        var currentYear = yearFormat.format(calendar.time)
        currentMonth = currentMonthName
        currentYearName = currentYear
        binding.tvMonth.text = currentMonthName
        binding.tvYear.text = currentYear

        binding.tvEvents.text =
            getString(R.string.events_on, currentMonth)

        updateStartAndEndOfMonth(calendar)

        // handle previous and next month
        binding.calenderView.setOnPreviousPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val calendar = binding.calenderView.currentPageDate
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)
                currentMonth = currentMonthName
                currentYearName = year
                binding.tvMonth.text = monthName
                binding.tvYear.text = year
                updateStartAndEndOfMonth(calendar)

            }
        })

        binding.calenderView.setOnForwardPageChangeListener(object : OnCalendarPageChangeListener {
            override fun onChange() {
                val calendar = binding.calenderView.currentPageDate
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)
                currentMonth = currentMonthName
                currentYearName = year
                binding.tvMonth.text = monthName
                binding.tvYear.text = year
                updateStartAndEndOfMonth(calendar)
            }
        })


        calenderClick()
    }


    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

                R.id.btnDone -> {
                    finish()
                }

            }
        }

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.chatObserver.observe(this@CalenderActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getVisitApiCAll" -> {
                            try {
                                val myDataModel: GetVisitResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    calenderAdapter.list = myDataModel.data
                                    if (calenderAdapter.list.isNotEmpty()) {
                                        binding.tvEvents.text = getString(R.string.events_on, currentMonth)
                                        binding.tvEmpty.visibility = View.GONE
                                    } else {
                                        binding.tvEvents.text = getString(R.string.events_in,
                                            "$currentMonth $currentYearName"
                                        )
                                        binding.tvEmpty.visibility = View.VISIBLE
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }


                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {

                }
            }
        }

    }

    /** calender click get date **/
    private fun calenderClick() {
        binding.calenderView.setOnDayClickListener(object : OnDayClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar.time
                // Format clicked date to yyyy-MM-dd
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val selectedDateStr = sdf.format(clickedDayCalendar)
                // Format to "15 Jan, Thursday"
                val displayFormat = SimpleDateFormat("d MMM, EEEE", Locale.US)
                val clickedDayInfo = displayFormat.format(clickedDayCalendar)
                currentMonth = clickedDayInfo
                val data = HashMap<String, String>()
                data["startDate"] = selectedDateStr
                data["endDate"] = selectedDateStr
                viewModel.getVisitApiCAll(Constants.VISIT_SCHEDULE, data)
            }
        })
    }

    /** get start and end date function **/
    private fun updateStartAndEndOfMonth(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startCal = calendar.clone() as Calendar
        startCal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(startCal.time)
        val endCal = calendar.clone() as Calendar
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = dateFormat.format(endCal.time)
        val data = HashMap<String, String>()
        data["startDate"] = startDate
        data["endDate"] = endDate
        viewModel.getVisitApiCAll(Constants.VISIT_SCHEDULE, data)
    }

    /** visit adapter **/
    private fun initAdapter() {
        calenderAdapter = SimpleRecyclerViewAdapter(R.layout.visit_rv_item, BR.bean) { v, m, pos ->
            when (v.id) {
                R.id.clVisit -> {
                }

            }
        }

        binding.rvVisit.adapter = calenderAdapter
    }


}

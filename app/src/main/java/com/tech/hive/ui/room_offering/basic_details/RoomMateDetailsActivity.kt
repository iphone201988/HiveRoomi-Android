package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.RoomMateModel
import com.tech.hive.databinding.ActivityRoomMateDetailsBinding
import com.tech.hive.databinding.AddRoomMateItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomMateDetailsActivity : BaseActivity<ActivityRoomMateDetailsBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private lateinit var roomMateAdapter: SimpleRecyclerViewAdapter<RoomMateModel, AddRoomMateItemViewBinding>
    private var roomiesList = ArrayList<RoomMateModel>()
    override fun getLayoutResource(): Int {
        return R.layout.activity_room_mate_details
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
        BindingUtils.statusBarStyle(this@RoomMateDetailsActivity)
        BindingUtils.statusBarTextColor(this@RoomMateDetailsActivity, false)
        // check state
        binding.genderType = ""
        binding.ageType = ""

        // adapter
        initAdapter()
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn add
                R.id.btnAdd -> {
                    if (validate()) {
                        roomMateAdapter.list.add(
                            RoomMateModel(
                                "M",
                                binding.etAge.text.toString().trim()
                            )
                        )
                        roomMateAdapter.list.reverse()
                        binding.etAge.setText("")
                        binding.etAge.clearFocus()
                        roomMateAdapter.notifyDataSetChanged()
                    }
                }
                // btn next
                R.id.btnContinue -> {
                    startActivity(Intent(this, ApartmentFeaturesActivity::class.java))
                }

                R.id.ivBack->{
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        // etGender
        binding.etGender.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.genderType = p0.toString()
            }

        })

        // etAge
        binding.etAge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.ageType = p0.toString()
            }

        })

    }

    /** handle api response  **/
    private fun initObserver() {

    }

    /** handle adapter **/
    private fun initAdapter() {
        roomMateAdapter =
            SimpleRecyclerViewAdapter(R.layout.add_room_mate_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel item
                    R.id.ivCancel -> {
                        roomMateAdapter.removeItem(pos)
                        roomMateAdapter.notifyItemChanged(pos, null)
                    }
                }
            }
        roomMateAdapter.list = roomiesList
        binding.rvAddRoommate.adapter = roomMateAdapter
    }

    /** validations **/
    private fun validate(): Boolean {
        if (binding.etAge.text.toString().isEmpty()) {
            showToast("Please enter age")
            return false
        }
        return true
    }
}
package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.CompatibilityModel
import com.tech.hive.databinding.ActivityBasicDetailsBinding
import com.tech.hive.databinding.UploadImageItemViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasicDetailsActivity : BaseActivity<ActivityBasicDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: BasicDetailsVM by viewModels()
    private var googleMap: GoogleMap? = null

    // image adapter
    private lateinit var uploadImageAdapter: SimpleRecyclerViewAdapter<CompatibilityModel, UploadImageItemViewBinding>
    override fun getLayoutResource(): Int {
        return R.layout.activity_basic_details
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
        BindingUtils.statusBarStyle(this@BasicDetailsActivity)
        BindingUtils.statusBarTextColor(this@BasicDetailsActivity, false)
        // check state
        binding.roomType = ""
        binding.titleType = ""
        binding.bioType = ""
        binding.locationType = ""

        // adapter
        initAdapter()
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btnVideo
                R.id.tvVideo -> {
                    showToast("Upload Video")
                }
                // btn Next
                R.id.btnContinue -> {
                    startActivity(Intent(this, PriceTermsActivity::class.java))
                }
                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

        }

        // etRoomType
        binding.etRoom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.roomType = p0.toString()
            }

        })

        // etTitle
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.titleType = p0.toString()
            }

        })

        // etDesc
        binding.etShortBio.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.bioType = p0.toString()
            }

        })

        // etLocation
        binding.etLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.locationType = p0.toString()
            }

        })

    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.basicDetailObserver.observe(this) {

        }

    }

    /** handle adapter **/
    private fun initAdapter() {
        uploadImageAdapter =
            SimpleRecyclerViewAdapter(R.layout.upload_image_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clImage, R.id.tvImage -> {
                        showToast(getString(R.string.upload_images))
                    }
                }
            }
        uploadImageAdapter.list = getList()
        // images Adapter
        binding.rvImages.adapter = uploadImageAdapter
    }

    // list
    private fun getList(): ArrayList<CompatibilityModel> {
        val list = ArrayList<CompatibilityModel>()
        list.add(CompatibilityModel(""))
        list.add(CompatibilityModel(""))
        list.add(CompatibilityModel(""))
        return list
    }

    /**map handling **/
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.uiSettings?.isMapToolbarEnabled = true
    }


}
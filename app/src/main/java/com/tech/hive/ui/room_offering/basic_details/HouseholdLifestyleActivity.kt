package com.tech.hive.ui.room_offering.basic_details

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import com.google.gson.Gson
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showInfoToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.ImageModel
import com.tech.hive.data.model.PostListingResponse
import com.tech.hive.data.model.RoomMateModelItem
import com.tech.hive.databinding.ActivityHouseholdLifestyleBinding
import com.tech.hive.databinding.FeaturesItemViewBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HouseholdLifestyleActivity : BaseActivity<ActivityHouseholdLifestyleBinding>() {
    private val viewModel: BasicDetailsVM by viewModels()
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var videoMultiplatform: MultipartBody.Part? = null
    private var roomType: String? = null
    private var titleType: String? = null
    private var bioType: String? = null
    private var locationType: String? = null
    private var priceType: String? = null
    private var utilityPriceType: String? = null
    private var depositType: String? = null
    private var contractType: String? = null
    private var availableType: String? = null
    private var minimumStayType: String? = null

    private var roomDetailType: String? = null
    private var sizeType: String? = null
    private var furnishedType: String? = null
    private var roommateType: String? = null
    private var floorType: String? = null
    private var elevatorType: String? = null
    private var heatingType: String? = null

    private var privateType: String? = null
    private var wifiType: String? = null
    private var equippedType: String? = null
    private var washingType: String? = null
    private var airType: String? = null
    private var balconyType: String? = null
    private var parkingType: String? = null
    private var listingData: GetListingData? = null
    private var editApiCall = false

    companion object {
        var sendImage = mutableListOf<ImageModel>()
        var changePosition = mutableListOf<String>()

        var sendVideo: File? = null
        var roomMateModelItem = ArrayList<RoomMateModelItem>()

        var videoUrl : String?=null
        var deleteImage = mutableListOf<String>()
    }

    // adapter
    private lateinit var lookingAdapter: SimpleRecyclerViewAdapter<String, FeaturesItemViewBinding>

    override fun getLayoutResource(): Int {
        return R.layout.activity_household_lifestyle
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
        BindingUtils.statusBarStyle(this@HouseholdLifestyleActivity)
        BindingUtils.statusBarTextColor(this@HouseholdLifestyleActivity, false)
        // adapter
        initAdapter()
        // intent data
        roomType = intent.getStringExtra("roomType")
        titleType = intent.getStringExtra("titleType")
        bioType = intent.getStringExtra("bioType")
        locationType = intent.getStringExtra("locationType")
        priceType = intent.getStringExtra("priceType")
        utilityPriceType = intent.getStringExtra("utilityPriceType")
        depositType = intent.getStringExtra("depositType")
        contractType = intent.getStringExtra("contractType")
        availableType = intent.getStringExtra("availableType")
        minimumStayType = intent.getStringExtra("minimumStayType")

        roomDetailType = intent.getStringExtra("roomDetailType")
        sizeType = intent.getStringExtra("sizeType")
        furnishedType = intent.getStringExtra("furnishedType")
        roommateType = intent.getStringExtra("roommateType")
        floorType = intent.getStringExtra("floorType")
        elevatorType = intent.getStringExtra("elevatorType")
        heatingType = intent.getStringExtra("heatingType")

        privateType = intent.getStringExtra("privateType")
        wifiType = intent.getStringExtra("wifiType")
        equippedType = intent.getStringExtra("equippedType")
        washingType = intent.getStringExtra("washingType")
        airType = intent.getStringExtra("airType")
        balconyType = intent.getStringExtra("balconyType")
        parkingType = intent.getStringExtra("parkingType")

        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            editApiCall = true
            binding.btnContinue.text = getString(R.string.update)
            val lookingList = it.lookingFor?.filterNotNull() ?: emptyList()
            lookingAdapter.list = ArrayList(lookingList)
            if (lookingAdapter.list.isNotEmpty()) {
                binding.etLooking.setText(lookingAdapter.list[0])
            }
            binding.etSmoking.setText(it.smoke)
            binding.etClean.setText(it.cleanliness.toString())
            if (it.pets == true) {
                binding.etPets.setText(getString(R.string.yes))
            } else {
                binding.etPets.setText(getString(R.string.no))
            }
        }
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btn next
                R.id.btnContinue -> {
                    if (validate()) {
                        apiCallListingCreate()
                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }


                R.id.etSmoking -> {
                    personalDialog(1)
                }

                R.id.etPets -> {
                    personalDialog(2)
                }

                R.id.etClean -> {
                    personalDialog(3)
                }

                R.id.etLooking -> {
                    personalDialog(4)
                }
            }


        }
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.basicDetailObserver.observe(this@HouseholdLifestyleActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "basicDetailApiCall" -> {
                            try {
                                val myDataModel: PostListingResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApiListening: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "basicDetailEditAPiCall" -> {
                            try {
                                val myDataModel: PostListingResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                showInfoToast(myDataModel?.message.toString())
                                val intent = Intent(this, DashboardActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            } catch (e: Exception) {
                                Log.e("error", "getHomeApiListening: $e")
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


    /** handle adapter **/
    private fun initAdapter() {
        // main adapter
        lookingAdapter =
            SimpleRecyclerViewAdapter(R.layout.features_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    // cancel item
                    R.id.ivCancel -> {
                        lookingAdapter.removeItem(pos)
                        lookingAdapter.notifyItemChanged(pos, null)
                        if (lookingAdapter.list.isEmpty()) {
                            binding.etLooking.setText("")
                        } else {
                            binding.etLooking.setText(lookingAdapter.list[0])
                        }
                    }
                }
            }

        binding.rvLooking.adapter = lookingAdapter
    }


    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal =
            BaseCustomDialog(this@HouseholdLifestyleActivity, R.layout.personal_dialog_item) {

            }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom(type)
    }

    /** handle adapter **/
    @SuppressLint("NotifyDataSetChanged")
    private fun initAdapterRoom(type: Int) {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    when (type) {
                        1 -> {
                            binding.etSmoking.setText(m.toString())
                        }

                        2 -> {
                            binding.etPets.setText(m.toString())
                        }

                        3 -> {
                            binding.etClean.setText(m.toString())
                        }

                        4 -> {
                            val value = m.toString().lowercase()
                            if (!lookingAdapter.list.contains(value)) {
                                lookingAdapter.list.add(value)
                                lookingAdapter.notifyDataSetChanged()
                                binding.etLooking.setText(lookingAdapter.list[0])
                            }

                        }

                    }

                    personal?.dismiss()
                }
            }
        }
        when (type) {
            1 -> {
                ageAdapter.list = firstTypeList()
            }

            2 -> {
                ageAdapter.list = secondTypeList()
            }

            3 -> {
                ageAdapter.list = thirdList()
            }

            4 -> {
                ageAdapter.list = fourList()
            }
        }

        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun firstTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.never),
            getString(R.string.sometimes),
            getString(R.string.often),

            )
    }

    private fun secondTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.yes),
            getString(R.string.no),

            )
    }

    private fun thirdList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.one),
            getString(R.string.two),
            getString(R.string.three),
            getString(R.string.four),
            getString(R.string.five),

            )
    }

    private fun fourList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.student),
            getString(R.string.workers),
            getString(R.string.anyone),


            )
    }

    var formattedDate = ""

    /** api call listing **/
    private fun apiCallListingCreate() {
        val roomMatesJson = Gson().toJson(roomMateModelItem)
        val lookingJson = Gson().toJson(lookingAdapter.list)
        val changeImagePosition = Gson().toJson(changePosition)
        val deleteImageApi = Gson().toJson(deleteImage)



        if (sendVideo != null) {
            val requestFile = sendVideo!!.asRequestBody("video/*".toMediaTypeOrNull())
            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "uploadVideos", sendVideo!!.name, requestFile
            )
            videoMultiplatform = body
        }


        if (availableType?.isNotEmpty() == true) {
            try {
                val inputFormat = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
                val parsedDate = inputFormat.parse(availableType.toString())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
                outputFormat.timeZone = TimeZone.getDefault()
                val formattedDateq = outputFormat.format(parsedDate!!)
                formattedDate = formattedDateq
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var pets = binding.etPets.text.toString().trim()
        val data = HashMap<String, RequestBody>()
        data["listingType"] = roomType.toString().toRequestBody()
        data["title"] = titleType.toString().toRequestBody()
        data["description"] = bioType.toString().toRequestBody()
        data["address"] = locationType.toString().toRequestBody()
        data["latitude"] = "30.7333148".toRequestBody()
        data["longitude"] = "76.7794179".toRequestBody()
        data["price"] = priceType.toString().toRequestBody()
        data["utilitiesPrice"] = utilityPriceType.toString().toRequestBody()
        data["deposit"] = depositType.toString().toRequestBody()
        data["contractLength"] = contractType.toString().toRequestBody()
        data["availableFrom"] = formattedDate.toRequestBody()
        data["minimumStay"] = minimumStayType.toString().toRequestBody()
        data["roomType"] = roomDetailType.toString().toRequestBody()
        data["size"] = sizeType.toString().toRequestBody()
        data["furnishingStatus"] = furnishedType.toString().toRequestBody()
        data["floor"] = floorType.toString().toRequestBody()
        data["elevator"] =
            (elevatorType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["heating"] = heatingType.toString().toRequestBody()
        data["privateBathroom"] =
            (privateType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["wifi"] = (wifiType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["kitchen"] =
            (equippedType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["washingMachine"] =
            (washingType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["airConditioner"] =
            (airType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["balcony"] =
            (balconyType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["parking"] =
            (parkingType?.lowercase()?.contains("yes") == true).toString().toRequestBody()
        data["roommates"] =
            roomMatesJson.toRequestBody()  // [{"gender":"men","age":"36"},{"gender":"women","age":"32"}]
        data["smoke"] = binding.etSmoking.text.toString().trim().toRequestBody()
        data["pets"] = (pets.lowercase().contains("yes") == true).toString().toRequestBody()
        data["cleanliness"] = binding.etClean.text.toString().trim().toRequestBody()
        data["lookingFor"] = lookingJson.toRequestBody() // ["student","workers","anyone"]

        val onlyParts =
            sendImage.dropLast(1).flatMap { it.imageMultiplatform.map { pair -> pair.second } }
                .toMutableList()

        if (editApiCall) {
            if (deleteImageApi.isNotEmpty()){
                data["images"] = deleteImageApi.toRequestBody()
            }

            if (!videoUrl.isNullOrEmpty()){
                data["videos"] = videoUrl.toString().toRequestBody()
            }

            data["newPositionImages"] = changeImagePosition.toRequestBody()

            listingData?._id?.let { id ->
                viewModel.basicDetailEditAPiCall(
                    "listing/$id", data, onlyParts, videoMultiplatform
                )
            }
        } else {
            viewModel.basicDetailApiCall(
                Constants.LISTING_CREATE, data, onlyParts, videoMultiplatform
            )
        }

    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val smoking = binding.etSmoking.text.toString().trim()
        val pets = binding.etPets.text.toString().trim()
        val clean = binding.etClean.text.toString().trim()
        val looking = binding.etLooking.text.toString().trim()


        if (smoking.isEmpty()) {
            showInfoToast("Please select smoke")
            return false
        } else if (pets.isEmpty()) {
            showInfoToast("Please select pets")
            return false
        } else if (clean.isEmpty()) {
            showInfoToast("Please select cleaning")
            return false
        } else if (looking.isEmpty()) {
            showInfoToast("Please select looking")
            return false
        }

        return true
    }

}
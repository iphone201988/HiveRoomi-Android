package com.tech.hive.ui.for_room_mate.profile

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amazonaws.regions.Regions
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.location.LocationSearchManager
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetUserProfileResponse
import com.tech.hive.databinding.ActivityEditProfileBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.quiz.QuizActivity
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartImage: MultipartBody.Part? = null
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var type = 1
    private var editType = 0
    private var userIdProof = ""
    private var ownerShipProof = ""
    private var lat : Double?=null
    private var long : Double?=null

    override fun getLayoutResource(): Int {
        return R.layout.activity_edit_profile
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
        type = 0

        // intent
        val intent = intent
        val type = intent.getStringExtra("sendType")
        when (type) {
            "first" -> {
                editType = 1
                binding.visibilityType = 1
            }

            "third" -> {
                editType = 2
                binding.visibilityType = 2
            }

        }

        // Make keyboard resize layout
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // set status bar color
        BindingUtils.statusBarStyle(this@EditProfileActivity)

        // Keyboard insets listener to avoid send button getting hidden
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(0, 0, 0, imeHeight)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // api call for profile
        viewModel.getUserProfile(Constants.USER_ME)
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@EditProfileActivity)
        BindingUtils.statusBarTextColor(this@EditProfileActivity, false)
        // check state
        binding.fullNameType = ""
        binding.bioType = ""
        binding.professionOther = ""
        binding.locationName = ""

    }

    /*** personal information api call ***/
    private fun updateProfile() {
        val data = HashMap<String, RequestBody>()
        val fullName = binding.etFullName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val gender = binding.etGender.text.toString().trim().lowercase()
        val professionRole = binding.etProfessionRole.text.toString().trim()
        val shortBio = binding.etShortBio.text.toString().trim()
        val campus = binding.etCampus.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (professionRole.contains("Other")){
            data["profession"] = binding.etProfessionOther.text.toString().trim().toRequestBody()
        }else{
            data["profession"] = professionRole.toRequestBody()
        }

        if (professionRole.contains("Student")) {
            data["campus"] = campus.toRequestBody()
        }


        data["name"] = fullName.toRequestBody()
        data["gender"] = gender.toRequestBody()
        data["ageRange"] = age.toRequestBody()

        data["bio"] = shortBio.toRequestBody()
        data["address"] = location.toRequestBody()
        if(lat!=null){
            data["latitude"] = lat.toString().toRequestBody()
        }
        if(long!=null){
            data["longitude"] = lat.toString().toRequestBody()
        }



        if (editType == 2) {
            data["providerType"] = binding.etProvider.text.toString().trim().toRequestBody()
        }
        viewModel.userUpdateProfile(data, multipartImage)
    }

    /** handle click **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    finish()
                }

                R.id.btnContinue -> {
                    updateProfile()
                }
                // camera button click
                R.id.ivCamera -> {
                    if (!BindingUtils.hasPermissions(
                            this@EditProfileActivity, BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCameraIntent()
                    }
                }
                // Gallery button click
                R.id.ivGallery -> {
                    if (!BindingUtils.hasPermissions(
                            this@EditProfileActivity, BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)

                    } else {
                        galleryImagePicker()

                    }
                }

                R.id.ivAge, R.id.etAge -> {
                    personalDialog(1)
                }

                R.id.ivGender, R.id.etGender -> {
                    personalDialog(2)
                }

                R.id.ivRole, R.id.etProfessionRole -> {
                    personalDialog(3)
                }

                R.id.ivCampus, R.id.etCampus -> {
                    personalDialog(4)
                }

                R.id.tvEditPreferencesDetails, R.id.ivChangeLanguage -> {
                    Constants.quiz = true
                    val intent = Intent(this@EditProfileActivity, QuizActivity::class.java)
                    startActivity(intent)
                }

                R.id.etDocuments, R.id.ivDocuments -> {
                    val intent =
                        Intent(this@EditProfileActivity, UpdateDocumentActivity::class.java)
                    intent.putExtra("userIdImage", userIdProof)
                    intent.putExtra("ownerIdImage", ownerShipProof)
                    startActivity(intent)
                }

                R.id.etProvider, R.id.ivProvider -> {
                    personalDialog(5)
                }
            }
        }


        binding.etProfessionRole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.isNotEmpty() == true && s.contains("Student")) {
                    binding.etCampus.visibility = View.VISIBLE
                    binding.tvCampus.visibility = View.VISIBLE
                    binding.ivCampus.visibility = View.VISIBLE
                } else {
                    binding.etCampus.visibility = View.GONE
                    binding.tvCampus.visibility = View.GONE
                    binding.ivCampus.visibility = View.GONE
                }
                if (s?.isNotEmpty() == true && s.contains("Other")){
                    binding.tvProfessionRoleOther.visibility = View.VISIBLE
                    binding.etProfessionOther.visibility = View.VISIBLE
                    binding.etProfessionOther.clearFocus()
                }else{
                    binding.tvProfessionRoleOther.visibility = View.GONE
                    binding.etProfessionOther.visibility = View.GONE
                }
            }

        })


        // etFullName
        binding.etFullName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.fullNameType = "name"
            } else {
                binding.fullNameType = ""
            }
        }

        binding.etProfessionOther.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.professionOther = "professionRole"
            } else {
                binding.professionOther = ""
            }
        }

        // etShortBio
        binding.etShortBio.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.bioType = "bio"
            } else {
                binding.bioType = ""
            }
        }
        // etShortBio
        binding.etLocation.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.locationName = "locationName"
            } else {
                binding.locationName = ""
            }
        }

        val locationSearchManager = LocationSearchManager(
            context = this,
            identityPoolId = "eu-north-1:c8a8cb6e-799b-43dc-ae59-737224071479",
            region = Regions.EU_NORTH_1,
            placeIndexName = getString(R.string.place_index_name)
        )

        locationSearchManager.setupSearch(
            autoCompleteTextView = binding.etLocation, onResultSelected = { label, coordinate ->
                binding.etLocation.clearFocus()
                // Hide keyboard
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etLocation.windowToken, 0)
                 lat = coordinate.latitude
                 long = coordinate.longitude
            })


    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.profileObserver.observe(this@EditProfileActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    if (type == 1) {
                        showLoading()
                    } else {
                        type = 0
                        hideLoading()
                    }

                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getUserProfile" -> {
                            try {
                                val myDataModel: GetUserProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    binding.bean = myDataModel.data
                                    userIdProof = myDataModel.data.userIdProof.toString()
                                    ownerShipProof = myDataModel.data.onwershipProof.toString()
                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {

                                hideLoading()
                            }
                        }

                        "userUpdateProfile" -> {
                            try {
                                val myDataModel: GetUserProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    showSuccessToast(myDataModel.message.toString())
                                    binding.bean = myDataModel.data
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


    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal = BaseCustomDialog(this@EditProfileActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapter(type)
    }

    /** handle adapter **/
    private fun initAdapter(type: Int) {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {

                    when (type) {
                        1 -> {
                            binding.etAge.setText(m.toString())
                        }

                        2 -> {
                            binding.etGender.setText(m.toString())
                        }

                        3 -> {
                            binding.etProfessionRole.setText(m.toString())
                        }

                        4 -> {
                            binding.etCampus.setText(m.toString())
                        }

                        5 -> {
                            binding.etProvider.setText(m.toString())
                        }
                    }
                    personal?.dismiss()
                }
            }
        }
        when (type) {
            1 -> {
                ageAdapter.list = ageList()
            }

            2 -> {
                ageAdapter.list = genderList()
            }

            3 -> {
                ageAdapter.list = professionRoleList()
            }

            4 -> {
                ageAdapter.list = campusList()
            }

            5 -> {
                ageAdapter.list = providerList()
            }
        }
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    /*** open gallery ***/
    private fun galleryImagePicker() {
        val pictureActionIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                type = "image/*"  // Restrict to images only
            }
        resultLauncherGallery.launch(pictureActionIntent)
    }

    /*** gallery launcher ***/
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageUri?.let {
                    binding.ivPerson.setImageURI(it)
                    multipartImage = convertMultipartPartGal(it)
                }
            }
        }


    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(this@EditProfileActivity.packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(this@EditProfileActivity)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    this@EditProfileActivity, "com.tech.hive.fileProvider", photoFile2!!
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultLauncherCamera.launch(pictureIntent)
            } else {
                Log.d("TAG", "openCameraIntent: ")
            }
        } else {
            Log.d("TAG", "openCameraIntent: ")
        }
    }

    private var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (photoFile2?.exists() == true) {
                    val imagePath = photoFile2?.absolutePath.toString()
                    val imageUri = imagePath.toUri()
                    imageUri.let {
                        multipartImage = convertMultipartPart(it)
                        binding.ivPerson.setImageURI(imageUri)

                    }
                }
            }
        }

    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(this@EditProfileActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "profileImage", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }


    /**** Gallery permission  ***/
    private var allGranted = false
    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (it in permissions.entries) {
                it.key
                val isGranted = it.value
                allGranted = isGranted
            }
            when {
                allGranted -> {
                    galleryImagePicker()
                }

                else -> {
                    showInfoToast("Permission Denied")
                }
            }
        }

    private val permissionResultLauncher1: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                openCameraIntent()
            } else {
                showInfoToast("Permission Denied")
            }
        }


    /** list data add  **/
    private fun professionRoleList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.student),
            getString(R.string.doctor),
            getString(R.string.nurse),
            getString(R.string.caregiver),
            getString(R.string.therapist),
            getString(R.string.physiotherapist),
            getString(R.string.social_worker),
            getString(R.string.teacher),
            getString(R.string.parent),
            getString(R.string.volunteer),
            getString(R.string.other)
        )
    }

    private fun genderList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.men), getString(R.string.women), getString(R.string.other)
        )
    }

    private fun ageList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.age_18_22),
            getString(R.string.age_23_27),
            getString(R.string.age_28_32),
            getString(R.string.age_33_37),
            getString(R.string.age_38_42),
            getString(R.string.age_43_47),
            getString(R.string.age_48_52),
            getString(R.string.age_53_57),
            getString(R.string.age_58_64),
            getString(R.string.age_65_plus)
        )
    }

    private fun campusList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.campus_university_of_milan),
            getString(R.string.campus_university_of_milan_bicocca),
            getString(R.string.campus_polytechnic_university_of_milan),
            getString(R.string.campus_luigi_bocconi_university),
            getString(R.string.campus_catholic_university_sacred_heart),
            getString(R.string.campus_vita_salute_san_raffaele_university),
            getString(R.string.campus_iulm),
            getString(R.string.campus_humanitas_university),
            getString(R.string.campus_brera_academy_of_fine_arts),
            getString(R.string.campus_naba),
            getString(R.string.campus_marangoni_institute),
            getString(R.string.campus_ied)
        )
    }


    private fun providerList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.landlord),
            getString(R.string.tenant_subletting),
            getString(R.string.real_estate_agency),
            getString(R.string.property_owner),
            getString(R.string.current_tenant_leaving_the_room),

            )
    }

}
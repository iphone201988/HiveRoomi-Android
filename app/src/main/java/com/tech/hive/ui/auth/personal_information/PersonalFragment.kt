package com.tech.hive.ui.auth.personal_information

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.AccountVerifyResponse
import com.tech.hive.data.model.UserProfileResponse
import com.tech.hive.databinding.FragmentPersonalBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.ui.auth.personal_information.upload_photo.UploadPhotoFragment
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class PersonalFragment : BaseFragment<FragmentPersonalBinding>() {
    private val viewModel: PersonalFragmentVM by viewModels()
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartImage: MultipartBody.Part? = null
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var selectedType = ""
    override fun getLayoutResource(): Int {
        return R.layout.fragment_personal
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // check state
        binding.fullNameType = ""
        binding.bioType = ""
        binding.linkType = ""
        binding.linkType1 = ""
        // click
        initClick()
        // observer
        initObserver()
      // intent
         selectedType = arguments?.getString("selected_type").toString()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // ivVisiblePassword button click
                R.id.btnContinue -> {
                    if (validate()) {
                        if (Constants.userType == 3) {
                            if (multipartImage!=null){
                                UploadPhotoFragment.multipartImage = multipartImage
                            }
                            val fullName = binding.etFullName.text.toString().trim()
                            val age = binding.etAge.text.toString().trim()
                            val gender = binding.etGender.text.toString().trim().lowercase()
                            val professionRole = binding.etProfessionRole.text.toString().trim()
                            val shortBio = binding.etShortBio.text.toString().trim()
                            val instagramLink = binding.etInsta.text.toString().trim()
                            val campus = binding.etCampus.text.toString().trim()
                            val linkedLink = binding.etLink.text.toString().trim()

                            val bundle = Bundle().apply {
                                putString("selected_type", selectedType)
                                putString("full_name", fullName)
                                putString("age", age)
                                putString("gender", gender)
                                putString("profession_role", professionRole)
                                putString("short_bio", shortBio)
                                putString("instagram_link", instagramLink)
                                putString("campus", campus)
                                putString("linked_link", linkedLink)
                            }

                            BindingUtils.navigateWithSlide(findNavController(), R.id.navigateToUploadPhotoFragment, bundle)
                        } else {
                            personalInformation()
                        }
                    }
                }
                // camera button click
                R.id.ivCamera -> {
                    if (!BindingUtils.hasPermissions(
                            requireContext(), BindingUtils.permissions
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
                            requireContext(), BindingUtils.permissions
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
            }
        }

        // check focusable
        binding.etFullName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.fullNameType = "fullName"
            } else {
                binding.fullNameType = ""
            }
        }
        binding.etShortBio.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.bioType = "bio"
            } else {
                binding.bioType = ""
            }
        }
        binding.etInsta.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.linkType = "link"
            } else {
                binding.linkType = ""
            }
        }
        binding.etLink.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.linkType1 = "link1"
            } else {
                binding.linkType1 = ""
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
            }

        })

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is visible
                Log.d("Keyboard", "Visible")
            } else {
                binding.fullNameType = ""
                binding.bioType = ""
                binding.linkType = ""
                binding.linkType1 = ""
                binding.etInsta.clearFocus()
                binding.etShortBio.clearFocus()
                binding.etFullName.clearFocus()
            }
        }

    }

    /** personal dialog  handel ***/
    private fun personalDialog(type: Int) {
        personal = BaseCustomDialog(requireContext(), R.layout.personal_dialog_item) {

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
        }
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }

    /*** personal information api call ***/
    private fun personalInformation() {
        val data = HashMap<String, RequestBody>()
        val fullName = binding.etFullName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val gender = binding.etGender.text.toString().trim().lowercase()
        val professionRole = binding.etProfessionRole.text.toString().trim()
        val shortBio = binding.etShortBio.text.toString().trim()
        val instagramLink = binding.etInsta.text.toString().trim()
        val campus = binding.etCampus.text.toString().trim()
        val linkedLink = binding.etLink.text.toString().trim()

        if (instagramLink.isNotEmpty()) {
            data["instagram"] = instagramLink.toRequestBody()
        }
        if (linkedLink.isNotEmpty()) {
            data["linkedin"] = linkedLink.toRequestBody()
        }
        if (campus.isNotEmpty()) {
            data["campus"] = campus.toRequestBody()
        }
        data["name"] = fullName.toRequestBody()
        data["gender"] = gender.toRequestBody()
        data["ageRange"] = age.toRequestBody()
        data["profession"] = professionRole.toRequestBody()
        data["bio"] = shortBio.toRequestBody()
        viewModel.userPersonalInformation(data, multipartImage)
    }


    /** api response observer **/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "userPersonalInformation" -> {
                            try {
                                val myDataModel: UserProfileResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    BindingUtils.navigateWithSlide(
                                        findNavController(),
                                        R.id.navigateToAccountCreatedSuccessfulFragment,
                                        null
                                    )
                                }

                            } catch (e: Exception) {
                                Log.e("error", "verifyAccount: $e")
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
        if (pictureIntent.resolveActivity(requireContext().packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(requireContext())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    requireContext(), "com.tech.hive.fileProvider", photoFile2!!
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
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
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

    /*** add validation ***/
    private fun validate(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()
        val professionRole = binding.etProfessionRole.text.toString().trim()
        val shortBio = binding.etShortBio.text.toString().trim()
        binding.etInsta.text.toString().trim()
        if (multipartImage == null) {
            showInfoToast("Please select picture")
            return false
        }
        if (fullName.isEmpty()) {
            showInfoToast("Please enter your name")
            return false
        } else if (age.isEmpty()) {
            showInfoToast("Please select your age")
            return false
        } else if (gender.isEmpty()) {
            showInfoToast("Please select your gender")
            return false
        } else if (professionRole.isEmpty()) {
            showInfoToast("Please select your role")
            return false
        } else if (professionRole.contains("Student") && binding.etCampus.text.toString().trim()
                .isEmpty()
        ) {
            showInfoToast("Please select your campus")
            return false
        } else if (shortBio.isEmpty()) {
            showInfoToast("Please enter your bio")
            return false
        }
        return true
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


}

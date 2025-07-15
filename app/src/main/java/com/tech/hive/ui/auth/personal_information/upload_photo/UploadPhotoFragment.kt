package com.tech.hive.ui.auth.personal_information.upload_photo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.tech.hive.R
import com.tech.hive.base.BaseFragment
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.data.model.UserProfileResponse
import com.tech.hive.databinding.FragmentUploadPhotoBinding
import com.tech.hive.databinding.VideoImagePickerDialogBoxBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class UploadPhotoFragment : BaseFragment<FragmentUploadPhotoBinding>() {
    private val viewModel: UploadPhotoFragmentVM by viewModels()
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var firstMultipartImage: MultipartBody.Part? = null
    private var secondMultipartImage: MultipartBody.Part? = null
    private var whichImage = 0
    private var photoFile2: File? = null
    private var photoURI: Uri? = null

    companion object {
        var multipartImage: MultipartBody.Part? = null
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_upload_photo
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // click
        initClick()
        // observer
        initObserver()
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    findNavController().popBackStack()
                }
                // btnContinue button click
                R.id.btnContinue -> {
                    val selectedType = arguments?.getString("selected_type")
                    val fullName = arguments?.getString("full_name")
                    val age = arguments?.getString("age")
                    val gender = arguments?.getString("gender")
                    val professionRole = arguments?.getString("profession_role")
                    val shortBio = arguments?.getString("short_bio")
                    val instagramLink = arguments?.getString("instagram_link")
                    val campus = arguments?.getString("campus")
                    val linkedLink = arguments?.getString("linked_link")
                    val data = HashMap<String, RequestBody>()

                    if (instagramLink?.isNotEmpty() == true) {
                        data["instagram"] = instagramLink.toRequestBody()
                    }
                    if (linkedLink?.isNotEmpty() == true) {
                        data["linkedin"] = linkedLink.toRequestBody()
                    }
                    if (campus?.isNotEmpty() == true) {
                        data["campus"] = campus.toRequestBody()
                    }
                    data["name"] = fullName.toString().toRequestBody()
                    data["gender"] = gender.toString().toRequestBody()
                    data["ageRange"] = age.toString().toRequestBody()
                    data["profession"] = professionRole.toString().toRequestBody()
                    data["bio"] = shortBio.toString().toRequestBody()
                    data["providerType"] = selectedType.toString().toRequestBody()
                    viewModel.userPersonalInformation(
                        data,
                        multipartImage,
                        firstMultipartImage,
                        secondMultipartImage
                    )

                }
                // open camera
                R.id.tvAdd -> {
                    whichImage = 1
                    imageDialog()
                }
                // open gallery
                R.id.tvAddOwnership -> {
                    whichImage = 2
                    imageDialog()
                }

            }
        }

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

    /**** Edit date and time dialog  handel ***/
    private fun imageDialog() {
        imageDialog = BaseCustomDialog(requireContext(), R.layout.video_image_picker_dialog_box) {
            when (it.id) {
                R.id.tvCamera, R.id.imageCamera -> {
                    if (!BindingUtils.hasPermissions(
                            requireContext(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher1.launch(BindingUtils.permissions)
                    } else {
                        // camera
                        openCameraIntent()
                    }
                    imageDialog!!.dismiss()
                }

                R.id.imageGallery, R.id.tvGallery -> {
                    if (!BindingUtils.hasPermissions(
                            requireContext(), BindingUtils.permissions
                        )
                    ) {
                        permissionResultLauncher.launch(BindingUtils.permissions)

                    } else {
                        galleryImagePicker()

                    }
                    imageDialog!!.dismiss()
                }

            }
        }
        imageDialog!!.create()
        imageDialog!!.show()

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
                    when (whichImage) {
                        1 -> {
                            binding.tvAdd.visibility= View.INVISIBLE
                            binding.icAddImg.visibility= View.VISIBLE
                            firstMultipartImage = convertMultipartPartGal1(it)
                            binding.icAddImg.setImageURI(imageUri)
                        }

                        2 -> {
                            binding.icAddOwnershipImg.visibility= View.VISIBLE
                            binding.tvAddOwnership.visibility= View.INVISIBLE
                            secondMultipartImage = convertMultipartPartGal2(it)
                            binding.icAddOwnershipImg.setImageURI(imageUri)
                        }

                    }


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

    /**** open camera ***/
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

    /*** camera launcher ***/
    private val resultLauncherCamera: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {


                if (photoFile2?.exists() == true) {
                    val imagePath = photoFile2?.absolutePath.toString()
                    val imageUri = imagePath.toUri()
                    imageUri.let {
                        when (whichImage) {
                            1 -> {
                                binding.tvAdd.visibility= View.INVISIBLE
                                binding.icAddImg.visibility= View.VISIBLE
                                firstMultipartImage = convertMultipartPart1(it)
                                binding.icAddImg.setImageURI(imageUri)
                            }

                            2 -> {
                                binding.icAddOwnershipImg.visibility= View.VISIBLE
                                binding.tvAddOwnership.visibility= View.INVISIBLE
                                secondMultipartImage = convertMultipartPart2(it)
                                binding.icAddOwnershipImg.setImageURI(imageUri)
                            }

                        }


                    }
                }
            }
        }


    private fun convertMultipartPart1(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("userIdProof", file.name, requestFile)
    }

    private fun convertMultipartPart2(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("onwershipProof", file.name, requestFile)
    }

    private fun convertMultipartPartGal1(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "userIdProof", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun convertMultipartPartGal2(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(requireActivity(), imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "onwershipProof", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

}

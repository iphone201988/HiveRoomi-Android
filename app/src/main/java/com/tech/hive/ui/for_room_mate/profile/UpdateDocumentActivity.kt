package com.tech.hive.ui.for_room_mate.profile

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.Status
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.base.utils.showSuccessToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.UserIdUpdateResponse
import com.tech.hive.data.model.UserProfileResponse
import com.tech.hive.databinding.ActivityUpdateDocumentBinding
import com.tech.hive.databinding.VideoImagePickerDialogBoxBinding
import com.tech.hive.ui.auth.personal_information.upload_photo.UploadPhotoFragment.Companion.multipartImage
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class UpdateDocumentActivity : BaseActivity<ActivityUpdateDocumentBinding>() {
    private val viewModel: ProfileFragmentVM by viewModels()
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var firstMultipartImage: MultipartBody.Part? = null
    private var secondMultipartImage: MultipartBody.Part? = null
    private var whichImage = 0
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    override fun getLayoutResource(): Int {
        return R.layout.activity_update_document
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        // click
        initClick()
        // observer
        initObserver()
        // get intent data
        val userIdImage = intent.getStringExtra("userIdImage")
        val ownerIdImage = intent.getStringExtra("ownerIdImage")

        if (userIdImage?.isNotEmpty() == true) {
            binding.tvAdd.visibility = View.INVISIBLE
            binding.icAddImg.visibility = View.VISIBLE
            Glide.with(this).load(Constants.BASE_URL_IMAGE + userIdImage)
                .placeholder(R.drawable.progress_animation_small).error(R.drawable.home_dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.icAddImg)
        } else {
            binding.tvAdd.visibility = View.VISIBLE
            binding.icAddImg.visibility = View.INVISIBLE
        }
        if (ownerIdImage?.isNotEmpty() == true) {
            Glide.with(this).load(Constants.BASE_URL_IMAGE + ownerIdImage)
                .placeholder(R.drawable.progress_animation_small).error(R.drawable.home_dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.icAddOwnershipImg)
            binding.icAddOwnershipImg.visibility = View.VISIBLE
            binding.tvAddOwnership.visibility = View.INVISIBLE
        } else {
            binding.icAddOwnershipImg.visibility = View.INVISIBLE
            binding.tvAddOwnership.visibility = View.VISIBLE
        }
    }

    /*** click event handel ***/
    private fun initClick() {
        viewModel.onClick.observe(this@UpdateDocumentActivity) {
            when (it?.id) {
                // back button click
                R.id.ivBack -> {
                    finish()
                }
                // btnContinue button click
                R.id.btnContinue -> {
                    if (firstMultipartImage!=null || secondMultipartImage!=null){
                        viewModel.documentUpdateApiCall(
                            firstMultipartImage,
                            secondMultipartImage
                        )
                    }else{
                        showInfoToast("Please select image")
                    }


                }
                // open camera
                R.id.tvAdd,R.id.icAddImg -> {
                    whichImage = 1
                    imageDialog()
                }
                // open gallery
                R.id.tvAddOwnership ,R.id.icAddOwnershipImg-> {
                    whichImage = 2
                    imageDialog()
                }

            }
        }

    }


    /** api response observer **/
    private fun initObserver() {
        viewModel.profileObserver.observe(this@UpdateDocumentActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "documentUpdateApiCall" -> {
                            try {
                                val myDataModel: UserIdUpdateResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast(myDataModel.message.toString())
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
        imageDialog =
            BaseCustomDialog(this@UpdateDocumentActivity, R.layout.video_image_picker_dialog_box) {
                when (it.id) {
                    R.id.tvCamera, R.id.imageCamera -> {
                        if (!BindingUtils.hasPermissions(
                                this@UpdateDocumentActivity, BindingUtils.permissions
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
                                this@UpdateDocumentActivity, BindingUtils.permissions
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
                            binding.tvAdd.visibility = View.INVISIBLE
                            binding.icAddImg.visibility = View.VISIBLE
                            firstMultipartImage = convertMultipartPartGal1(it)
                            binding.icAddImg.setImageURI(imageUri)
                        }

                        2 -> {
                            binding.icAddOwnershipImg.visibility = View.VISIBLE
                            binding.tvAddOwnership.visibility = View.INVISIBLE
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
        if (pictureIntent.resolveActivity(this@UpdateDocumentActivity.packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(this@UpdateDocumentActivity)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    this@UpdateDocumentActivity, "com.tech.hive.fileProvider", photoFile2!!
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
                                binding.tvAdd.visibility = View.INVISIBLE
                                binding.icAddImg.visibility = View.VISIBLE
                                firstMultipartImage = convertMultipartPart1(it)
                                binding.icAddImg.setImageURI(imageUri)
                            }

                            2 -> {
                                binding.icAddOwnershipImg.visibility = View.VISIBLE
                                binding.tvAddOwnership.visibility = View.INVISIBLE
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
        val file = FileUtil.getTempFile(this@UpdateDocumentActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "userIdProof", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun convertMultipartPartGal2(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(this@UpdateDocumentActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "onwershipProof", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

}
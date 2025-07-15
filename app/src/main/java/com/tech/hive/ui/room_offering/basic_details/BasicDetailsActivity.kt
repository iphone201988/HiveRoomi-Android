package com.tech.hive.ui.room_offering.basic_details

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.data.model.ImageModel
import com.tech.hive.databinding.ActivityBasicDetailsBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.databinding.UploadImageItemViewBinding
import com.tech.hive.databinding.VideoImagePickerDialogBoxBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class BasicDetailsActivity : BaseActivity<ActivityBasicDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: BasicDetailsVM by viewModels()
    private var googleMap: GoogleMap? = null
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var imageMultiplatform: MultipartBody.Part? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var imageList = ArrayList<ImageModel>()
    private var videoUriCover: File? = null
    private var thumbnail: Uri? = null
    // image adapter
    private lateinit var uploadImageAdapter: SimpleRecyclerViewAdapter<ImageModel, UploadImageItemViewBinding>
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
    }

    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@BasicDetailsActivity)
        BindingUtils.statusBarTextColor(this@BasicDetailsActivity, false)
        // check state
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
                    // icAddImg
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "video/*"
                    resultLauncherVideo.launch(intent)
                }
                // btn Next
                R.id.btnContinue -> {
                    if (validate()){
                        HouseholdLifestyleActivity.sendImage = imageList
                        HouseholdLifestyleActivity.sendVideo = videoUriCover
                        val intent = Intent(this@BasicDetailsActivity, PriceTermsActivity::class.java)
                        intent.putExtra("roomType",binding.etRoom.text.toString().trim())
                        intent.putExtra("titleType",binding.etTitle.text.toString().trim())
                        intent.putExtra("bioType",binding.etShortBio.text.toString().trim())
                        intent.putExtra("locationType",binding.etLocation.text.toString().trim())
                        startActivity(intent)
                    }

                }

                R.id.ivBack -> {
                    onBackPressedDispatcher.onBackPressed()
                }

                R.id.etRoom, R.id.ivRoom -> {
                    personalDialog()
                }


            }

        }

        // etTitle
        binding.etTitle.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.titleType = "titleType"
            } else {
                binding.titleType = ""
            }
        }


        // etDesc
        binding.etShortBio.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.bioType = "bioType"
            } else {
                binding.bioType = ""
            }
        }

        // etLocation
        binding.etLocation.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.locationType = "locationType"
            } else {
                binding.locationType = ""
            }
        }


    }


    /** handle adapter **/
    private fun initAdapter() {
        uploadImageAdapter =
            SimpleRecyclerViewAdapter(R.layout.upload_image_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clImage, R.id.tvImage -> {
                        imageDialog()
                    }
                }
            }
        uploadImageAdapter.list = getList()
        // images Adapter
        binding.rvImages.adapter = uploadImageAdapter
    }

    // list
    private fun getList(): ArrayList<ImageModel> {
        imageList = ArrayList<ImageModel>()
        imageList.add(ImageModel("image", 2))
        return imageList
    }

    /**map handling **/
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.uiSettings?.isMapToolbarEnabled = true
    }


    /** personal dialog  handel ***/
    private fun personalDialog() {
        personal = BaseCustomDialog(this@BasicDetailsActivity, R.layout.personal_dialog_item) {

        }
        personal!!.create()
        personal!!.show()
        // adapter
        initAdapterRoom()
    }

    /** handle adapter **/
    private fun initAdapterRoom() {
        ageAdapter = SimpleRecyclerViewAdapter(R.layout.un_pin_layout, BR.bean) { v, m, pos ->
            when (v?.id) {
                R.id.consMainUnPin -> {
                    binding.etRoom.setText(m.toString())
                    personal?.dismiss()
                }
            }
        }
        ageAdapter.list = roomTypeList()
        personal?.binding?.rvPersonal?.adapter = ageAdapter
    }


    private fun roomTypeList(): ArrayList<String> {
        return arrayListOf(
            getString(R.string.single),
            getString(R.string.doubles),
            getString(R.string.studios),
            getString(R.string.apartment)
        )
    }


    private var resultLauncherVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val fileUri = data?.data
                if (fileUri != null) {
                    binding.icAddImg.visibility = View.VISIBLE
                    Glide.with(this).load(fileUri).into(binding.icAddImg)
                    val documentFile = DocumentFile.fromSingleUri(this, fileUri)
                    documentFile?.let { docFile ->
                        val videoInputStream = contentResolver.openInputStream(docFile.uri)
                        videoInputStream?.let { inputStream ->
                            videoUriCover = File(cacheDir, docFile.name ?: "video.mp4")
                            videoUriCover!!.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    }
//                    if (videoUriCover != null) {
//                        val requestFile =
//                            videoUriCover!!.asRequestBody("video/*".toMediaTypeOrNull())
//                        val body: MultipartBody.Part = MultipartBody.Part.createFormData(
//                            "uploadVideos",
//                            videoUriCover!!.name,
//                            requestFile
//                        )
//                        videoMultiplatform = body
//
//
//                    }
//                    val thumbNailBitmap = createVideoThumb(this@BasicDetailsActivity, fileUri)
//
//                    if (thumbNailBitmap != null) {
//                        thumbnail = bitmapToUri(this@BasicDetailsActivity, thumbNailBitmap)
//                    }


                }
            }
        }

    /** Create video thumbnail using content resolver (Android 10+) or fallback to older methods **/
    private fun createVideoThumb(context: Context, uri: Uri): Bitmap? {
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, uri)
            return mediaMetadataRetriever.frameAtTime
        } catch (ex: Exception) {
            Toast.makeText(context, "Error retrieving bitmap", Toast.LENGTH_SHORT).show()
        }
        return null

    }

    /** convert bitmap in uri **/
    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, "temp_image.jpg")
        try {
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    /**** Edit date and time dialog  handel ***/
    private fun imageDialog() {
        imageDialog =
            BaseCustomDialog(this@BasicDetailsActivity, R.layout.video_image_picker_dialog_box) {
                when (it.id) {
                    R.id.tvCamera, R.id.imageCamera -> {
                        if (!BindingUtils.hasPermissions(
                                this@BasicDetailsActivity, BindingUtils.permissions
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
                                this@BasicDetailsActivity, BindingUtils.permissions
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
                    imageList.removeAll { it.type == 2 }

                    imageList.add(ImageModel(it.toString(), 1))
                    imageList.add(ImageModel("image", 2))
                    uploadImageAdapter.list = imageList
                    uploadImageAdapter.notifyDataSetChanged()

                    imageMultiplatform = convertMultipartPartGal(it)
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
        if (pictureIntent.resolveActivity(this@BasicDetailsActivity.packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(this@BasicDetailsActivity)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    this@BasicDetailsActivity, "com.tech.hive.fileProvider", photoFile2!!
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
                        imageList.removeAll { it.type == 2 }
                        imageList.add(ImageModel(it.toString(), 1))
                        imageList.add(ImageModel("image", 2))
                        uploadImageAdapter.list = imageList
                        uploadImageAdapter.notifyDataSetChanged()
                        imageMultiplatform = convertMultipartPart(it)
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
        return MultipartBody.Part.createFormData("uploadImages", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(this@BasicDetailsActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "uploadImages", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }


    /*** add validation ***/
    private fun validate(): Boolean {
        val room = binding.etRoom.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etShortBio.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        if (room.isEmpty()) {
            showInfoToast("Please select room type")
            return false
        }  else if (title.isEmpty()) {
            showInfoToast("Please enter title")
            return false
        }
        else if (desc.isEmpty()) {
            showInfoToast("Please enter description")
            return false
        } else if (location.isEmpty()) {
            showInfoToast("Please enter address")
            return false
        }
        else if (imageList.size < 2){
            showInfoToast("Please select images")
            return false
        }
        else if (videoUriCover == null){
            showInfoToast("Please select video")
            return false
        }
        return true
    }


}
package com.tech.hive.ui.room_offering.basic_details

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
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
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.regions.Regions
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.util.FileUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tech.hive.BR
import com.tech.hive.R
import com.tech.hive.base.BaseActivity
import com.tech.hive.base.BaseViewModel
import com.tech.hive.base.SimpleRecyclerViewAdapter
import com.tech.hive.base.location.LocationSearchManager
import com.tech.hive.base.utils.AppUtils
import com.tech.hive.base.utils.BaseCustomDialog
import com.tech.hive.base.utils.BindingUtils
import com.tech.hive.base.utils.showErrorToast
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetListingData
import com.tech.hive.data.model.ImageModel
import com.tech.hive.databinding.ActivityBasicDetailsBinding
import com.tech.hive.databinding.PersonalDialogItemBinding
import com.tech.hive.databinding.UnPinLayoutBinding
import com.tech.hive.databinding.UploadImageItemViewBinding
import com.tech.hive.databinding.VideoImagePickerDialogBoxBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.Collections

@AndroidEntryPoint
class BasicDetailsActivity : BaseActivity<ActivityBasicDetailsBinding>(), OnMapReadyCallback {
    private val viewModel: BasicDetailsVM by viewModels()
    private var googleMap: GoogleMap? = null
    private var imageDialog: BaseCustomDialog<VideoImagePickerDialogBoxBinding>? = null
    private var personal: BaseCustomDialog<PersonalDialogItemBinding>? = null
    private lateinit var ageAdapter: SimpleRecyclerViewAdapter<String, UnPinLayoutBinding>
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var videoUriCover: File? = null
    private var videoUriData: String = ""
    private var listingData: GetListingData? = null
    private var lat: Double? = null
    private var long: Double? = null
    private var videoDelete = ""
    private var imageList = mutableListOf<ImageModel>()
    private var clickedImageModel: ImageModel? = null
    var deleteImage = mutableListOf<String>()

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
        binding.mapView.setOnTouchListener({ v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        })
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
        // get intent data
        listingData = intent.getParcelableExtra<GetListingData>("basicDetail")
        listingData?.let {
            binding.etRoom.setText(it.roomType?.replaceFirstChar { c -> c.uppercase() })
            binding.etTitle.setText(it.title?.replaceFirstChar { c -> c.uppercase() })
            binding.etShortBio.setText(it.description?.replaceFirstChar { c -> c.uppercase() })
            binding.etLocation.setText(it.address)

            imageList.removeAll { it.type == 2 }

            it.images?.filterNotNull()?.forEach { imageUrl ->
                imageList.add(ImageModel(imageUrl, 3))
            }

            imageList.add(ImageModel("image", 2))
            uploadImageAdapter.list = imageList
            uploadImageAdapter.notifyDataSetChanged()


            val styleUrl =
                "https://api.maptiler.com/maps/${Constants.MAP_ID}/style.json?key=${Constants.MAP_API_KEY}"

            binding.mapView.getMapAsync { mapboxMap ->
                mapboxMap.setStyle(styleUrl) { style ->
                    mapboxMap.uiSettings.isScrollGesturesEnabled = false
                    mapboxMap.uiSettings.isZoomGesturesEnabled = false // (optional)
                    mapboxMap.uiSettings.isRotateGesturesEnabled = false // (optional)
                    mapboxMap.uiSettings.isTiltGesturesEnabled = false
                    mapboxMap.uiSettings.areAllGesturesEnabled()
                    val latitude = listingData!!.latitude ?: 0.0
                    val longitude = listingData!!.longitude ?: 0.0

                    Log.d("dgdfggf", "onCreateView: $latitude ,$longitude")

                    val latLng = LatLng(latitude, longitude)
                    mapboxMap.cameraPosition =
                        CameraPosition.Builder().target(latLng).zoom(10.0).build()
                    mapboxMap.addMarker(
                        com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latLng)
                            .title(getString(R.string.map_name))
                    )
                }
            }

            videoUriData = it.videos.toString()

            if (it.videos?.isEmpty() == true) {
                binding.tvVideo.visibility = View.VISIBLE
                binding.icAddImg.visibility = View.GONE
                videoDelete = ""
            } else {
                binding.tvVideo.visibility = View.INVISIBLE
                binding.icAddImg.visibility = View.VISIBLE
                Glide.with(binding.icAddImg.context).load(Constants.BASE_URL_IMAGE + it.videos)
                    .into(binding.icAddImg)

                videoDelete = it.videos.toString()
            }
        }

        // Make keyboard resize layout
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // set status bar color
        BindingUtils.statusBarStyle(this@BasicDetailsActivity)

        // Keyboard insets listener to avoid send button getting hidden
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.setPadding(0, 0, 0, imeHeight)
            insets
        }
    }

    /** handle clicks **/
    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                // btnVideo
                R.id.tvVideo,R.id.icAddImg -> {
                    // icAddImg
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "video/*"
                    resultLauncherVideo.launch(intent)
                }
                // btn Next
                R.id.btnContinue -> {
                    if (validate()) {
                        if (imageList.isNotEmpty()){
                            HouseholdLifestyleActivity.deleteImage.clear()
                            HouseholdLifestyleActivity.deleteImage = deleteImage
                            HouseholdLifestyleActivity.changePosition.clear()
                            for (i in imageList) {
                                if (i.type!=2){
                                    HouseholdLifestyleActivity.changePosition.add(Constants.BASE_URL_IMAGE + i.image)
                                }
                            }
                        }
                        HouseholdLifestyleActivity.sendImage.clear()
                        HouseholdLifestyleActivity.sendImage = imageList
                        HouseholdLifestyleActivity.sendVideo = videoUriCover
                        HouseholdLifestyleActivity.videoUrl = videoDelete
                        val intent = Intent(this@BasicDetailsActivity, PriceTermsActivity::class.java)
                        intent.putExtra("roomType", binding.etRoom.text.toString().trim())
                        intent.putExtra("titleType", binding.etTitle.text.toString().trim())
                        intent.putExtra("bioType", binding.etShortBio.text.toString().trim())
                        intent.putExtra("locationType", binding.etLocation.text.toString().trim())
                        if (listingData != null) {
                            intent.putExtra("basicDetail", listingData)
                        }
                        startActivity(intent)
                    }

                }

                R.id.ivBack -> {
                    finish()
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


                val styleUrl =
                    "https://api.maptiler.com/maps/${Constants.MAP_ID}/style.json?key=${Constants.MAP_API_KEY}"

                binding.mapView.getMapAsync { mapboxMap ->
                    mapboxMap.setStyle(styleUrl) { style ->
                        mapboxMap.uiSettings.isScrollGesturesEnabled = false
                        mapboxMap.uiSettings.isZoomGesturesEnabled = false // (optional)
                        mapboxMap.uiSettings.isRotateGesturesEnabled = false // (optional)
                        mapboxMap.uiSettings.isTiltGesturesEnabled = false
                        mapboxMap.uiSettings.areAllGesturesEnabled()
                        val latitude = lat ?: 0.0
                        val longitude = long ?: 0.0


                        val latLng = LatLng(latitude, longitude)
                        mapboxMap.cameraPosition =
                            CameraPosition.Builder().target(latLng).zoom(10.0).build()
                        mapboxMap.addMarker(
                            com.mapbox.mapboxsdk.annotations.MarkerOptions().position(latLng)
                                .title(getString(R.string.map_name))
                        )
                    }
                }
            })


    }


    /** handle adapter **/
    private fun initAdapter() {
        uploadImageAdapter =
            SimpleRecyclerViewAdapter(R.layout.upload_image_item_view, BR.bean) { v, m, pos ->
                when (v.id) {
                    R.id.clImage, R.id.tvImage -> {
                        clickedImageModel = m
                        imageDialog()
                    }
                }
            }
        uploadImageAdapter.list = getList()
        // images Adapter
        binding.rvImages.adapter = uploadImageAdapter
        // Enable drag & drop
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvImages)
    }


    private val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
        ): Int {
            val position = viewHolder.bindingAdapterPosition
            return if (imageList[position].type == 2) {
                0 // Don't allow dragging add button
            } else {
                makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                    0 // Disable swipe
                )
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.bindingAdapterPosition
            val toPos = target.bindingAdapterPosition

            // Don't allow dropping into the + icon
            if (imageList[toPos].type == 2 || imageList[fromPos].type == 2) return false

            Collections.swap(imageList, fromPos, toPos)
            uploadImageAdapter.notifyItemMoved(fromPos, toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // No swipe actions
        }

        override fun isLongPressDragEnabled(): Boolean = true
    }


    // list
    private fun getList(): ArrayList<ImageModel> {
        imageList = ArrayList<ImageModel>()
        imageList.add(ImageModel("image", 2))
        return imageList as ArrayList<ImageModel>
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
//    private var resultLauncherGallery =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val data: Intent? = result.data
//                val imageUri = data?.data
//
//                imageUri?.let { uri ->
//                    val uriString = uri.toString()
//                    val newImage = ImageModel(uriString, 1)
//
//                    // Prevent duplicate
//                    if (addedImageUris.contains(uriString)) {
//                        Toast.makeText(this@BasicDetailsActivity, "This image is already added", Toast.LENGTH_SHORT).show()
//                        return@let
//                    }
//
//                    clickedImageModel?.let { clickedItem ->
//                        val index = imageList.indexOf(clickedItem)
//
//                        if (index != -1) {
//                            if (imageList[index].type == 2) {
//                                imageList.add(imageList.size - 1, newImage)
//                            } else {
//                                val oldUri = imageList[index].image
//
//                                addedImageUris.remove(oldUri)
//                                imageMultiplatform.removeAll { it.first == oldUri }
//
//                                imageList[index] = newImage
//                            }
//
//                            // Keep "+" at end
//                            imageList.removeAll { it.type == 2 }
//                            imageList.add(ImageModel("image", 2))
//
//                            uploadImageAdapter.list = imageList
//                            uploadImageAdapter.notifyDataSetChanged()
//
//                            // Add new image
//                            addedImageUris.add(uriString)
//                            convertMultipartPartGal(uri).let { part ->
//                                imageMultiplatform.add(uriString to part)
//                            }
//
//                            // Logs
//                            val realCount = imageList.count { it.type == 1 }
//                            Log.d("gfdfgfgfgf", ": multipart $imageMultiplatform")
//                            Log.d("gfdfgfgfgf", ": adapter ${realCount + 1}")
//                        }
//                    }
//
//                    clickedImageModel = null
//                }
//            }
//        }
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data

                imageUri?.let { uri ->
                        try {
                            val multipartPart = convertMultipartPartGal(uri)

                            val newImage = ImageModel(
                                uri.toString(), 1, mutableListOf(uri.toString() to multipartPart)
                            )

                            clickedImageModel?.let { clickedItem ->
                                val index = imageList.indexOf(clickedItem)

                                if (index != -1) {
                                    if (imageList[index].type == 2) {
                                        imageList.add(imageList.size - 1, newImage)
                                    } else {
                                        deleteImage.add(imageList[index].image)
                                        imageList[index] = newImage
                                    }

                                    imageList.removeAll { it.type == 2 }
                                    imageList.add(ImageModel("image", 2))

                                    uploadImageAdapter.list = imageList
                                    uploadImageAdapter.notifyDataSetChanged()

                                    for (i in uploadImageAdapter.list) {
                                        Log.d("dfgdfdfgfd", ":${i.imageMultiplatform}")
                                    }
                                }
                            }

                            clickedImageModel = null
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showErrorToast("Image compression failed")
                        }

                }
            }
        }

    /*    private var resultLauncherGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri = data?.data
                      imageUri?.let { uri ->
                       var multipartImage  = convertMultipartPartGal(uri).let { part ->
                              imageMultiplatform.add(uri.toString() to part)
                          }
                          val newImage = ImageModel(uri.toString(), 1,multipartImage)

                          clickedImageModel?.let { clickedItem ->
                              val index = imageList.indexOf(clickedItem)

                              if (index != -1) {
                                  if (imageList[index].type == 2) {
                                      // Insert before last item
                                      imageList.add(imageList.size - 1, newImage)
                                  } else {
                                      imageList[index] = newImage
                                  }

                                  // Ensure only one "+" icon at end
                                  imageList.removeAll { it.type == 2 }
                                  imageList.add(ImageModel("image", 2))

                                  uploadImageAdapter.list = imageList
                                  uploadImageAdapter.notifyDataSetChanged()

                              }
                          }

                          clickedImageModel = null
                      }

                }
            }*/
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
    private val resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (photoFile2?.exists() == true) {
                    val imagePath = photoFile2?.absolutePath.toString()
                    val imageUri = imagePath.toUri()

                    lifecycleScope.launch {
                        try {
                            val multipartPart = convertMultipartPart(imageUri)

                            multipartPart?.let { part ->

                                val newImage = ImageModel(
                                    imageUri.toString(),
                                    1,
                                    mutableListOf(imageUri.toString() to part)
                                )

                                clickedImageModel?.let { clickedItem ->
                                    val index = imageList.indexOf(clickedItem)

                                    if (index != -1) {
                                        if (imageList[index].type == 2) {
                                            imageList.add(imageList.size - 1, newImage)
                                        } else {
                                            imageList[index] = newImage
                                        }

                                        imageList.removeAll { it.type == 2 }
                                        imageList.add(ImageModel("image", 2))

                                        uploadImageAdapter.list = imageList
                                        uploadImageAdapter.notifyDataSetChanged()
                                    }

                                    clickedImageModel = null
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showErrorToast("Compression fail")
                        }
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


    /** video launcher **/
    private val resultLauncherVideo =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val fileUri = data?.data
                if (fileUri != null) {
                    binding.icAddImg.visibility = View.VISIBLE

                    // Show a preview frame from the video using Glide
                    Glide.with(this).load(fileUri).frame(1000000)
                        .into(binding.icAddImg)

                    val documentFile = DocumentFile.fromSingleUri(this, fileUri)
                    documentFile?.let { docFile ->
                        contentResolver.openInputStream(docFile.uri)?.use { inputStream ->
                            val targetFile = File(cacheDir, docFile.name ?: "video.mp4")

                            // Copy video stream with small buffer to avoid OOM
                            targetFile.outputStream().use { outputStream ->
                                val buffer = ByteArray(4096)
                                var bytesRead: Int
                                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                }
                            }

                            videoUriCover = targetFile
                            videoUriData = "video_ready"
                        } ?: run {
                            showInfoToast("Unable to read selected video.")
                        }
                    } ?: showInfoToast("Invalid video file.")
                }
            }
        }


    /*    private var resultLauncherVideo =
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
                                if (videoUriData!=null){
                                    videoUriData = "fssdfdfs"
                                }else{
                                    videoUriData = ""
                                }
                            }
                        }
                    }
                }
            }*/


    /*** add validation ***/
    private fun validate(): Boolean {
        val room = binding.etRoom.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etShortBio.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        if (room.isEmpty()) {
            showInfoToast("Please select room type")
            return false
        } else if (title.isEmpty()) {
            showInfoToast("Please enter title")
            return false
        } else if (desc.isEmpty()) {
            showInfoToast("Please enter description")
            return false
        } else if (location.isEmpty()) {
            showInfoToast("Please enter address")
            return false
        } else if (imageList.size < 4) {
            showInfoToast("Please select minimum 3 images")
            return false
        } else if (videoUriData.isEmpty()) {
            showInfoToast("Please select video")
            return false
        }
        return true
    }


}
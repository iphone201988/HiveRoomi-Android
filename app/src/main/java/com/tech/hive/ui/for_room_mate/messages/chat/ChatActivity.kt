package com.tech.hive.ui.for_room_mate.messages.chat

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
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
import com.tech.hive.data.model.CommonResponse
import com.tech.hive.data.model.GetUserData
import com.tech.hive.data.model.GetUserMessageResponse
import com.tech.hive.data.model.UploadImageResponse
import com.tech.hive.databinding.ActivityChatBinding
import com.tech.hive.databinding.CalenderDialogItemBinding
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val viewModel: ChatActivityVM by viewModels()
    private var calenderDialog: BaseCustomDialog<CalenderDialogItemBinding>? = null
    private lateinit var chatAdapter: ChatAdapter
    private var userId: String? = null
    private var photoFile2: File? = null
    private var photoURI: Uri? = null
    private var multipartImage: MultipartBody.Part? = null
    private var socketId: String? = null
    private var currentPage = 1
    private var filetype = ""
    private lateinit var socket: Socket


    override fun getLayoutResource(): Int {
        return R.layout.activity_chat
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        var shareData = sharedPrefManager.getLoginData()
        if (shareData?._id?.isNotEmpty() == true) {
            Constants.userId = shareData._id.toString()
            // connect Socket
            socketHandle(shareData._id)
            Log.d("gfdgfgfg", "onCreateView:${shareData._id} ")
        }
        // view
        initView()
        // click
        initOnClick()
        // observer
        initObserver()
    }


    private fun socketHandle(userId: String?) {
        try {
            val options = IO.Options()
            options.query = "userId=$userId"
            options.reconnection = true
            options.reconnectionAttempts = 5
            options.reconnectionDelay = 2000

            socket = IO.socket(Constants.SOCKET_URL, options)
            socket.connect()
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("Sockets", "✅ Socket Connected!")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d("Sockets", "✅ Socket Disconnected!")

            }

            socket.on("message") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject

                    try {
                        val messageData: GetUserData? = BindingUtils.parseJson(data.toString())
                        if (messageData != null) {
                            val chatData = convertToChatHistoryApiResponseData(messageData)
                            // Add the new message to the chat adapter and refresh the RecyclerView
                            runOnUiThread {
                                chatAdapter.addData(chatData)
                                chatAdapter.notifyDataSetChanged()
                                binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                            }


                        } else {
                            Log.e("newMessage", "Failed to parse message data")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }


        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }


    }

    fun convertToChatHistoryApiResponseData(messageDataResponse: GetUserData): GetUserData {
        return GetUserData(
            _id = messageDataResponse._id,
            content = messageDataResponse.content,
            contentType = messageDataResponse.contentType,
            createdAt = messageDataResponse.createdAt,
            isRead = messageDataResponse.isRead,
            sender = messageDataResponse.sender,
        )
    }


    // sendMessage
    fun sendMessage(message: String) {
        val messageObject = JSONObject()
        messageObject.put("receiver", socketId)
        messageObject.put("content", message)
        messageObject.put("contentType", "text")

        socket.emit("message", messageObject)
        Log.d("Sockets", "sendMessage: $messageObject")
    }

    // sendImage
    fun sendFileMessage(fileUrl: String, receiverId: String) {
        val messageObject = JSONObject()
        messageObject.put("receiver", receiverId)
        messageObject.put("content", fileUrl)
        messageObject.put("contentType", filetype)

        socket.emit("message", messageObject)
    }

    /** handle api response **/
    private fun initObserver() {
        viewModel.chatObserver.observe(this@ChatActivity) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getChatWithIdApi" -> {
                            try {
                                val myDataModel: GetUserMessageResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    binding.bean = myDataModel.otherUser
                                    chatAdapter.setList(myDataModel.data.reversed() as List<GetUserData>?)

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "sendImageAPi" -> {
                            try {
                                val myDataModel: UploadImageResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel?.data != null) {
                                    if (myDataModel.data.url?.isNotEmpty() == true) {
                                        sendFileMessage(myDataModel.data.url, socketId.toString())
                                    }

                                }
                            } catch (e: Exception) {
                                Log.e("error", "getHomeApi: $e")
                            } finally {
                                hideLoading()
                            }
                        }

                        "userBlockAPi" -> {
                            try {
                                val myDataModel: CommonResponse? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (myDataModel != null) {
                                    showSuccessToast("user block successfully")
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


    /** handle view **/
    private fun initView() {
        // set status bar color
        BindingUtils.statusBarStyle(this@ChatActivity)
        BindingUtils.statusBarTextColor(this@ChatActivity, false)
        // intent
        userId = intent.getStringExtra("chatId")
        socketId = intent.getStringExtra("socketId")
        if (userId?.isNotEmpty() == true) {
            val data = HashMap<String, String>()
            data["chatId"] = userId.toString()
            data["page"] = currentPage.toString()
            data["limit"] = "10"
            viewModel.getChatWithIdApi(Constants.CHAT_MESSAGE, data)
        }

        // adapter initialize
        chatAdapter = ChatAdapter()
        binding.rvChat.adapter = chatAdapter

    }

    /*** all click event handle here ***/
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(this@ChatActivity) {
            when (it?.id) {
                // dot button click
                R.id.ivDot -> {
                    if (binding.clEdit.visibility == View.VISIBLE) {
                        binding.clEdit.visibility = View.GONE
                    } else {
                        binding.clEdit.visibility = View.VISIBLE
                    }
                }
                // user block
                R.id.tvEditBlog -> {
                    val data = HashMap<String, Any>()
                    data["id"] = socketId.toString()
                    viewModel.userBlockAPi(data, Constants.USER_BLOCK)
                }
                // user feedback
                R.id.tvDeleteBlog -> {

                }

                // add button click
                R.id.ivAdd -> {
                    if (binding.ivCategory.visibility == View.GONE) {
                        fadeInAnimation(binding.ivCategory)
                    } else {
                        fadeOutAnimation(binding.ivCategory)

                    }
                }

                // calender icon click
                R.id.ivCalender -> {
                    // dialog
                    initDialog()
                }


                // send message
                R.id.ivSend -> {
                    val message = binding.etEmail.text.toString().trim()
                    if (message.isEmpty()) {
                        showInfoToast("Please enter any message")
                    } else {
                        sendMessage(message)
                        // Clear input
                        binding.etEmail.setText("")
                        // Scroll to bottom
                        binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        }

        binding.ivCategory.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val totalHeight = v.height
                val sectionHeight = totalHeight / 3
                val y = event.y.toInt()

                when {
                    y < sectionHeight -> {
                        if (!BindingUtils.hasPermissions(
                                this@ChatActivity, BindingUtils.permissions
                            )
                        ) {
                            permissionResultLauncher.launch(BindingUtils.permissions)

                        } else {
                            galleryImagePicker()

                        }
                    }

                    y < sectionHeight * 2 -> {
                        if (!BindingUtils.hasPermissions(
                                this@ChatActivity, BindingUtils.permissions
                            )
                        ) {
                            permissionResultLauncher.launch(BindingUtils.permissions)

                        } else {
                            openPdfFile()

                        }

                    }

                    else -> {
                        if (!BindingUtils.hasPermissions(
                                this@ChatActivity, BindingUtils.permissions
                            )
                        ) {
                            permissionResultLauncher1.launch(BindingUtils.permissions)
                        } else {
                            // camera
                            openCameraIntent()
                        }
                    }
                }
            }
            true
        }


    }

    private fun openPdfFile() {
        pdfPickerLauncher.launch(arrayOf("application/pdf"))
    }

    private val pdfPickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                uri.let {
                    uploadPdfFile(it)
//                    multipartImage = convertMultipartPartGal(it)
//                    if (multipartImage != null) {
//                        filetype = "document"
//                        viewModel.sendImageAPi(Constants.USER_UPLOAD, multipartImage)
//                    }

                }
            } else {
                Log.d("PDFPicker", "No PDF selected")
            }
        }


    private fun uploadPdfFile(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return  // Stop here safely
            }

            val tempFile = File.createTempFile("upload", ".pdf", cacheDir)
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val multipartBody =
                MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            if (true) {
                filetype = "document"
                viewModel.sendImageAPi(Constants.USER_UPLOAD, multipartBody)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to process PDF", Toast.LENGTH_SHORT).show()
        }
    }

    /*** open gallery ***/
    private fun galleryImagePicker() {
        val pictureActionIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                type = "image/*"
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
                    multipartImage = convertMultipartPartGal(it)
                    if (multipartImage != null) {
                        filetype = "file"
                        viewModel.sendImageAPi(Constants.USER_UPLOAD, multipartImage)
                    }

                }
            }
        }

    private fun openCameraIntent() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(packageManager) != null) {
            try {
                photoFile2 = AppUtils.createImageFile1(this@ChatActivity)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile2 != null) {
                photoURI = FileProvider.getUriForFile(
                    this@ChatActivity, "com.tech.hive.fileProvider", photoFile2!!
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
                        if (multipartImage != null) {
                            filetype = "file"
                            viewModel.sendImageAPi(Constants.USER_UPLOAD, multipartImage)
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
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun convertMultipartPartGal(imageUri: Uri): MultipartBody.Part {
        val file = FileUtil.getTempFile(this@ChatActivity, imageUri)
        val fileName =
            "${file!!.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "file", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
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

    /** dialog **/
    private fun initDialog() {
        calenderDialog = BaseCustomDialog(this@ChatActivity, R.layout.calender_dialog_item) {
            when (it?.id) {
                R.id.tvCancel -> {
                    calenderDialog?.dismiss()
                }

                R.id.tvLogout -> {
                    calenderDialog?.dismiss()
                }

                // time button  click
                R.id.tvTimes -> {
                    val calendar = Calendar.getInstance()
                    val hour = calendar[Calendar.HOUR_OF_DAY]
                    val minute = calendar[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(
                        this@ChatActivity, { view, hourOfDay, minute ->
                            calenderDialog?.binding?.tvTimes?.text = String.format(
                                "%02d:%02d", hourOfDay, minute
                            )

                        }, hour, minute, true
                    ) // 'true' for 24-hour format

                    timePickerDialog.show()
                }
            }

        }
        calenderDialog?.setCancelable(false)
        calenderDialog?.create()
        calenderDialog?.show()


        var calendar = Calendar.getInstance()
        calenderDialog?.binding?.rangeCalenderOneTime?.setDate(calendar)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentMonthName = monthFormat.format(calendar.time)
        val currentYear = yearFormat.format(calendar.time)
        calenderDialog?.binding?.tvMonth?.text = currentMonthName
        calenderDialog?.binding?.tvYear?.text = currentYear

        calenderDialog?.binding?.rangeCalenderOneTime?.setOnPreviousPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                calendar = calenderDialog?.binding?.rangeCalenderOneTime?.currentPageDate!!
                calendar.set(Calendar.DAY_OF_MONTH, 1)

                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)

                calenderDialog?.binding?.tvMonth?.text = monthName
                calenderDialog?.binding?.tvYear?.text = year

            }
        })

        calenderDialog?.binding?.rangeCalenderOneTime?.setOnForwardPageChangeListener(object :
            OnCalendarPageChangeListener {
            override fun onChange() {
                calendar = calenderDialog?.binding?.rangeCalenderOneTime?.currentPageDate!!
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthName = monthFormat.format(calendar.time)
                val year = yearFormat.format(calendar.time)
                calenderDialog?.binding!!.tvMonth.text = monthName
                calenderDialog?.binding!!.tvYear.text = year

            }
        })


        calenderClick()

    }

    private fun calenderClick() {
        calenderDialog?.binding?.rangeCalenderOneTime?.setOnDayClickListener(object :
            OnDayClickListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar.time
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val startDate1 = clickedDayCalendar.time
                val strStartDate: String = sdf.format(startDate1)
                Log.d("fgdfgfgdg", "onDayClick: $strStartDate")
            }
        })
    }

    fun disconnectSocket() {
        if (this::socket.isInitialized) {
            socket.disconnect()
            Log.d("Sockets", "Socket Disconnected Manually")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectSocket()
    }

}

/*** Fades out a view smoothly and hides it when the animation ends.*/
private fun fadeOutAnimation(view: View) {
    view.animate().alpha(0f).setDuration(1000).withEndAction { view.visibility = View.GONE }
}

/*** Fades in a view smoothly and makes it visible */
private fun fadeInAnimation(view: View) {
    view.apply {
        visibility = View.VISIBLE
        alpha = 0f
        animate().alpha(1f).setDuration(1000)
    }
}




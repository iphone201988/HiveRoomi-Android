package com.tech.hive.ui.for_room_mate.messages.chat


import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.GetUserData
import com.tech.hive.databinding.ChatSenderItemBinding
import com.tech.hive.databinding.ReciverChatItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = ArrayList<GetUserData>()

    companion object {
        private const val VIEW_TYPE_SENDER = 0
        private const val VIEW_TYPE_RECEIVE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                ConversationRightViewHolder(
                    ChatSenderItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            VIEW_TYPE_RECEIVE -> {
                ConversationLeftViewHolder(
                    ReciverChatItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    fun addMessage(message: GetUserData) {
        listItem.add(message)
        notifyItemInserted(listItem.size - 1)
    }

    fun getMessages(): List<GetUserData> = listItem

    override fun getItemCount(): Int {
        return listItem.size
    }

    fun addData(data: GetUserData) {
        val positionStart: Int = listItem.size
        listItem.add(data)
        notifyItemInserted(positionStart)
    }

    fun setList(newDataList: List<GetUserData>?) {
        listItem.clear()
        if (newDataList != null) listItem.addAll(newDataList)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listItem[position]

        if (Constants.userId == item.sender?._id) {
            (holder as ConversationRightViewHolder).binding.apply {
                tvSend.text = item.content
                Glide.with(ivImageSender.context).load(Constants.BASE_URL_IMAGE + item.content)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImageSender)

                val isText = item.contentType?.contains("text") == true
                val image = item.contentType?.contains("file") == true
                val document = item.contentType?.contains("document") == true

                if (image) {
                    ivImageSender.visibility = View.VISIBLE
                    ivHuk.visibility = View.GONE
                    tvSend.visibility = View.GONE
                    ivPdf1.visibility = View.GONE
                    clRight.visibility = View.GONE
                    ivPdf.visibility = View.GONE
                }

                if (isText) {
                    ivImageSender.visibility = View.GONE
                    ivPdf.visibility = View.GONE
                    ivPdf1.visibility = View.GONE
                    tvSend.visibility = View.VISIBLE
                    clRight.visibility = View.VISIBLE
                    ivHuk.visibility = View.VISIBLE
                }

                if (document) {
                    ivImageSender.visibility = View.GONE
                    tvSend.visibility = View.GONE
                    clRight.visibility = View.GONE
                    ivHuk.visibility = View.GONE
                    ivPdf.visibility = View.VISIBLE
                    ivPdf1.visibility = View.VISIBLE


                    Glide.with(ivPdf1.context).load(item.content)
                        .placeholder(R.drawable.progress_animation_small)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivPdf1)

                    loadPdfThumbnail(
                        ivPdf.context,
                        Constants.BASE_URL_IMAGE + item.content.toString(),
                        ivPdf,
                        ivPdf1
                    )

                }


            }
        } else {
            (holder as ConversationLeftViewHolder).binding.apply {
                tvReceiver.text = item.content
                Glide.with(ivImageReceiver.context).load(Constants.BASE_URL_IMAGE + item.content)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(ivImageReceiver)
                val isText = item.contentType?.contains("text") == true
                val image = item.contentType?.contains("file") == true
                val document = item.contentType?.contains("document") == true

                ivImageReceiver.visibility = if (isText) View.GONE else View.VISIBLE
                tvReceiver.visibility = if (isText) View.VISIBLE else View.GONE
                ivHuk.visibility = if (isText) View.VISIBLE else View.GONE


                if (image) {
                    ivImageReceiver.visibility = View.VISIBLE
                    ivHuk.visibility = View.GONE
                    tvReceiver.visibility = View.GONE
                    clLeft.visibility = View.GONE
                    ivPdf1.visibility = View.GONE
                    ivPdf.visibility = View.GONE
                }

                if (isText) {
                    ivImageReceiver.visibility = View.GONE
                    ivPdf.visibility = View.GONE
                    tvReceiver.visibility = View.VISIBLE
                    ivPdf1.visibility = View.GONE
                    clLeft.visibility = View.VISIBLE
                    ivHuk.visibility = View.VISIBLE
                }

                if (document) {
                    ivImageReceiver.visibility = View.GONE
                    tvReceiver.visibility = View.GONE
                    clLeft.visibility = View.GONE
                    ivHuk.visibility = View.GONE
                    ivPdf.visibility = View.VISIBLE
                    ivPdf1.visibility = View.VISIBLE
                    loadPdfThumbnail(
                        ivPdf.context,
                        Constants.BASE_URL_IMAGE + item.content.toString(),
                        ivPdf,
                        ivPdf1
                    )
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val item = listItem[position]
        return if (Constants.userId == item.sender?._id) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVE
        }
    }


    fun loadPdfThumbnail(
        context: Context,
        pdfUrl: String,
        imageView: ImageView,
        imageView1: ImageView
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(pdfUrl)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val tempFile = File.createTempFile("temp_pdf", ".pdf", context.cacheDir)
                inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val fileDescriptor =
                    ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()
                fileDescriptor.close()

                withContext(Dispatchers.Main) {
                    Glide.with(imageView.context).load(bitmap)
                        .placeholder(R.drawable.progress_animation_small)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
                    //imageView.setImageBitmap(bitmap)
                    imageView.visibility = View.VISIBLE
                    imageView1.visibility = View.GONE
                }

            } catch (e: Exception) {
                imageView.visibility = View.GONE
                imageView1.visibility = View.GONE
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    class ConversationRightViewHolder(itemView: ChatSenderItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    class ConversationLeftViewHolder(itemView: ReciverChatItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

}

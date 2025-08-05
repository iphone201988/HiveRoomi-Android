package com.tech.hive.ui.for_room_mate.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.HomeRoomTData
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView


class HomeSwipeAdapter(
    val context: Context,
    private var itemList: List<HomeRoomTData> = emptyList(),
) : BaseAdapter() {

    override fun getCount(): Int = itemList.size

    override fun getItem(position: Int): Any = itemList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun homeGetItemAt(position: Int): HomeRoomTData? {
        return if (position in itemList.indices) itemList[position] else null
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_rv_swipe_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = itemList[position]
        val imageUrls = item.images ?: emptyList()
        var currentImageIndex = 0

        // --- Text setup ---
        val price = item.price?.toString() ?: "0.0"
        val perMonth = context.getString(R.string.per_month)
        viewHolder.name.text = context.getString(R.string.price11, "$$price.0", perMonth)

        viewHolder.title.text = item.title ?: context.getString(R.string.no_title_found)
        val address = if (!item.address.isNullOrEmpty()) "📍 ${item.address}" else context.getString(
            R.string.no_address_found1
        )
        val roomType = if (!item.roomType.isNullOrEmpty()) "🛏 ${item.roomType}" else context.getString(
            R.string.not_found1
        )
        val roomMate = if (!item.roommates.isNullOrEmpty()) "👥 ${item.roommates.size} ${context.getString(R.string.roommates)}" else context.getString(
            R.string._0_roommates1
        )
        viewHolder.noSingle.text = "$address $roomType $roomMate"
        val smoking = if (item.smoke?.contains("yes") == true) context.getString(R.string.yes_smoking1) else context.getString(
            R.string.no_smoking1
        )
        val pets = if (item.pets == true) context.getString(R.string.pets_allowed1) else context.getString(
            R.string.no_pets1
        )
        viewHolder.noSmoking.text = "$smoking $pets"

        fun loadImage(index: Int) {
            if (index in imageUrls.indices) {
                Glide.with(viewHolder.personImage.context)
                    .load(Constants.BASE_URL_IMAGE + imageUrls[index])
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.home_dummy_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.personImage)
            }else{
                viewHolder.personImage.setImageResource(R.drawable.home_dummy_icon)
            }
        }

        if (imageUrls.isNotEmpty()) {
            loadImage(currentImageIndex)
        }

        viewHolder.storiesProgressView.setStoriesCount(imageUrls.size)
        viewHolder.storiesProgressView.setStoryDuration(3000L)

        viewHolder.storiesProgressView.setStoriesListener(object : StoriesProgressView.StoriesListener {
            override fun onNext() {
                currentImageIndex++
                if (currentImageIndex < imageUrls.size) {
                    loadImage(currentImageIndex)
                }
            }

            override fun onPrev() {
                currentImageIndex = (currentImageIndex - 1).coerceAtLeast(0)
                loadImage(currentImageIndex)
            }

            override fun onComplete() {

            }
        })



        viewHolder.next.setOnClickListener {
            if (currentImageIndex < imageUrls.size - 1) {
                currentImageIndex++
                loadImage(currentImageIndex)

                viewHolder.storiesProgressView.destroy()
                viewHolder.storiesProgressView.setStoriesCount(imageUrls.size)
                viewHolder.storiesProgressView.setStoryDuration(3000L)
                viewHolder.storiesProgressView.startStories(currentImageIndex)
            }
        }

        viewHolder.previous.setOnClickListener {
            if (currentImageIndex > 0) {
                currentImageIndex--
                loadImage(currentImageIndex)

                viewHolder.storiesProgressView.destroy()
                viewHolder.storiesProgressView.setStoriesCount(imageUrls.size)
                viewHolder.storiesProgressView.setStoryDuration(3000L)
                viewHolder.storiesProgressView.startStories(currentImageIndex)
            }
        }


        if (imageUrls.isNotEmpty()) {
            viewHolder.storiesProgressView.startStories(currentImageIndex)
        }

        viewHolder.ivSend.setOnClickListener {
            val intent = Intent(context, SecondMatchActivity::class.java)
            intent.putExtra("profileIdSecond", item._id)
            context.startActivity(intent)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val noSmoking: AppCompatTextView = view.findViewById(R.id.tvTitle2)
        val noSingle: AppCompatTextView = view.findViewById(R.id.tvTitle1)
        val name: AppCompatTextView = view.findViewById(R.id.tvName)
        val title: AppCompatTextView = view.findViewById(R.id.tvTitle)
        val personImage: AppCompatImageView = view.findViewById(R.id.ivPerson)
        val ivSend: AppCompatImageView = view.findViewById(R.id.ivSend)
        val previous: View = view.findViewById(R.id.reverse)
        val next: View = view.findViewById(R.id.skip)
        var storiesProgressView: StoriesProgressView = view.findViewById(R.id.storiesProgressView)
        val ivCardView: CardView = view.findViewById(R.id.ivCardView)
    }

}
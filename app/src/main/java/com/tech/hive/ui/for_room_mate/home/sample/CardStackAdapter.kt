package com.tech.hive.ui.for_room_mate.home.sample

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.ui.for_room_mate.home.CardItem
import com.tech.hive.ui.for_room_mate.home.MatchedProfileActivity
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView


class CardStackAdapter(
    private var items: List<CardItem> = emptyList(),
    private val context: Context,
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>(), StoriesProgressView.StoriesListener {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CardItem.HomeRoom -> {
                holder.clFirst.visibility = View.VISIBLE
                holder.clSecond.visibility = View.GONE
                holder.tvTitle.visibility = View.INVISIBLE
                holder.storiesProgressView.visibility = View.GONE
                holder.reverse.visibility = View.GONE
                holder.skip.visibility = View.GONE

                holder.name.text = "${item.user.name} ${item.user.ageRange}"
                holder.profession.text = item.user.profession
                holder.money.text = item.user.ageRange
                holder.parties.text = item.user.campus ?: "No Campus Info"

                Glide.with(holder.image).load(Constants.BASE_URL_IMAGE + item.user.profileImage)
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.home_dummy_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image)



                holder.ivSend.setOnClickListener {
                    val intent = Intent(context, MatchedProfileActivity::class.java)
                    intent.putExtra("profileIdFirst", item.user._id)
                    context.startActivity(intent)
                }

                holder.ivSend.setOnClickListener {
                    val intent = Intent(context, MatchedProfileActivity::class.java)
                    intent.putExtra("profileIdFirst", item.user._id)
                    context.startActivity(intent)
                }
            }

            is CardItem.RoomListing -> {
                holder.clFirst.visibility = View.GONE
                holder.clSecond.visibility = View.VISIBLE
                holder.tvTitle.visibility = View.VISIBLE
                val imageCount = item.room.images?.size ?: 0
                holder.storiesProgressView.setStoriesCount(imageCount)
                holder.storiesProgressView.setStoryDuration(3000L)
                holder.storiesProgressView.setStoriesListener(object : StoriesProgressView.StoriesListener {
                    override fun onNext() {}
                    override fun onPrev() {}
                    override fun onComplete() {}
                })

                if (imageCount > 0) {
                    holder.storiesProgressView.startStories()
                }

                val price = item.room.price?.toString() ?: "0.0"
                holder.name.text = "Price: $$price"
                holder.tvTitle.text = item.room.title ?: "No Title Found"
                val address =
                    if (item.room.address?.isNotEmpty() == true) "📍 ${item.room.address}" else "📍 No Address found"
                val roomType =
                    if (item.room.roomType?.isNotEmpty() == true) "🛏 ${item.room.roomType}" else "🛏 Not found"

                val roomMate = if (item.room.roommates?.isNotEmpty() == true) {
                    "\uD83D\uDC65 ${item.room.roommates.size} Roommates"
                } else {
                    "\uD83D\uDC65 0 Roommates"
                }
                holder.tvTitle1.text = "$address $roomType $roomMate"


                val smoking =
                    if (item.room.smoke?.contains("yes") == true) "\uD83D\uDEAD Yes Smoking" else "\uD83D\uDEAD No Smoking"
                val pets =
                    if (item.room.pets == true) "\uD83D\uDC36 Pets Allowed" else "\uD83D\uDC36 No Pets"
                holder.tvTitle2.text = "$smoking $pets"

                val imageUrl = item.room.images?.firstOrNull()

                Glide.with(holder.image).load(Constants.BASE_URL_IMAGE + (imageUrl ?: ""))
                    .placeholder(R.drawable.progress_animation_small)
                    .error(R.drawable.home_dummy_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image)

                holder.ivSend.setOnClickListener {
                    val intent = Intent(context, SecondMatchActivity::class.java)
                    intent.putExtra("profileIdSecond", item.room._id)
                    context.startActivity(intent)
                }

                holder.reverse.setOnClickListener {
                    holder.storiesProgressView.reverse()
                }
                holder.skip.setOnClickListener {
                    holder.storiesProgressView.skip()
                }
            }
        }
    }


    override fun getItemCount(): Int = items.size


    fun setItems(items: List<CardItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getItems(): List<CardItem> = items


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: AppCompatTextView = view.findViewById(R.id.tvName)
        var profession: AppCompatTextView = view.findViewById(R.id.tvProfession)
        var money: AppCompatTextView = view.findViewById(R.id.tvMoney)
        var parties: AppCompatTextView = view.findViewById(R.id.tvParties)
        var image: AppCompatImageView = view.findViewById(R.id.ivPerson)
        var ivSend: AppCompatImageView = view.findViewById(R.id.ivSend)
        var clSecond: ConstraintLayout = view.findViewById(R.id.clSecond)
        var clFirst: ConstraintLayout = view.findViewById(R.id.clFirst)
        var tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)
        var tvTitle2: AppCompatTextView = view.findViewById(R.id.tvTitle2)
        var tvTitle1: AppCompatTextView = view.findViewById(R.id.tvTitle1)
        var reverse: View = view.findViewById(R.id.reverse)
        var skip: View = view.findViewById(R.id.skip)
        var storiesProgressView: StoriesProgressView = view.findViewById(R.id.storiesProgressView)

    }

    override fun onNext() {
    }

    override fun onPrev() {

    }

    override fun onComplete() {

    }

}

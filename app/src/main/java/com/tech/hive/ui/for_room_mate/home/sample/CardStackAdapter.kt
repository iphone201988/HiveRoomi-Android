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
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.ui.for_room_mate.home.MatchedProfileActivity
import com.tech.hive.ui.for_room_mate.home.second.SecondMatchActivity
import com.tech.hive.ui.for_room_mate.home.second.storiesprogressview.StoriesProgressView

class CardStackAdapter(
    private var spots: List<Spot> = emptyList(), private val context: Context,
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>(), StoriesProgressView.StoriesListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        //holder.name.text = "${spot.id}. ${spot.name}"
        if (Constants.userType == 1) {
            holder.clFirst.visibility = View.VISIBLE
            holder.clSecond.visibility = View.GONE
            holder.tvTitle.visibility = View.INVISIBLE
            holder.storiesProgressView.visibility = View.GONE
            holder.reverse.visibility = View.GONE
            holder.skip.visibility = View.GONE
        } else {
            holder.clFirst.visibility = View.GONE
            holder.clSecond.visibility = View.VISIBLE
            holder.tvTitle.visibility = View.VISIBLE
            holder.storiesProgressView.setStoriesCount(spots.size)
            holder.storiesProgressView.setStoryDuration(3000L)
            holder.storiesProgressView.setStoriesListener(this)
            holder.storiesProgressView.startStories(0)
        }
        holder.name.text = spot.name
        holder.profession.text = spot.profession
        holder.money.text = spot.money
        holder.parties.text = spot.parties
        holder.image.setImageResource(spot.url)


//        Glide.with(holder.image)
//                .load(spot.url)
//                .into(holder.image)

        holder.ivSend.setOnClickListener {
            if (Constants.userType == 1) {
                val intent = Intent(context, MatchedProfileActivity::class.java)
                intent.putExtra("matchType", "before")
                context.startActivity(intent)
            } else if (Constants.userType == 2) {
                val intent = Intent(context, SecondMatchActivity::class.java)
                intent.putExtra("secondMatchType", "before")
                context.startActivity(intent)
            }
        }

        holder.reverse.setOnClickListener {
            holder.storiesProgressView.reverse()
        }
        holder.skip.setOnClickListener {
            holder.storiesProgressView.skip()
        }
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<Spot>) {
        this.spots = spots
    }

    fun getSpots(): List<Spot> {
        return spots
    }

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

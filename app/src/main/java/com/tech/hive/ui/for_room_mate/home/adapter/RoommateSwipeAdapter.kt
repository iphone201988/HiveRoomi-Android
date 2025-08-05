package com.tech.hive.ui.for_room_mate.home.adapter

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.HomeApiData
import com.tech.hive.ui.for_room_mate.home.MatchedProfileActivity


class RoommateSwipeAdapter(
    val context: Context,
    private var itemList: List<HomeApiData> = emptyList(),
) : BaseAdapter()
{

    override fun getCount(): Int = itemList.size

    override fun getItem(position: Int): Any = itemList[position]

    override fun getItemId(position: Int): Long = position.toLong()



    fun getItemAt(position: Int): HomeApiData? {
        return if (position in itemList.indices) itemList[position] else null
    }


    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spot, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val item = itemList[position]

        viewHolder.clFirst.visibility = View.VISIBLE
        viewHolder.name.text = "${item.name} ${item.ageRange}"
        viewHolder.profession.text = item.profession
        viewHolder.money.text = item.ageRange
        viewHolder.parties.text = item.campus ?: context.getString(R.string.no_campus_info)


        Glide.with(viewHolder.image)
            .load(Constants.BASE_URL_IMAGE + item.profileImage)
            .placeholder(R.drawable.progress_animation_small)
            .error(R.drawable.home_dummy_icon)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(viewHolder.image)

        viewHolder.ivSend.setOnClickListener {
            val intent = Intent(context, MatchedProfileActivity::class.java)
            intent.putExtra("profileIdFirst", item._id)
            context.startActivity(intent)
        }

        return view
    }



    private class ViewHolder(view: View) {
        val name: AppCompatTextView = view.findViewById(R.id.tvName)
        val profession: AppCompatTextView = view.findViewById(R.id.tvProfession)
        val money: AppCompatTextView = view.findViewById(R.id.tvMoney)
        val parties: AppCompatTextView = view.findViewById(R.id.tvParties)
        val image: AppCompatImageView = view.findViewById(R.id.ivPerson)
        val ivSend: AppCompatImageView = view.findViewById(R.id.ivSend)
        val clFirst: ConstraintLayout = view.findViewById(R.id.clFirst)
        val ivCardView: CardView = view.findViewById(R.id.ivCardView)
    }

}

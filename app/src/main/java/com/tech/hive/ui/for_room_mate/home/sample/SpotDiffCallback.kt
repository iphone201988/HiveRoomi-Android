package com.tech.hive.ui.for_room_mate.home.sample

import androidx.recyclerview.widget.DiffUtil
import com.tech.hive.data.model.HomeApiData
import com.tech.hive.ui.for_room_mate.home.CardItem
import com.tech.hive.ui.for_room_mate.home.sample.Spot

/*
class SpotDiffCallback(
    private val old: List<HomeApiData>,
    private val new: List<HomeApiData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition]._id == new[newPosition]._id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
*/

class SpotDiffCallback(
    private val oldList: List<CardItem>,
    private val newList: List<CardItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return when {
            old is CardItem.HomeRoom && new is CardItem.HomeRoom ->
                old.user._id == new.user._id

            old is CardItem.RoomListing && new is CardItem.RoomListing ->
                old.room._id == new.room._id

            else -> false // Different types
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return old == new
    }
}


package com.tech.hive.ui.for_room_mate.home

import com.tech.hive.data.model.HomeApiData
import com.tech.hive.data.model.HomeRoomTData
import com.tech.hive.data.model.HomeRoomType

sealed class CardItem {
    data class HomeRoom(val user: HomeApiData) : CardItem()
    data class RoomListing(val room: HomeRoomTData) : CardItem()
}